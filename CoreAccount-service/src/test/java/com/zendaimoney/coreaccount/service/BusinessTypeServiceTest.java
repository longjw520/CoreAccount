package com.zendaimoney.coreaccount.service;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.zendaimoney.coreaccount.entity.BusinessType;

@ContextConfiguration(locations = {"/applicationContext-test.xml"})
public class BusinessTypeServiceTest extends SpringTxTestCase{
	
	@Inject
	private BusinessTypeService businessTypeService;
	
	@Before
	public void before() {
		try {
			Fixtures.reloadAllTable(dataSource, "data/BusinessTypeDao.xml");
		} catch (Exception e) {
		}
	}
	@Test
	public void testGetBusinessTypeBy() {
		for (int i = 0; i< 3; i++) {
			exec();
		}
	}
	
	private void exec() {
		BusinessType businessType = businessTypeService.getBusinessTypeBy("111");
		Assert.assertEquals("admin", businessType.getName());
		businessType = businessTypeService.getBusinessTypeBy("222");
		Assert.assertEquals("joey", businessType.getName());
	}
}
