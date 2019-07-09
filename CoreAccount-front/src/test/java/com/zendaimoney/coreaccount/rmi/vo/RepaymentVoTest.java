package com.zendaimoney.coreaccount.rmi.vo;

import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

public class RepaymentVoTest extends VoTest {
	@Ignore
	public void LoanId_err() {
		RepaymentVo repaymentVo = new RepaymentVo();

		repaymentVo.setPayDate("20130102");
		repaymentVo.setAuthTeller("a");
		repaymentVo.setOperateCode("111");
		repaymentVo.setOrgan("1111");
		repaymentVo.setOperator("a");
		Set<ConstraintViolation<RepaymentVo>> constraintViolations = validator.validate(repaymentVo);

		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("不能为null", constraintViolations.iterator().next().getMessage());
	}

	@Ignore
	public void LoanId_ok() {
		RepaymentVo repaymentVo = new RepaymentVo();

		repaymentVo.setPayDate("20130102");
		repaymentVo.setAuthTeller("a");
		repaymentVo.setOperateCode("111");
		repaymentVo.setOrgan("1111");
		repaymentVo.setOperator("a");
		Set<ConstraintViolation<RepaymentVo>> constraintViolations = validator.validate(repaymentVo);

		Assert.assertEquals(0, constraintViolations.size());
	}

	@Ignore
	public void PayDate_err() {
		RepaymentVo repaymentVo = new RepaymentVo();

		repaymentVo.setPayDate("");
		repaymentVo.setAuthTeller("a");
		repaymentVo.setOperateCode("111");
		repaymentVo.setOrgan("1111");
		repaymentVo.setOperator("a");
		Set<ConstraintViolation<RepaymentVo>> constraintViolations = validator.validate(repaymentVo);

		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("不能为空", constraintViolations.iterator().next().getMessage());
	}

	@Ignore
	public void PayDate_Illegal() {
		RepaymentVo repaymentVo = new RepaymentVo();

		repaymentVo.setPayDate("2012-01-023");
		repaymentVo.setAuthTeller("a");
		repaymentVo.setOperateCode("111");
		repaymentVo.setOrgan("1111");
		repaymentVo.setOperator("a");
		Set<ConstraintViolation<RepaymentVo>> constraintViolations = validator.validate(repaymentVo);

		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("个数必须在0和10之间", constraintViolations.iterator().next().getMessage());
	}
}
