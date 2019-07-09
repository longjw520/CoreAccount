package com.zendaimoney.coreaccount.rmi.vo;

import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.junit.Test;

public class CustomerVoTest extends VoTest {
	@Test
	public void customerNotNull() {
		CustomerVO customerVo = new CustomerVO();
		Set<ConstraintViolation<CustomerVO>> constraintViolations = validator.validate(customerVo);
		Assert.assertEquals(8, constraintViolations.size());
		Assert.assertEquals("不能为空", constraintViolations.iterator().next().getMessage());
	}
}
