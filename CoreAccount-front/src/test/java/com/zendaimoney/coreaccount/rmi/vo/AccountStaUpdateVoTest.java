package com.zendaimoney.coreaccount.rmi.vo;

import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.junit.Test;

public class AccountStaUpdateVoTest extends VoTest {

	@Test
	public void regex_error() {
		AccountStaUpdateVo accountStaUpdateVo = new AccountStaUpdateVo();
		accountStaUpdateVo.setAuthTeller("a");
		accountStaUpdateVo.setId(123L);
		accountStaUpdateVo.setOperateCode("111");
		accountStaUpdateVo.setOrgan("1111");
		accountStaUpdateVo.setBusiType("1");
		accountStaUpdateVo.setAcctStatus("a");
		accountStaUpdateVo.setOperator("a");
		Set<ConstraintViolation<AccountStaUpdateVo>> constraintViolations = validator.validate(accountStaUpdateVo);
		Assert.assertEquals(1, constraintViolations.size());
		Assert.assertEquals("更新的账户状态不合法", constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void regex_OK() {
		AccountStaUpdateVo accountStaUpdateVo = new AccountStaUpdateVo();
		accountStaUpdateVo.setAuthTeller("a");
		accountStaUpdateVo.setId(123L);
		accountStaUpdateVo.setOperateCode("111");
		accountStaUpdateVo.setBusiType("1");
		accountStaUpdateVo.setOrgan("1111");
		accountStaUpdateVo.setAcctStatus("1");
		accountStaUpdateVo.setOperator("a");
		Set<ConstraintViolation<AccountStaUpdateVo>> constraintViolations = validator.validate(accountStaUpdateVo);
		Assert.assertEquals(0, constraintViolations.size());
	}

}
