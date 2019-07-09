package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.exception.BusinessException;

import javax.inject.Named;

/**
 * 记录日志
 * 
 * @author binliu
 * 
 */
@Named
public class DatagramLogFilter extends DatagramFilter {

	@Override
	public void doFilter(Object datagram) throws BusinessException {
		logger.info(CoreAccountFrontSession.get(Constant.CLIENT_HOST_IN_SESSION).toString() + "\t请求报文： " + datagram.toString());
	}

}
