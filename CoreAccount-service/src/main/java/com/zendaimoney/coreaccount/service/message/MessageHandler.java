package com.zendaimoney.coreaccount.service.message;

import org.apache.log4j.Logger;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;

public abstract class MessageHandler {
	protected Logger logger = Logger.getLogger(getClass());

	public abstract String handle(Datagram datagram);

	/**
	 * 将源报文体的公共字段设置给返回报文体
	 * 
	 * @param DatagramBody
	 *            ,DatagramBody
	 */
	protected void setResultVO(DatagramBody sourceDatagramBody, DatagramBody retrunDatagramBody) {
		retrunDatagramBody.setOperator(sourceDatagramBody.getOperator());
		retrunDatagramBody.setOrgan(sourceDatagramBody.getOrgan());
		retrunDatagramBody.setAuthTeller(sourceDatagramBody.getAuthTeller());
		retrunDatagramBody.setOperateCode(Constants.PROCESS_STATUS_OK);
		retrunDatagramBody.setMemo(null);
	}
}
