package com.zendaimoney.coreaccount.vo;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.zendaimoney.coreaccount.constants.Subject;
import com.zendaimoney.coreaccount.entity.Flow;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.vo.EntryInfo.OPERATOR;

@RunWith(MockitoJUnitRunner.class)
public class EntryInfoTest {

	@Test
	public void testEntryInfo() throws Exception {
		Ledger ledger = new Ledger();
		ledger.setFrozenAmt(new BigDecimal("0.01"));
		String detailType = Subject.ACCT_TITLE_CASH;
		BigDecimal tradeAmount = new BigDecimal("12.022123001");
		String entryNo = "000001122121";
		EntryInfo info = new EntryInfo(ledger, detailType, tradeAmount, entryNo);
		Assert.assertEquals(detailType, getValue(info, "detailType"));
		Assert.assertEquals(entryNo, getValue(info, "entryNo"));
		Assert.assertTrue(tradeAmount.compareTo(BigDecimal.class.cast(getValue(info, "tradeAmount"))) == 0);
		Assert.assertTrue(ledger == getValue(info, "ledger"));
		Assert.assertTrue(null == (BigDecimal) getValue(info, "startBalance"));
		detailType = Subject.FROZEN_CASH;
		info = new EntryInfo(ledger, detailType, tradeAmount, entryNo);
		Assert.assertTrue(ledger.getFrozenAmt().compareTo((BigDecimal) getValue(info, "startBalance")) == 0);
		detailType = "none";
		ledger.setDetailValue(detailType, BigDecimal.TEN);
		info = new EntryInfo(ledger, detailType, tradeAmount, entryNo);
		Assert.assertTrue(BigDecimal.TEN.compareTo((BigDecimal) getValue(info, "startBalance")) == 0);
	}

	@Test(expected = AssertionError.class)
	public void testEntryInfo_fail() {
		Ledger ledger = new Ledger();
		new EntryInfo(ledger, null, BigDecimal.ZERO, "");
	}

	@Test
	public void testUpdateAmt() throws Exception {
		Ledger ledger = new Ledger();
		ledger.setAmount(BigDecimal.ONE);
		ledger.setFrozenAmt(BigDecimal.ONE);
		EntryInfo info = new EntryInfo(ledger, Subject.ACCT_TITLE_CASH, BigDecimal.TEN, "");
		info = info.updateAmt(OPERATOR.ADD);
		Assert.assertTrue(new BigDecimal(11).compareTo((BigDecimal) getValue(info, "endBalance")) == 0);
		Assert.assertTrue(((Ledger) getValue(info, "ledger")).getAmount().compareTo((BigDecimal) getValue(info, "endBalance")) == 0);

		info = info.updateAmt(OPERATOR.SUBTRACT);
		Assert.assertTrue(new BigDecimal(-9).compareTo((BigDecimal) getValue(info, "endBalance")) == 0);
		Assert.assertTrue(((Ledger) getValue(info, "ledger")).getAmount().compareTo((BigDecimal) getValue(info, "endBalance")) == 0);

		info = new EntryInfo(ledger, Subject.FROZEN_CASH, BigDecimal.ONE, "");
		info = info.updateAmt(OPERATOR.SUBTRACT);
		Assert.assertTrue(((Ledger) getValue(info, "ledger")).getFrozenAmt().compareTo(BigDecimal.ZERO) == 0);
		ledger.setDetailValue("subject", BigDecimal.ZERO);
		info = new EntryInfo(ledger, "subject", BigDecimal.TEN, "");
		info = info.updateAmt(OPERATOR.ADD);
		Assert.assertTrue(BigDecimal.TEN.compareTo(((Ledger) getValue(info, "ledger")).getDetailValue("subject")) == 0);
	}

	@Test(expected = AssertionError.class)
	public void testUpdateAmt_fail() {
		new EntryInfo(null, null, BigDecimal.TEN, "");
	}

	@Test
	public void testWrap() {
		Ledger ledger = new Ledger();
		ledger.setAmount(BigDecimal.ONE);
		EntryInfo info = new EntryInfo(ledger, Subject.ACCT_TITLE_CASH, BigDecimal.TEN, "");
		Flow flow = info.wrap(1L, "", "34343", 2343L, "C");
		Assert.assertEquals("C", flow.getDorc());
		Assert.assertEquals("34343", flow.getFlowNo());
	}

	private Object getValue(Object target, String fieldName) throws Exception {
		Class<EntryInfo> klass = EntryInfo.class;
		Field f = klass.getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(target);
	}
}
