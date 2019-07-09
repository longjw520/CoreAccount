package com.zendaimoney.coreaccount.rmi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.zendaimoney.coreaccount.rmi.annotation.validator.BeanNotNullProcessor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = BeanNotNullProcessor.class)
public @interface BeanNotNull {
	
	String message() default "集合中的对象不能为null";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
