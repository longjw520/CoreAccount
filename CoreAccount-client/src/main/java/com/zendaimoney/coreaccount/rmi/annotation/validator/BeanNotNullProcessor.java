package com.zendaimoney.coreaccount.rmi.annotation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.zendaimoney.coreaccount.rmi.annotation.BeanNotNull;

/**
 * 校验集合所有Bean对象不为空(BeanNotNull注解处理器)
 * 
 * @author binliu
 * @version 1.0
 * 
 */
public class BeanNotNullProcessor implements ConstraintValidator<BeanNotNull, Collection<?>> {

	@Override
	public void initialize(BeanNotNull constraintAnnotation) {

	}

	@Override
	public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty())
			return false;
		for (Object item : value) {
			if (item == null)
				return false;
		}
		return true;
	}

}
