package com.zendaimoney.coreaccount.util;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.exception.BusinessException;

import java.math.BigDecimal;

import static java.lang.Math.pow;

/**
 * 
 * The class NPresentValueAPI contains methods to calculate present values of a
 * series of cashflows. There are three basic methods in the class.
 * 
 * @author Phil Barker
 * @see IRRUtil 目前使用方法如下， 先通过IRRapi.getDayIRR(final double estimatedResult,final
 *      double[] cashFlows ) 得到IRR，做为r的传入参数，同时传入现金流。 double result1=pV(double
 *      r,double[] cashflows)
 */
public final class NPresentValueUtil {

	/**
	 * @param r
	 *            ： IRR，IRRapi.getDayIRR(final double estimatedResult,final
	 *            double[] cashFlows )
	 * @param cashflows
	 *            ：现金流。 *
	 * @return
	 */
	static public BigDecimal pV(BigDecimal r, BigDecimal[] cashflows) {

		// --start--fix bug CA-196----by Jianlong Ma----
		if (r.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("pv.calculate.rate.must.greater.than.zero"));
		}
		// --end--fix bug CA-196----
		int indx = 0;
		BigDecimal sum = BigDecimal.ZERO;
		for (int i = 0; i < cashflows.length; i++) {
			BigDecimal cashFlow = cashflows[i] == null ? BigDecimal.ZERO : cashflows[i];
			sum = sum.add(Arith.div(cashFlow, new BigDecimal(pow((1 + r.doubleValue()), indx))));// Implements//PV
			indx++;
		}
		return new BigDecimal(Strings.format(sum));
	}

	/**
	 * 不含当日现金流（去掉回款部分）
	 * 
	 * @param r
	 * @param cashflows
	 * @return
	 */
	static public BigDecimal pvNotPointValue(BigDecimal r, BigDecimal[] cashflows) {
		BigDecimal pv = pV(r, cashflows);
		pv = Arith.sub(pv, cashflows[0]);

		if (pv.compareTo(BigDecimal.ZERO) > 0) {
			return pv;
		} else {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 实时计算PV
	 * 
	 * @param deducted
	 *            是否扣除当日回款
	 * @param rate
	 *            利率
	 * 
	 * @param cashflows
	 *            现金流
	 * @return
	 */
	public static BigDecimal realtimeCalculatePV(Boolean deducted, BigDecimal rate, BigDecimal[] cashflows) {
		try {
			if (null != deducted && deducted.booleanValue()) {
				return pvNotPointValue(rate, cashflows);
			} else {
				return pV(rate, cashflows);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("calculate.pv.error"));
		}
	}

}