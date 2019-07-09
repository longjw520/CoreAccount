package com.zendaimoney.coreaccount.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.BusinessType;
import com.zendaimoney.coreaccount.rmi.vo.CustomerVO;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(MockitoJUnitRunner.class)
public class BusinessInfoServiceTest {
	@InjectMocks
	private BusinessInfoService businessInfoService;

	@Mock
	private BusinessInfoDao businessInfoDao;
	@Mock
	private BusinessTypeService businessTypeService;

	@Test
	public void testSaveBusinessInfo() {
		BusinessInfo businessInfo = new BusinessInfo();
		BusinessType businessType = new BusinessType();
		businessType.setId(11L);
		Datagram datagram = (Datagram) JsonHelper.toBean(BufferedInputFile.read("data/json/openAccount.json"), CustomerVO.class);
		businessInfo.setMessageSequence("010001");
		Mockito.when(businessTypeService.getBusinessTypeBy("010001")).thenReturn(businessType);
		businessInfo.setBusinessTypeId(businessType.getId());
		Mockito.doNothing().when(businessInfoDao).save(businessInfo);
		businessInfoService.saveBusinessInfo(datagram);
		Assert.assertEquals(11, businessInfo.getBusinessTypeId().intValue());
	}
}
