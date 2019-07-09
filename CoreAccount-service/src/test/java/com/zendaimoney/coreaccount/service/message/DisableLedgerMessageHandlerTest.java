package com.zendaimoney.coreaccount.service.message;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springside.modules.utils.Reflections;

import com.zendaimoney.coreaccount.service.LedgerService;

public class DisableLedgerMessageHandlerTest {
	@Mock
	private LedgerService ledgerService;

	@Mock
	private DisableLedgerMessageHandler disableLedgerMessageHandler;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		disableLedgerMessageHandler = new DisableLedgerMessageHandler();

		Reflections.setFieldValue(disableLedgerMessageHandler, "ledgerDisableService", ledgerService);
	}
}
