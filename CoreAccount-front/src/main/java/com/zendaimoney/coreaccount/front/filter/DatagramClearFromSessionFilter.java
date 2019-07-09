package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.exception.BusinessException;

import javax.inject.Named;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

/**
 * 清除session,同时记录客户端ip
 * 
 * @author jice.gao
 * 
 */
@Named
public class DatagramClearFromSessionFilter extends DatagramFilter {

	@Override
	public void doFilter(Object datagram) throws BusinessException {
		CoreAccountFrontSession.clear();
		try {
			CoreAccountFrontSession.put(Constant.CLIENT_HOST_IN_SESSION, RemoteServer.getClientHost());
		} catch (ServerNotActiveException e) {
			logger.error("", e);
			throw new BusinessException(e);
		}
	}

}
