package com.zendaimoney.coreaccount.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.zendaimoney.coreaccount.entity.Customer;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class CustomerDaoTest extends SpringTxTestCase {
	@Inject
	private CustomerDao customerDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/customerDao.xml");
	}

	@Test
	public void testGetCustomer() {
		String cardId = "689854545";
		String cardType = "01";
		Customer customer = customerDao.getCustomer(cardId, cardType);
		assertNotNull(customer);
		assertEquals("0200000000000001", customer.getTotalAcct());
	}

	@Test
	public void testGetCustomer_none() {
		String cardId = "689854545";
		String cardType = "09";
		Customer customer = customerDao.getCustomer(cardId, cardType);
		assertNull(customer);
	}

	@Test
	public void testGetRowCountBy() {
		String organ = "01";
		int count = customerDao.getRowCountBy(organ);
		assertEquals(1, count);
	}

	@Test
	public void testGetRowCountBy_none() {
		String organ = "xxx";
		int count = customerDao.getRowCountBy(organ);
		assertEquals(0, count);
	}

	@Test
	public void testGetMaxFlowBy() {
		String organ = "04000000";
		String result = customerDao.getMaxFlowBy(organ);
		assertEquals("00000001", result);
	}

	@Test
	public void testGetMaxFlowBy_none() {
		String organ = "A04000000";
		String result = customerDao.getMaxFlowBy(organ);
		assertEquals("0", result);
	}

	@Test
	public void query() {
		assertEquals(5, customerDao.getAll().size());
	}

}
