package com.zendaimoney.coreaccount.service.message;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springside.modules.utils.Reflections;

import com.zendaimoney.coreaccount.dao.CustomerDao;
import com.zendaimoney.coreaccount.service.CustomerService;

public class CreateCustomerMessageHandlerTest {
	private CustomerService customerService;

	@Mock
	private CustomerDao customerDao;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		customerService = new CustomerService();
		Reflections.setFieldValue(customerService, "customerDao", customerDao);
	}

	@Test
	public void genMainAccount() {
	
	}
}
