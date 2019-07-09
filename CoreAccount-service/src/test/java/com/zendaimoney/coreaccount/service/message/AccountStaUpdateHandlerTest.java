package com.zendaimoney.coreaccount.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zendaimoney.coreaccount.rmi.vo.AccountStaUpdateVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerFinanceService;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.exception.BusinessException;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
public class AccountStaUpdateHandlerTest {

	@InjectMocks
	AccountStaUpdateHandler accountStaUpdateHandler;
	@Mock
	LedgerFinanceService ledgerFinanceService;
	@Mock
	LedgerLoanService ledgerLoanService;

	// 业务类型不对
	@Test(expected = BusinessException.class)
	public void testDisableAccount_error_busitype() throws Exception {
		String datagramText = BufferedInputFile.read("data/json/LoanHouseholdService_dis_error_busitype.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagramText, AccountStaUpdateVo.class);

		String result = accountStaUpdateHandler.handle(datagram);
		Assert.assertEquals("000001", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
	}

	@Test
	public void testDisableAccount_investtype() throws JsonProcessingException, IOException {
		String datagramText = BufferedInputFile.read("data/json/LoanHouseholdService_dis_investtype.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagramText, AccountStaUpdateVo.class);
		accountStaUpdateHandler.handle(datagram);
	}

	@Test
	public void testDisableAccount_loanype() throws JsonProcessingException, IOException {
		String datagramText = BufferedInputFile.read("data/json/LoanHouseholdService_dis_loantype.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagramText, AccountStaUpdateVo.class);
		AccountStaUpdateVo accountStaUpdateVo = (AccountStaUpdateVo) datagram.getDatagramBody();

		accountStaUpdateHandler.handle(datagram);
		Mockito.verify(ledgerFinanceService, times(0)).updateStatus(accountStaUpdateVo, null);

	}
}
