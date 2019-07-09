package com.zendaimoney.coreaccount.rmi.vo;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Ignore;

@Ignore
public class VoTest {
	protected Validator validator;

	public VoTest() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
}
