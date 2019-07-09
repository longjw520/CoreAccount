package com.zendaimoney.coreaccount.util;

import java.math.BigDecimal;

/**
 * 数字计算辅助工具类
 * 
 * @author binliu
 * 
 */
public abstract class NumberUtil {

	public static BigDecimal getBigNum(BigDecimal value) {
		return getBigNum(value, BigDecimal.ZERO);
	}

	public static BigDecimal getBigNum(BigDecimal value, BigDecimal defaultValue) {
		return value == null ? defaultValue : value;
	}

}
