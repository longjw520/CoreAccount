package com.zendaimoney.coreaccount.service;

import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_FAIL;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;

public class ValidateUtilTest {
	// 分账-ok
	@Test
	public void testValidateLedger_ok() {
		Ledger customerLedger = getLedger(1L, "0000000100000002000", Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
		ValidateUtil.validateLedger(customerLedger, Constants.BUSINESS_TYPE_FINANCING);
	}

	// 分账号不存在
	@Test
	public void testValidateLedger_account_error() {
		try {
			ValidateUtil.validateLedger(null, null);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("account_not_exist"), e.getMessage());
		}

	}

	// 分账状态不正常
	@Test
	public void testValidateLedger_status_error() {
		Ledger customerLedger = getLedger(1L, "0000000100000002000", Constants.ACCOUNT_STATUS_DISABLE, Constants.BUSINESS_TYPE_FINANCING);
		try {
			ValidateUtil.validateLedger(customerLedger, null);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledgerLoan.create.ledger.disabled"), e.getMessage());
		}
	}

	// 分账业务类型不符
	@Test
	public void testValidateLedger_type_error() {
		Ledger customerLedger = getLedger(1L, "0000000100000002000", Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
		try {
			ValidateUtil.validateLedger(customerLedger, Constants.BUSINESS_TYPE_LOAN);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledger.businessType.notdebt"), e.getMessage());
		}
	}

	// 债权-ok
	@Test
	public void testValidateLedgerLoan_ok() {
		LedgerLoan ledgerLoan = getLedgerLoan(1L, Constants.ACCOUNT_STATUS_REGULAR);
		ValidateUtil.validateLedgerLoan(ledgerLoan);
	}

	// 债权编号不存在
	@Test
	public void testValidateLedgerLoan_null() {
		try {
			ValidateUtil.validateLedgerLoan(null);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledgerLoan.id.not.exist"), e.getMessage());
		}
	}

	// 债权状态不正常
	@Test
	public void testValidateLedgerLoan_status_error() {
		LedgerLoan ledgerLoan = getLedgerLoan(1L, Constants.ACCOUNT_STATUS_DISABLE);
		try {
			ValidateUtil.validateLedgerLoan(ledgerLoan);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledgerLoan.acctstatus.disabled"), e.getMessage());
		}
	}

	// 解冻金额ok
	@Test
	public void testValidateUnfreezeAmount_ok() {
		BigDecimal unfreezeAmount = BigDecimal.valueOf(10);
		BigDecimal ledgerFrozenAmt = BigDecimal.valueOf(100);

		ValidateUtil.validateUnfreezeAmount(unfreezeAmount, ledgerFrozenAmt);

	}

	// 解冻金额不足validateUnfreezeAmount
	@Test
	public void testValidateUnfreezeAmount_error() {
		BigDecimal unfreezeAmount = BigDecimal.valueOf(1000);
		BigDecimal ledgerFrozenAmt = BigDecimal.valueOf(100);

		try {
			ValidateUtil.validateUnfreezeAmount(unfreezeAmount, ledgerFrozenAmt);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledger.unfreezeAmt.error"), e.getMessage());
		}

	}

	// 冻结金额okvalidateUnfreezeAmount
	@Test
	public void testValidateFrozenAmount_ok() {
		BigDecimal frozenAmount = BigDecimal.valueOf(10);
		BigDecimal ledgerAmount = BigDecimal.valueOf(13);

		ValidateUtil.validateFrozenAmount(frozenAmount, ledgerAmount);

	}

	// 冻结金额不足validateUnfreezeAmount
	@Test
	public void testValidateFrozenAmount_error() {
		BigDecimal frozenAmount = BigDecimal.valueOf(100);
		BigDecimal ledgerAmount = BigDecimal.valueOf(13);

		try {
			ValidateUtil.validateFrozenAmount(frozenAmount, ledgerAmount);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledger.frozenAmt.error"), e.getMessage());
		}

	}

	// 客户账户余额ok
	@Test
	public void testValidateLedgerAmount_ok() {
		BigDecimal amount = new BigDecimal(50);
		BigDecimal customerLedgerAmount = new BigDecimal(50);

		ValidateUtil.validateLedgerAmount(amount, customerLedgerAmount);
	}

	// 客户账户余额不足
	@Test
	public void testValidateLedgerAmount_error() {
		BigDecimal amount = new BigDecimal("500.001");
		BigDecimal customerLedgerAmount = new BigDecimal(500);

		try {
			ValidateUtil.validateLedgerAmount(amount, customerLedgerAmount);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledger.amount.less"), e.getMessage());
		}

	}

	// 公司现金账户余额ok
	@Test
	public void testValidateCompanyCashLedgerAmount_ok() {
		BigDecimal amount = new BigDecimal(50);
		BigDecimal companyCashLedgerAmount = new BigDecimal(50);

		ValidateUtil.validateCompanyCashLedgerAmount(amount, companyCashLedgerAmount);
	}

	// 客户账户余额不足
	@Test
	public void testValidateCompanyCashLedgerAmount_error() {
		BigDecimal amount = new BigDecimal("500.001");
		BigDecimal customerLedgerAmount = new BigDecimal(500);

		try {
			ValidateUtil.validateCompanyCashLedgerAmount(amount, customerLedgerAmount);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("company.cash.balance.not.enough"), e.getMessage());
		}

	}

	// 小数大于大数小于0.01
	@Test
	public void testCompareAmount_Ok1() {
		BigDecimal littleAmount = new BigDecimal("78945416548932434.0019999");
		BigDecimal muchAmount = new BigDecimal("78945416548932434.001");
		ValidateUtil.compareAmount(littleAmount, muchAmount, "pv.calculate.repayment.plan.no.exist");

	}

	// 小数小于大数
	@Test
	public void testCompareAmount_Ok2() {
		BigDecimal littleAmount = new BigDecimal("78945416548932434.002");
		BigDecimal muchAmount = new BigDecimal("78945416548932434.0020001");
		ValidateUtil.compareAmount(littleAmount, muchAmount, "pv.calculate.repayment.plan.no.exist");

	}

	// 小数大于大数等于0.001
	@Test
	public void testCompareAmount_Exception1() {
		BigDecimal littleAmount = new BigDecimal("8888888888.004");
		BigDecimal muchAmount = new BigDecimal("8888888888.003");
		try {
			ValidateUtil.compareAmount(littleAmount, muchAmount, "pv.calculate.repayment.plan.no.exist");
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("pv.calculate.repayment.plan.no.exist"), e.getMessage());
		}

	}

	// 小数大于大数大于0.001 (expected = BusinessException.class)
	@Test
	public void testCompareAmount_Exception2() {
		BigDecimal littleAmount = new BigDecimal("1000000000.00211");
		BigDecimal muchAmount = new BigDecimal("1000000000.001");
		try {
			ValidateUtil.compareAmount(littleAmount, muchAmount, "pv.calculate.repayment.plan.no.exist");
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("pv.calculate.repayment.plan.no.exist"), e.getMessage());
		}

	}

	// 小数大于大数等于0.001
	@Test
	public void testCompareAmount_Exception3() {
		BigDecimal littleAmount = new BigDecimal("0.005444444");
		BigDecimal muchAmount = new BigDecimal("0.004444444");
		try {
			ValidateUtil.compareAmount(littleAmount, muchAmount, "pv.calculate.repayment.plan.no.exist");
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("pv.calculate.repayment.plan.no.exist"), e.getMessage());
		}

	}

	private Ledger getLedger(Long id, String account, String status, String busiType) {
		Ledger ledger = new Ledger();
		ledger.setId(id);
		ledger.setAccount(account);
		ledger.setBusiType(busiType);
		ledger.setAcctStatus(status);
		return ledger;
	}

	private LedgerLoan getLedgerLoan(Long id, String status) {
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(id);
		ledgerLoan.setAcctStatus(status);
		return ledgerLoan;
	}

	@Test(expected = BusinessException.class)
	public void testValidateLedgerFinance_null() {
		try {

			ValidateUtil.validateLedgerFinance(null, 0);
		} catch (BusinessException e) {
			Assert.assertEquals(PROCESS_STATUS_FAIL, e.getCode());
			throw e;
		}
	}

	@Test(expected = BusinessException.class)
	public void testValidateLedgerFinance_status() {
		LedgerFinance lf = new LedgerFinance();
		lf.setAcctStatus("9");
		ValidateUtil.validateLedgerFinance(lf, 0);
	}

	@Test
	public void testValidateLedgerFinance_ok() {
		LedgerFinance lf = new LedgerFinance();
		lf.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
		ValidateUtil.validateLedgerFinance(lf, 0);
	}

	@Test(expected = RuntimeException.class)
	public void testValidateRepaymentPlan() {
		ValidateUtil.validateRepaymentPlan(null);
	}

	@Test(expected = BusinessException.class)
	public void testValidateBusinessInfo() {
		ValidateUtil.validateBusinessInfo(null, "000111");
	}

	@Test(expected = BusinessException.class)
	public void testValidateCustomer() {
		Customer c = new Customer();
		ValidateUtil.validateCustomer(c);
	}

	@Test
	public void testValidateCustomer_fail() {
		Customer c = null;
		try {
			ValidateUtil.validateCustomer(c);
		} catch (BusinessException e) {
			Assert.fail();
		}
	}

	@Test(expected = BusinessException.class)
	public void testIfNotSameCustomer() {
		Ledger account1 = new Ledger();
		Ledger account2 = new Ledger();
		Customer c1 = new Customer();
		c1.setId(1L);
		Customer c2 = new Customer();
		c2.setId(2L);
		account1.setCustomer(c1);
		account2.setCustomer(c2);
		try {
			ValidateUtil.ifNotSameCustomer(account1, account2);
		} catch (BusinessException e) {
			Assert.assertEquals(Constants.PROCESS_STATUS_FAIL, e.getCode());
			throw e;
		}
	}

	// 小数大于大数小于0.00000000000001
	@Test
	public void testCompareProportions_Ok1() {
		BigDecimal littleProportions = new BigDecimal("0.000000000000019");
		BigDecimal muchProportions = new BigDecimal("0.00000000000001");
		ValidateUtil.compareProportions(littleProportions, muchProportions, "pv.calculate.repayment.plan.no.exist", 1L);

	}

	// 小数小于大数
	@Test
	public void testCompareProportions_Ok2() {
		BigDecimal littleProportions = new BigDecimal("34.00000000000001");
		BigDecimal muchProportions = new BigDecimal("34.00000000000001");
		ValidateUtil.compareProportions(littleProportions, muchProportions, "pv.calculate.repayment.plan.no.exist", 1L);

	}

	// 小数大于大数等于0.00000000000001
	@Test
	public void testCompareProportions_Exception1() {
		BigDecimal littleProportions = new BigDecimal("34.00000000000002");
		BigDecimal muchProportions = new BigDecimal("34.00000000000001");
		try {
			ValidateUtil.compareProportions(littleProportions, muchProportions, "pv.calculate.repayment.plan.no.exist", 1L);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("pv.calculate.repayment.plan.no.exist") + "1", e.getMessage());
		}

	}

	// 小数大于大数大于0.00000000000001
	@Test
	public void testCompareProportions_Exception2() {
		BigDecimal littleProportions = new BigDecimal("34.00000000000003001");
		BigDecimal muchProportions = new BigDecimal("34.00000000000002");
		try {
			ValidateUtil.compareProportions(littleProportions, muchProportions, "pv.calculate.repayment.plan.no.exist", 1L);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("pv.calculate.repayment.plan.no.exist") + "1", e.getMessage());
		}

	}

	// finance冻结比例不足
	@Test
	public void testValidateUnfreezeProportions() {
		BigDecimal littleProportions = new BigDecimal("34.00000000000002");
		BigDecimal muchProportions = new BigDecimal("34.00000000000001");
		try {
			ValidateUtil.validateUnfreezeProportions(littleProportions, muchProportions, 1L);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledgerfinance.frozenproportion.less") + "1", e.getMessage());
		}
	}

	// finance持有比例不足
	@Test
	public void testValidateFrozenProportions() {
		BigDecimal littleProportions = new BigDecimal("34.00000000000002");
		BigDecimal muchProportions = new BigDecimal("34.00000000000001");
		try {
			ValidateUtil.validateFrozenProportions(littleProportions, muchProportions, 1L);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("000001", e.getCode());
			Assert.assertEquals(PropertiesReader.readAsString("ledgerfinance.proportion.less") + "1", e.getMessage());
		}
	}

	@Test(expected = BusinessException.class)
	public void testAssertTrue() {
		try {
			ValidateUtil.assertTrue(false, "ledgerfinance.proportion.less");
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals(Constants.PROCESS_STATUS_FAIL, e.getCode());
			throw e;
		}
	}

	@Test
	public void testAssertTrue_fail() {
		ValidateUtil.assertTrue(false | true, "ledgerfinance.proportion.less");
	}
}
