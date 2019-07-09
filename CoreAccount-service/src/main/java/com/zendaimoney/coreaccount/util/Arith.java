package com.zendaimoney.coreaccount.util;

/**
 * 
 * 
 * 高精度计算类
 * 
 * @author zhangtan
 */

import java.math.BigDecimal;

public class Arith {

	// 默认精度

	private static final int DEF_DIV_SCALE = 18;

	private Arith() {

	}

	/**
	 * 加法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
		if (v1 == null) {
			v1 = BigDecimal.valueOf(0);
		}
		if (v2 == null) {
			v2 = BigDecimal.valueOf(0);
		}
		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.add(b2);

	}

	/**
	 * 减法运算
	 * 
	 * @param v1
	 * 
	 * @param v2
	 * 
	 * @return
	 */

	public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
		v1 = v1 == null ? new BigDecimal(0) : v1;
		v2 = v2 == null ? new BigDecimal(0) : v2;

		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.subtract(b2);

	}

	/**
	 * 乘法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
		v1 = v1 == null ? new BigDecimal(0) : v1;
		v2 = v2 == null ? new BigDecimal(0) : v2;

		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.multiply(b2);

	}

	/**
	 * 除法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal div(BigDecimal v1, BigDecimal v2) {

		return div(v1, v2, DEF_DIV_SCALE);

	}

	/**
	 * 除法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @param scale
	 *            精度
	 * 
	 * @return
	 */

	public static double div(double v1, double v2, int scale) {

		if (scale < 0) {

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}

		BigDecimal b1 = new BigDecimal(Double.toString(v1));

		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	/**
	 * 除法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @param scale
	 *            精度
	 * 
	 * @return
	 */

	public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
		v1 = v1 == null ? new BigDecimal(0) : v1;
		v2 = v2 == null ? new BigDecimal(0) : v2;

		if (scale < 0) {

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}

		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);

	}

	/**
	 * 
	 * 
	 * @param v
	 * 
	 * 
	 * @param scale
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal round(BigDecimal v, int scale) {
		v = v == null ? new BigDecimal(0) : v;
		if (scale < 0) {

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}

		BigDecimal b = new BigDecimal(v.toString());

		BigDecimal one = new BigDecimal("1");

		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP);

	}

}
