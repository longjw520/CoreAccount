package com.zendaimoney.coreaccount.rmi.vo;

import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.junit.Test;

public class QueryLedgerLoanVoTest extends VoTest {
	@Test
	public void testProductCodeArray() {
		QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
		String[] str = { "1", "2", "asdayyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" };
		queryLedgerLoanVo.setProductCodeArray(str);
		queryLedgerLoanVo.setOperator("002");
		queryLedgerLoanVo.setOrgan("220");

		Set<ConstraintViolation<QueryLedgerLoanVo>> constraintViolations = validator.validate(queryLedgerLoanVo);
		Assert.assertEquals("借款产品代码长度不能超过30，并且不能为null", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testAcctStatusArray() {
		QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
		String[] str = { null, null };

		queryLedgerLoanVo.setOperator("002");
		queryLedgerLoanVo.setOrgan("220");
		queryLedgerLoanVo.setAcctStatusArray(str);
		Set<ConstraintViolation<QueryLedgerLoanVo>> constraintViolations = validator.validate(queryLedgerLoanVo);
		Assert.assertEquals("理财分户状态长度不能超过2，并且不能为null", constraintViolations.iterator().next().getMessage());
	}
}
