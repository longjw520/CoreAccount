package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.BusinessType;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.utils.DateUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Transactional
public class BusinessInfoService {

	@Inject
	private BusinessInfoDao businessInfoDao;

	@Inject
	private BusinessTypeService businessTypeService;

	/**
	 * 保存业务信息
	 * 
	 * @param datagram
	 */
	public void saveBusinessInfo(Datagram datagram) {
		BusinessInfo businessInfo = new BusinessInfo();
		DatagramBody datagramBody = datagram.getDatagramBody();
		DatagramHeader datagramHeader = datagram.getDatagramHeader();
		businessInfo.setMessageSequence(datagramHeader.getMessageSequence());
		String messageCode = datagramHeader.getMessageCode();
		BusinessType businessType = businessTypeService.getBusinessTypeBy(messageCode);
		businessInfo.setBusinessTypeId(businessType.getId());
		businessInfo.setOperator(datagramBody.getOperator());// 柜员号
		businessInfo.setAuthTeller(datagramBody.getAuthTeller());// 授权柜员号
		businessInfo.setOrgan(datagramBody.getOrgan());// 营业网点
		businessInfo.setMemo(datagramBody.getMemo());
		businessInfo.setRequestSystem(datagramHeader.getSenderSystemCode());
		/** 报文头中的实际交易时间 */
		businessInfo.setActualTradeTime(DateUtils.nullSafeParseDate(datagramHeader.getActualTradeTime(), Constants.DATE_TIME_FORMAT));
		businessInfoDao.save(businessInfo);
		datagramHeader.setBusinessId(businessInfo.getId());
		datagramHeader.setBusinessTypeId(businessType.getId());
	}

	public long findByMessageSequence(String messageSequence) {
		return businessInfoDao.findUniqueBy("messageSequence", messageSequence).getId();
	}
}
