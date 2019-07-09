package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.ExpiryInterestVo;
import com.zendaimoney.coreaccount.service.ExpiryInterestService;
import com.zendaimoney.coreaccount.util.JsonHelper;

import javax.inject.Inject;
import javax.inject.Named;

import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_OK;

/**
 * Copyright (c) 2014 ZENDAI. All Rights Reserved. This software is published
 * under the terms of the ZENDAI Software
 * 结息（回款） 020046
 *
 * @author chen.hao
 * @mail haoc@zendaimoney.com
 * @date: 2014/12/16 20:56
 */
@Named
public class ExpiryInterestHandler extends MessageHandler {

	@Inject
	private ExpiryInterestService expiryInterestService;

	@Override
	public String handle(Datagram datagram) {
		ExpiryInterestVo expiryInterestVo = (ExpiryInterestVo) datagram.getDatagramBody();
		expiryInterestService.expiry(expiryInterestVo, datagram.getDatagramHeader().getBusinessId());
		datagram.getDatagramBody().setOperateCode(PROCESS_STATUS_OK);
		return JsonHelper.toJson(datagram);
	}

}
