package com.zendaimoney.coreaccount.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.CustomerDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.rmi.vo.CustomerVO;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;

@RunWith(PowerMockRunner.class)
public class CustomerServiceTest {

	@InjectMocks
	private CustomerService customerService;

	@Mock
	private CustomerDao customerDao;

	@Mock
	private SequenceDao sequenceDao;

	@Test
	public void testGenMainAccount() {
		Mockito.when(customerDao.getMaxFlowBy("00030001")).thenReturn("00000001");
		String result = customerService.genMainAccount("00030001");
		Assert.assertEquals("0003000100000002", result);
	}

	@Test
	public void testFGenMainAccount_none() {
		Mockito.when(customerDao.getMaxFlowBy("00030001")).thenReturn("0");
		String result = customerService.genMainAccount("00030001");
		Assert.assertEquals("0003000100000001", result);
	}

	@Test
	public void testGetCustomerBy() {
		String cardId = "310102198204071220";
		String cardType = "01";
		Mockito.when(customerDao.getCustomer("310102198204071220", "01")).thenReturn(new Customer());
		assertThat(customerService.getCustomerBy(cardId, cardType), notNullValue());
	}

	@Test(expected = BusinessException.class)
	public void testOpenAccount_fail() {
		Datagram datagram = new Datagram();
		CustomerVO vo = new CustomerVO();
		vo.setCardId("11");
		vo.setCardType("01");
		datagram.setDatagramBody(vo);
		Mockito.when(customerService.getCustomerBy(vo.getCardId(), vo.getCardType())).thenReturn(new Customer());
		try {
			customerService.openAccount(datagram);
		} catch (BusinessException e) {
			assertThat(Constants.PROCESS_STATUS_FAIL, equalTo(e.getCode()));
			assertThat(PropertiesReader.readAsString("account.opened"), equalTo(e.getMessage()));
			throw e;
		}
	}

	@Test
	public void testOpenAccount() throws Exception {
		Datagram datagram = new Datagram();
		CustomerVO vo = new CustomerVO();
		vo.setCardId("11");
		vo.setCardType("21");
		vo.setOrgan("111");
		datagram.setDatagramBody(vo);
		PowerMockito.when(customerService.getCustomerBy(vo.getCardId(), vo.getCardType())).thenReturn(null);
		PowerMockito.when(customerDao.getMaxFlowBy(vo.getOrgan())).thenReturn("0");
		PowerMockito.when(customerService.genMainAccount(vo.getOrgan())).thenReturn("0001");
		PowerMockito.when(sequenceDao.nextCustomerNo()).thenReturn("No11111");
		Customer c = new Customer();
		PowerMockito.whenNew(Customer.class).withNoArguments().thenReturn(c);
		assertThat(customerService.openAccount(datagram), notNullValue());
		assertThat(vo.getTotalAcct(), Matchers.equalTo("11100000002"));
		assertThat(vo.getCustomerNo(), Matchers.equalTo("No11111"));
		Mockito.verify(customerDao).getMaxFlowBy(vo.getOrgan());
		Mockito.verify(sequenceDao).nextCustomerNo();
	}
}
