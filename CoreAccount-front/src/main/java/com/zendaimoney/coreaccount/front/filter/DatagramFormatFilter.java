package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.utils.Json;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.exception.BusinessException;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 请求报文格式检查
 * 
 * @author Jianlong Ma
 * 
 */
@Named
public class DatagramFormatFilter extends DatagramFilter {
	@Inject
	private Json json;

	@Override
	public void doFilter(Object datagram) throws BusinessException {
		try {
			Datagram dg = json.toBean(datagram.toString());
			CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, dg);
		} catch (Exception e) {
			throw new BusinessException(Constant.REQUEST_STATUS_MESSAGE_ERROR, e.getMessage());
		}
	}

}
