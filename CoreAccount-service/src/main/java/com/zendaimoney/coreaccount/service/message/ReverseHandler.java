package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.ReverseVo;
import com.zendaimoney.coreaccount.service.message.reverse.Reverseable;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springside.modules.utils.SpringContextHolder;

import javax.inject.Named;

import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_FAIL;

/**
 * 冲正
 * 
 * @author larry
 * 
 */
@Named
public class ReverseHandler extends MessageHandler {

	@Override
	public String handle(Datagram datagram) {
		ReverseVo reverseVo = (ReverseVo) datagram.getDatagramBody();
		Reverseable reverseable = lookup(reverseVo.getMessageCode());
		return reverseable.process(datagram);
	}

	private final Reverseable lookup(String beanID) {
		try {
			return SpringContextHolder.getBean(beanID, Reverseable.class);
		} catch (NoSuchBeanDefinitionException e) {
			throw new BusinessException(PROCESS_STATUS_FAIL, PropertiesReader.readAsString("reverse.impl.not.found"));
		} catch (Throwable e) {
			throw new BusinessException(PROCESS_STATUS_FAIL, e.getCause().getMessage());
		}
	}
}
