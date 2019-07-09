package com.zendaimoney.coreaccount.entity;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Test;

import com.zendaimoney.coreaccount.constants.Constants;

/** 测试LedgerLoan实体中取得有效"理财分户信息"的方法 */
public class LedgerLoanTest {

	@Test
	public void getValidLedgerFinances() {
		LedgerLoan ledgerLoan = new LedgerLoan();
		LedgerFinance ledgerFinance = new LedgerFinance();
		ledgerFinance.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
		ledgerLoan.getLedgerFinances().add(ledgerFinance);

		ledgerFinance = new LedgerFinance();
		ledgerFinance.setAcctStatus(Constants.ACCOUNT_STATUS_DISABLE);
		ledgerLoan.getLedgerFinances().add(ledgerFinance);

		Assert.assertEquals(1, ledgerLoan.getValidLedgerFinances().size());
	}

	@Test
	public void testGetFirstRepaymentPlan() {
		LedgerLoan loan = new LedgerLoan();
		RepaymentPlan p1 = new RepaymentPlan();
		p1.setCurrNum(0L);
		RepaymentPlan p2 = new RepaymentPlan();
		p2.setCurrNum(1L);
		loan.setRepaymentPlans(new HashSet<RepaymentPlan>(Arrays.asList(p1, p2)));
		Assert.assertEquals(new Long(0), loan.getFirstRepaymentPlan().getCurrNum());
		Assert.assertSame(p1, loan.getFirstRepaymentPlan());
	}

	public void testGetFirstRepaymentPlan_null() {
		LedgerLoan loan = new LedgerLoan();
		loan.setRepaymentPlans(new HashSet<RepaymentPlan>());
		Assert.assertNull(loan.getFirstRepaymentPlan());
	}
}
