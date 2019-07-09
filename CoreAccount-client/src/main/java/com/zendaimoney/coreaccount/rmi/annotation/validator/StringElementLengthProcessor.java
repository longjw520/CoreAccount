package com.zendaimoney.coreaccount.rmi.annotation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.zendaimoney.coreaccount.rmi.annotation.StringElementLengthRange;

/**
 * 校验String数组元素的长度不超过规定值(StringElementLengthRange注解处理器)
 * 
 * @author Jianlong Ma
 * @version 1.0
 * 
 */
public class StringElementLengthProcessor implements ConstraintValidator<StringElementLengthRange, String[]> {

	private int max;

	@Override
	public void initialize(StringElementLengthRange constraintAnnotation) {
		max = constraintAnnotation.max();
	}

	@Override
	public boolean isValid(String[] element, ConstraintValidatorContext context) {
		if (element == null || element.length == 0)
			return true;
		for (String item : element) {
			if (null == item)
				return false;
			if (item.length() > max)
				return false;
		}
		return true;
	}

}
