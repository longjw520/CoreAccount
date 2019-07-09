package com.zendaimoney.coreaccount.dao;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringTxTestCase;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class WorkFlowDaoTest extends SpringTxTestCase{

	@Inject
	WorkFlowDao workFlowDao;
	
	@Test
	public void testWorkFlowSave() {
		workFlowDao.workFlowSave(1L, "3", 4l, "4", null, "5", 6L, "7", 8L);
		Assert.assertNotNull(workFlowDao.findBy("businessId", 1L));
	}

}
