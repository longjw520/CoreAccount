package com.zendaimoney.coreaccount.rmi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.zendaimoney.coreaccount.rmi.annotation.validator.StringElementLengthProcessor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = StringElementLengthProcessor.class)
public @interface StringElementLengthRange {

	int max();

	int min() default 0;

	String message();

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
