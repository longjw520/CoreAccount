package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.data.redis.BasicRedisOpts;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.util.NPresentValueUtil;
import com.zendaimoney.coreaccount.util.ThreadPoolUtil;
import com.zendaimoney.coreaccount.util.ThreadPoolUtil.ThreadPool;
import com.zendaimoney.utils.DateFormatUtils;
import com.zendaimoney.utils.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

@Named
public class PvService {

	private Logger logger = Logger.getLogger(getClass());

	@Inject
	BasicRedisOpts basicRedisOpts;
	@Inject
	private ThreadPoolTaskExecutor taskExecutor;
	@Inject
	private LedgerLoanDao ledgerLoanDao;
	@Inject
	private LedgerLoanService ledgerLoanService;
	@Inject
	private RepaymentPlanService repaymentPlanService;

	/**
	 * 从redis获取 PV
	 * 
	 * @param deducted
	 *            是否扣除当日回款
	 * @param loanId
	 *            债权编号
	 * @param pvDate
	 *            计算日期
	 * @return PV
	 */
	public BigDecimal redisPV(Boolean deducted, long loanId, String pvDate) {
		String pvValue = null;
		try {
			pvValue = basicRedisOpts.getSingleResult(loanId + ":" + pvDate);
		} catch (Exception e) {
			return null;// redis server maybe shutdown or network is wrong!
		}

		/** 计算PV时，是否扣除当日回款的回款额 */
		if (null != pvValue) {
			if (null != deducted && deducted.booleanValue()) {
				return new BigDecimal(pvValue.split(":")[1]);
			}
			return new BigDecimal(pvValue.split(":")[0]);
		}
		return null;
	}

	/**
	 * 优先从redis中获取pv,若不能获取再通过本地计算
	 * 
	 * @return pv值
	 */
	public BigDecimal getPvAlways(LedgerLoan ledgerLoan, Boolean deducted) {
		BigDecimal result;
		String pvDate = DateFormatUtils.format(ledgerLoan.getLastExpiry(), "yyyy-MM-dd");
		long id = ledgerLoan.getId();
		if ((result = redisPV(deducted, ledgerLoan.getId(), pvDate)) == null) {
			BigDecimal cash[] = repaymentPlanService.getBigDecimalAfterCashFLow(id, DateUtils.truncate(ledgerLoan.getLastExpiry(), Calendar.DATE));
			if (deducted) {
				return NPresentValueUtil.pvNotPointValue(ledgerLoan.getRate(), cash);
			}
			return NPresentValueUtil.pV(ledgerLoan.getRate(), cash);
		}
		return result;
	}

	/**
	 * 计算当天所有债权的PV累计值
	 * 
	 * @param queryLedgerLoanVo
	 * @return
	 */

	public BigDecimal getAllLoansPV(QueryLedgerLoanVo queryLedgerLoanVo) {
		ThreadPool<BigDecimal> threadPool = ThreadPoolUtil.newCompletionService(taskExecutor.getThreadPoolExecutor());
		List<Object[]> objArrays = ledgerLoanDao.getPVKeys(queryLedgerLoanVo);
		BigDecimal totalAmt = BigDecimal.ZERO;
		for (final Object[] objArray : objArrays) {
			ThreadPoolUtil.submitTask(threadPool, new Callable<BigDecimal>() {
				@Override
				public BigDecimal call() throws Exception {
					Long loanId = ((BigDecimal) objArray[0]).longValue();
					BigDecimal rate = (BigDecimal) objArray[1];
					Date interestStart = (Date) objArray[2];
					String date = DateFormatUtils.format(new Date(), Constants.DATE_FORMAT);
					return ledgerLoanService.queryPV(loanId, rate, interestStart, date, true);
				}
			});
		}
		try {
			for (int i = 0, j = objArrays.size(); i < j; ++i) {
				totalAmt = totalAmt.add(ThreadPoolUtil.getResult(threadPool));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return totalAmt;
	}
}
