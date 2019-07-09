package com.zendaimoney.coreaccount.web.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zendaimoney.coreaccount.rmi.IRmiService;
import com.zendaimoney.coreaccount.tool.Encry;

@Controller
@RequestMapping("/")
public class RmiController {

	@RequestMapping("")
	public String index() {
		return "index";
	}

	@RequestMapping("openAccount")
	public String openAccount(String className, Model model) throws ClassNotFoundException {
		List<String[]> fields = new ArrayList<String[]>();
		@SuppressWarnings("rawtypes")
		Class clazz = this.getClass().getClassLoader().loadClass(className);
		Field[] fields2 = clazz.getDeclaredFields();
		for (Field f : fields2) {
			String name = f.getName();
			if (!name.startsWith("serialVersionUID")) {
				String size = "";
				fields.add(new String[] { name, size });
			}
		}
		model.addAttribute("fields", fields);
		return "business/register";
	}

	@RequestMapping("sendDatagram")
	@ResponseBody
	public String sendDatagram(String url, String content, Model model) {
		RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
		rmiProxyFactoryBean.setServiceUrl(url);
		rmiProxyFactoryBean.setServiceInterface(IRmiService.class);
		rmiProxyFactoryBean.afterPropertiesSet();
		IRmiService rmiService = (IRmiService) rmiProxyFactoryBean.getObject();
		String result = rmiService.getResult(content);

		// model.addAttribute("result", result);
		return result;
	}

//	@RequestMapping("sendDatagram")
//	@ResponseBody
//	public String sendDatagram(String url, String content, Model model) {
//		RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
//		rmiProxyFactoryBean.setServiceUrl(url);
//		rmiProxyFactoryBean.setServiceInterface(IRmiService.class);
//		rmiProxyFactoryBean.afterPropertiesSet();
//		IRmiService rmiService = (IRmiService) rmiProxyFactoryBean.getObject();
//		new ClientMain().process(1000, content, rmiService);
//		// model.addAttribute("result", result);
//		return "OK";
//	}
	
	@RequestMapping("jsonFormat")
	@ResponseBody
	public String jsonFormat(String jsonStr, Model model) {
		String result = "";

		if (StringUtils.isBlank(jsonStr) || !jsonStr.trim().startsWith("{")) {
			result = "<font color=\"red\">请输入正确的JSON字符串！</font>";
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
			objectMapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			try {
				Object o = objectMapper.readValue(jsonStr, Object.class);
				result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
				model.addAttribute("result", result);
			} catch (Exception e) {
				result = "<font color=\"red\">JSON字符串解析出错，请输入正确的JSON字符串！</font>\n" + e.getMessage();
			}
		}
		// model.addAttribute("jsonStr", jsonStr);
		// model.addAttribute("result", result);
		return result;
	}

	@RequestMapping("encry")
	@ResponseBody
	public String encry(@RequestParam String plaintext, Model model) {
		String data;
		if (StringUtils.isBlank(plaintext)) {
			data = "<font color=\"red\">请输入需要加密的明文！</font>";
			return data;
		}
		data = Encry.encode(plaintext);
		model.addAttribute("result", data);
		return data;
	}

	@ExceptionHandler
	@ResponseBody
	public void exceptionHandler(Exception e, HttpServletResponse response) throws IOException {
		e.printStackTrace(response.getWriter());
	}
}
