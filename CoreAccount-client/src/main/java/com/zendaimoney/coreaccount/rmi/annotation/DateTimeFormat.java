package com.zendaimoney.coreaccount.rmi.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * 
 * 提供对日期时间格式字段的校验注释(默认格式:yyyy-MM-dd)
 * @author binliu
 * @since 1.0
 */
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface DateTimeFormat {
	String pattern() default "yyyy-MM-dd";
}
