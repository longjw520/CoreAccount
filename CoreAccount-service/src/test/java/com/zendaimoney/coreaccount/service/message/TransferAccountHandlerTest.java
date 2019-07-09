package com.zendaimoney.coreaccount.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.TransferAccountVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.exception.BusinessException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springside.modules.utils.Reflections;

import java.io.IOException;

import static org.mockito.Mockito.doThrow;

/**
 * 测试分账转账的handler
 * 
 * @author Jianlong Ma
 */
@RunWith(MockitoJUnitRunner.class)
public class TransferAccountHandlerTest {

	private TransferAccountHandler transferAccountHandler;
	@Mock
	private LedgerService ledgerService;

	@Before
	public void init() {
		transferAccountHandler = new TransferAccountHandler();
		Reflections.setFieldValue(transferAccountHandler, "ledgerService", ledgerService);
	}

	@Test
	public void testCalculateAccrualHandler_ok() {

		String json = BufferedInputFile.read("data/json/LedgerTransferAccountTest.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(json, TransferAccountVo.class);
		TransferAccountVo transferVo = (TransferAccountVo) datagram.getDatagramBody();

		Ledger customerLedger = new Ledger();
		customerLedger.setId(5L);
		customerLedger.setAccount(Constants.COMPANY_LEDGER_ACCOUNT);
		customerLedger.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);

		Ledger companyCashLedger = new Ledger();
		companyCashLedger.setId(5L);
		companyCashLedger.setAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
		companyCashLedger.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);

		String result = transferAccountHandler.handle(datagram);
		Mockito.verify(ledgerService).transferAccount(transferVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			Assert.fail();
		} catch (IOException e) {
			Assert.fail();
		}

	}

	@Test(expected = BusinessException.class)
	public void testCalculateAccrualHandler_error() {

		String json = BufferedInputFile.read("data/json/LedgerTransferAccountTest.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(json, TransferAccountVo.class);
		TransferAccountVo transferVo = (TransferAccountVo) datagram.getDatagramBody();

		doThrow(new BusinessException()).when(ledgerService).transferAccount(transferVo, 0L);

		transferAccountHandler.handle(datagram);
		Mockito.verify(ledgerService).transferAccount(transferVo, 0L);

	}
}
