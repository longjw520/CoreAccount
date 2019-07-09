package com.zendaimoney.coreaccount.front.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.hibernate.NonUniqueResultException;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.SpringTransactionalTests;

import com.zendaimoney.coreaccount.front.entity.Client;


@ContextConfiguration(locations = "/applicationContext-test.xml")
public class ClientServiceTest extends SpringTransactionalTests {
	
	@Inject
	private ClientService clientService;
	
	@Test
	public void testGetClient() {
		Client client = clientService.getClient("nick");
		assertNotNull(client);
	}
	
	@Test
	public void testClient_null() {
		Client client = clientService.getClient("none");
		assertNull(client);
	}
	
	@Test(expected = NonUniqueResultException.class)
	public void testClient_exception() {
		clientService.getClient("JOEY");
	}

	@Override
	protected String getDataFilePath() {
		return "/data/clientDao.xml";
	}
}
