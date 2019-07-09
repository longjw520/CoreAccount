package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.service.MessageLogService;
import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.util.PropertiesReader;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.exception.BusinessException;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 根据请求发送系统和报文序列号过滤重复请求
 * 
 * @author jice.gao
 * 
 */
@Named
public class DatagramDuplicateFilter extends DatagramFilter {

	@Inject
	private MessageLogService messageLogService;

	@Override
	public void doFilter(Object datagram) throws BusinessException {
		Datagram dg = (Datagram) CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION);
		DatagramHeader header = dg.getDatagramHeader();
		if (messageLogService.existInDb(header.getSenderSystemCode(), header.getMessageSequence())) {
			throw new BusinessException(Constant.REQUEST_STATUS_REPEATED, PropertiesReader.readAsString("request_repeat"));
		}
	}

}