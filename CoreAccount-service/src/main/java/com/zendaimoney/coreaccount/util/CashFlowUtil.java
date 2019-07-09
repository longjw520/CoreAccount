/**
 *
 */
package com.zendaimoney.coreaccount.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zendaimoney.coreaccount.entity.RepaymentPlan;

/**
 * @author zhangtan 现金流工具类.
 */
public class CashFlowUtil {

	public static BigDecimal[] getAfterCashFlowArray(Date beginDate, List<RepaymentPlan> cfList) throws Exception {

		int size = DateUtils.getDayCount(beginDate, cfList.get(cfList.size() - 1).getRepayDay()) + 1;
		BigDecimal[] cashFlow = new BigDecimal[size];

		for (RepaymentPlan cf : cfList) {
			cashFlow[DateUtils.getDayCount(beginDate, cf.getRepayDay())] = cf.getAmt();
		}
		return cashFlow;
	}

	/**
	 * 功能同getAfterCashFlowArray使用sql查询计算
	 * 
	 * @param beginDate
	 * @param cfList
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal[] getCashFlowArray(Date beginDate, List<Object[]> cfList) throws Exception {
		int size = DateUtils.getDayCount(beginDate, (Date) cfList.get(cfList.size() - 1)[0]) + 1;
		BigDecimal[] cashFlow = new BigDecimal[size];
		for (Object[] cf : cfList) {
			cashFlow[DateUtils.getDayCount(beginDate, (Date) cf[0])] = (BigDecimal) cf[1];
		}
		return cashFlow;
	}
}
