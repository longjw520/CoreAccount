package com.zendaimoney.coreaccount.front.filter;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import com.zendaimoney.coreaccount.rmi.utils.Json;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.exception.BusinessException;
import net.sf.cglib.core.ReflectUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 对报文中字符串格式的日期时间进行校验
 * 
 * @author liubin
 * 
 */
@Named
public class DatagramDateFilter extends DatagramFilter {
	@Resource(name = "businessVos")
	private Map<String, Class<? extends DatagramBody>> businessVos;
	@Inject
	private Json json;
	private final Map<Class<? extends DatagramBody>, PropertyDescriptor[]> _cache = Maps.newHashMap();
	private PropertyDescriptor[] headProps = getBeanProperties(DatagramHeader.class);
	private Table<Class<?>, String, Boolean> _cache_ = HashBasedTable.create();

	public DatagramDateFilter() {
		scan();
	}

	@Override
	public void doFilter(Object datagram) throws BusinessException {
		Datagram msg = (Datagram) CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION);
		if (null == msg)
			msg = json.toBean(datagram.toString());
		DatagramHeader datagramHeader = msg.getDatagramHeader();
		DatagramBody datagramBody = msg.getDatagramBody();
		Class<? extends DatagramBody> bodyClz = businessVos.get(datagramHeader.getMessageCode());
		PropertyDescriptor[] pds = checkCache(bodyClz);
		final SimpleDateFormat sdf = new SimpleDateFormat();
		Field tmp = null;
		String tmpPattern = null;
		Field f;
		Method getMethod;
		try {
			for (PropertyDescriptor pd : pds) {
				getMethod = pd.getReadMethod();
				f = getMethod.getDeclaringClass().getDeclaredField(pd.getName());
				tmp = f;
				String pattern = f.getAnnotation(DateTimeFormat.class).pattern();
				sdf.applyPattern(pattern);
				tmpPattern = pattern;
				Object date = null;
				if (_cache_.get(f.getDeclaringClass(), f.getName()))
					date = getMethod.invoke(datagramBody);
				else if (DatagramHeader.class == f.getDeclaringClass())
					date = getMethod.invoke(datagramHeader);
				else
					continue;
				if (f.isAnnotationPresent(NotBlank.class) || f.isAnnotationPresent(NotNull.class) || f.isAnnotationPresent(NotEmpty.class)) {
					sdf.parse((String) date);
					continue;
				}
				if (date != null)
					sdf.parse((String) date);
			}
		} catch (ParseException e) {
			throw new BusinessException(Constant.REQUEST_STATUS_MESSAGE_ERROR, tmp.getDeclaringClass().getName() + ":" + tmp.getName() + "格式有误(期望:)" + tmpPattern);
		} catch (Exception ignore) {
		}
	}

	/**
	 * 将vo的类型信息缓存起来
	 * 
	 * @param bodyClz
	 * @return
	 */
	private PropertyDescriptor[] checkCache(Class<? extends DatagramBody> bodyClz) {
		if (!_cache.containsKey(bodyClz)) {
			PropertyDescriptor[] bodyPds = getBeanProperties(bodyClz);
			PropertyDescriptor[] datagramProps = new PropertyDescriptor[headProps.length + bodyPds.length];
			System.arraycopy(headProps, 0, datagramProps, 0, headProps.length);
			System.arraycopy(bodyPds, 0, datagramProps, headProps.length, bodyPds.length);
			List<PropertyDescriptor> dateProps = new ArrayList<PropertyDescriptor>();
			Method getMethod;
			Field f;
			int size = 0;
			try {
				for (PropertyDescriptor pd : datagramProps) {
					if (pd.getPropertyType() == String.class) {
						getMethod = pd.getReadMethod();
						f = getMethod.getDeclaringClass().getDeclaredField(pd.getName());
						if (f.isAnnotationPresent(DateTimeFormat.class)) {
							dateProps.add(pd);
							++size;
						}
					}
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
			_cache.put(bodyClz, dateProps.toArray(new PropertyDescriptor[size]));
		}
		return _cache.get(bodyClz);
	}

	private PropertyDescriptor[] getBeanProperties(Class<?> clazz) {
		return ReflectUtils.getBeanGetters(clazz);
	}

	/**
	 * 判断源Class和目标Class是否有继承关系(或者类型一致)
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	private boolean isAssignFrom(Class<?> source, Class<?> target) {
		Class<?> nextClass = source;
		Set<Class<?>> result = new HashSet<Class<?>>();
		while (nextClass != Object.class) {
			result.add(nextClass);
			nextClass = nextClass.getSuperclass();
		}
		return result.contains(target);
	}

	/**
	 * 缓存Vo字段信息
	 */
	@SuppressWarnings("unchecked")
	private void scan() {
		logger.debug("Start scanning the package com.zendaimoney.coreaccount.rmi.vo");
		String jarFilePath = Datagram.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String pkgPath = Datagram.class.getPackage().getName().replaceAll("\\.", "/") + '/';
		File pkgDir = new File(jarFilePath + pkgPath);
		String fileNotFound = null;
		Class<?> clazz = Object.class;
		ZipFile zipFile = null;
		try {
			if (jarFilePath.contains("classes")) {// for dev env
				for (String fname : pkgDir.list()) {
					fileNotFound = pkgPath + fname;
					fname = fname.substring(0, fname.indexOf("."));
					clazz = Class.forName(pkgPath.replaceAll("/", ".") + fname);
					for (Field f : clazz.getDeclaredFields()) {
						if (f.isAnnotationPresent(DateTimeFormat.class)) {
							_cache_.put(clazz, f.getName(), isAssignFrom(clazz, DatagramBody.class));
							checkCache((Class<? extends DatagramBody>) clazz);
						}
					}
				}
				return;
			} // for prod env
			zipFile = new JarFile(jarFilePath);
			for (final Enumeration<? extends ZipEntry> enumeration = zipFile.entries(); enumeration.hasMoreElements();) {
				final ZipEntry zipEntry = enumeration.nextElement();
				String name = zipEntry.getName();
				fileNotFound = name;
				if (name.startsWith("com/zendaimoney/coreaccount/rmi/vo/") && !zipEntry.isDirectory()) {
					name = name.substring(0, name.indexOf("."));
					clazz = Class.forName(name.replaceAll("/", "."));
					for (Field f : clazz.getDeclaredFields()) {
						if (f.isAnnotationPresent(DateTimeFormat.class)) {
							_cache_.put(clazz, f.getName(), isAssignFrom(clazz, DatagramBody.class));
							checkCache((Class<? extends DatagramBody>) clazz);
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.print("can't find file : " + fileNotFound);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null)
				try {
					zipFile.close();
				} catch (IOException ignore) {
				}
		}

	}
}