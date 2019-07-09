package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.entity.Client;
import com.zendaimoney.coreaccount.front.filter.auth.User;
import com.zendaimoney.coreaccount.front.service.ClientService;
import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.util.PropertiesReader;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.exception.BusinessException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

/**
 * 请求权限检查
 * 
 * @author Jianlong Ma
 * 
 */
@Named
public class DatagramAuthFilter extends DatagramFilter {

	private final Set<User> cacheUser = new HashSet<User>();
	@Inject
	private ClientService clientService;

	@Override
	public void doFilter(Object datagram) throws BusinessException {
		Datagram dg = (Datagram) CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION);
		DatagramHeader datagramHeader = dg.getDatagramHeader();
		if (null != datagramHeader) {
			String userName = datagramHeader.getUserName();
			String pwd = datagramHeader.getPassword();
			String ip = CoreAccountFrontSession.get(Constant.CLIENT_HOST_IN_SESSION).toString();
			boolean queryAction;// 标识是否为查询操作
			if (cacheUser.contains(new User(userName, ip, pwd, pwd, queryAction = datagramHeader.getMessageCode().startsWith(Constant.MESSAGE_BUSINESS_TYPE_QUERY_TYPE))))
				return;
			Client client = clientService.getClient(userName);

			if (null == client)
				throw new BusinessException(Constant.REQUEST_STATUS_USER_NOT_EXIST, PropertiesReader.readAsString("user_not_exist"));
			String dbPassword = queryAction ? client.getQueryPwd() : client.getOperationPwd();
			String dbIp = client.getIpInfo();

			if (!pwd.equals(dbPassword))
				throw new BusinessException(Constant.REQUEST_STATUS_PWD_ERROR, PropertiesReader.readAsString("password_error"));
			if (!ip.equals(dbIp))
				throw new BusinessException(Constant.REQUEST_STATUS_IP_ERROR, PropertiesReader.readAsString("ip_error"));
			cacheUser.add(new User(userName, dbIp, client.getQueryPwd(), client.getOperationPwd(), queryAction));
		}
	}
}
