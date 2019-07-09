package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.dao.FlowDao;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.dao.RepaymentPlanDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.util.Arith;
import com.zendaimoney.coreaccount.util.DateUtils;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.coreaccount.util.SystemUtil;
import com.zendaimoney.coreaccount.vo.EntryInfo;
import com.zendaimoney.coreaccount.vo.EntryInfo.OPERATOR;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

import static com.zendaimoney.coreaccount.constants.Constants.*;
import static com.zendaimoney.coreaccount.constants.EntryNo.CALCULATE_ACCRUAL;
import static com.zendaimoney.coreaccount.constants.Subject.*;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * 每日计息-- 000003
 *
 * @author Jianlong Ma
 * @update ShiMing
 */
@Named
@Transactional
public class CalculateAccrualService {

    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private SequenceDao sequenceDao;
    @Inject
    private LedgerLoanDao ledgerLoanDao;
    @Inject
    private RepaymentPlanDao repaymentPlanDao;
    @Inject
    private LedgerLoanService ledgerLoanService;
    @Inject
    private FlowDao flowDao;

    /**
     * 计算一个债权列表的PV
     *
     * @param ledgerLoanList
     * @return pvValues
     */
    public Map<Long, BigDecimal> calculatePV(List<LedgerLoan> ledgerLoanList) {
        Map<Long, BigDecimal> pvValues = new HashMap<Long, BigDecimal>();

        /** 还款计划信息 */
        String nowStr = DateFormatUtils.format(SystemUtil.currentDate(), "yyyy-MM-dd");
        try {
            for (LedgerLoan ledgerLoan : ledgerLoanList) {
                pvValues.put(ledgerLoan.getId(), ledgerLoanService.calculatePV(false, ledgerLoan.getRate(), ledgerLoan.getId(), nowStr));
            }
        } catch (Exception e) {
            logger.warn(e.toString());
            throw new BusinessException(PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerloan.calculate.pv.error"));
        }
        return pvValues;

    }

    /**
     * 计息
     */
    public void calculateInterest(long businessId) {
        logger.info("计息开始.");
        Date appDate = SystemUtil.currentDate();
        String nowStr = DateFormatUtils.format(appDate, "yyyy-MM-dd");
        /** 取得下一回款日的所有还款计划 */
        Map<Long, RepaymentPlan> repaymentPlans = repaymentPlanDao.getALLNextPay(nowStr);

        /** 取得有效债权信息 */
        List<LedgerLoan> ledgerLoanList = ledgerLoanDao.getLedgerLoans(ACCOUNT_STATUS_REGULAR, ACCOUNT_STATUS_OVERDUE, ACCOUNT_STATUS_IDLE);
        logger.info("有效债权集合长度..." + ledgerLoanList.size());
        Map<Long, BigDecimal> pvValues = calculatePV(ledgerLoanList);

        BigDecimal newinterestPayable;
        logger.debug("size of ledgerLoanList is " + ledgerLoanList.size());
        Map<Long, Ledger> allLedgers = new HashMap<Long, Ledger>();
        int i = 0;
        for (LedgerLoan ledgerLoan : ledgerLoanList) {
            logger.debug("LedgerLoan id is " + ledgerLoan.getId());
            Date nextExpiry = ledgerLoan.getNextExpiry();//下次结息日
            BigDecimal interestPayable = ledgerLoan.getInterestPayable();// 当前应付利息
            Date interestStart = ledgerLoan.getInterestStart();//起息日
            Date lastExpiry = ledgerLoan.getLastExpiry();//上次结息日
            BigDecimal creditorInterestDay, interestDayDeviation, borrowerInterestDayDetail;

            RepaymentPlan repaymentPlan = repaymentPlans.get(ledgerLoan.getId());
            BigDecimal interestAmt = repaymentPlan.getInterestAmt(); //
            if (ledgerLoan.getCurrNum() == 0 && repaymentPlan.getCurrNum() == 0) {
                // 首期应付利息
                newinterestPayable = Arith.mul(Arith.div(interestAmt, new BigDecimal(DateUtils.getDayCount(interestStart, nextExpiry))), new BigDecimal(DateUtils.getDayCount(interestStart, appDate)));
            } else {
                /** 非首期应付利息 */
                newinterestPayable = Arith.mul(Arith.div(interestAmt, new BigDecimal(DateUtils.getDayCount(lastExpiry, nextExpiry))), new BigDecimal(DateUtils.getDayCount(lastExpiry, appDate)));
            }
            //借款人日利息=新应付利息-原应付利息
            BigDecimal borrowerInterestDay = Arith.sub(newinterestPayable, interestPayable);

            /** 更新贷款分户表中的应付利息 */
            ledgerLoan.setInterestPayable(newinterestPayable); // 借款人应付利息

            BigDecimal nowPv = pvValues.get(ledgerLoan.getId());
            ledgerLoan.setAmountSpare(nowPv); // 贷款分户对应的还款计划中每个loan的PV

            BigDecimal allInterestReceivable = nowPv.subtract(ledgerLoan.getOutstanding());
            Set<LedgerFinance> ledgerFinances = ledgerLoan.getValidLedgerFinances();
            logger.debug("size of ledgerFinances is " + ledgerFinances.size());
            for (LedgerFinance finance : ledgerFinances) {
                i++;
                if (i % 1000 == 0) {
                    logger.info("当前执行了：" + i + "次");
                }
                /** 原债权人应收利息 */
                BigDecimal startBalance = getBigDecimalValue(finance.getInterestReceivable());

                /** 债权人(理财人)应收利息=（PV-本金）*持有比例 */
                BigDecimal interestReceivable = allInterestReceivable.multiply(getTotalDebtProportion(finance));

                /** 更新理财分户信息表(当前应收利息) */
                finance.setInterestReceivable(interestReceivable);

                /** 债权人日息=（PV-本金）*持有比例-原债权人应收利息 */
                creditorInterestDay = interestReceivable.subtract(getBigDecimalValue(startBalance));

                /** 借款人针对此投资明细的日息部分 = 借款人日息*总比例 */
                borrowerInterestDayDetail = borrowerInterestDay.multiply(getTotalDebtProportion(finance));

                /** 日息误差=借款人日息-债权人日息 */
                interestDayDeviation = borrowerInterestDayDetail.subtract(creditorInterestDay);

                /** 计算累计差额 = 原值+当日应付利息-当日应收利息 */
                finance.setInterestDeviation(getBigDecimalValue(finance.getInterestDeviation()).add(interestDayDeviation));

                finance.setLastModified(new Date());
                Ledger financeLedger = allLedgers.get(finance.getLedger().getId());
                if (null == financeLedger) {
                    financeLedger = finance.getLedger();
                    allLedgers.put(financeLedger.getId(), financeLedger);
                }

                long flowGroupNo = sequenceDao.nextFlowGroupNo();
                String memo = String.valueOf(finance.getId());

                /** 写债权人流水--（+）应收的日利息 */
                flowDao.save(new EntryInfo(financeLedger, OVERDUEINTEREST_INTERESTRECEIVABLE, creditorInterestDay.setScale(7, ROUND_HALF_UP), CALCULATE_ACCRUAL).updateAmt(OPERATOR.ADD).wrap(businessId, memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
                /** 写借款人流水-- (+) 应付的日利息*/
                flowDao.save(new EntryInfo(ledgerLoan.getLedger(), LEDGER_DETAIL_TYPE_PAYABLE, borrowerInterestDayDetail.setScale(7, ROUND_HALF_UP), CALCULATE_ACCRUAL).updateAmt(OPERATOR.ADD).wrap(businessId, memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
                /** 写误差流水 */
                flowDao.save(new EntryInfo(financeLedger, LEDGER_DETAIL_TYPE_DEVIATION, interestDayDeviation.setScale(7, ROUND_HALF_UP), CALCULATE_ACCRUAL).updateAmt(OPERATOR.ADD).wrap(businessId, memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            }
        }
        logger.info("计息批处理成功" + new Date());
        logger.info("总循环次数..." + i + "次");
    }

    /**
     * 计算持有比例
     */
    private BigDecimal getTotalDebtProportion(LedgerFinance finance) {
        BigDecimal debtProportion = (null != finance.getDebtProportion()) ? finance.getDebtProportion() : BigDecimal.ZERO;
        BigDecimal frozenPorportion = (null != finance.getFrozenPorportion()) ? finance.getFrozenPorportion() : BigDecimal.ZERO;
        return debtProportion.add(frozenPorportion);
    }

    /**
     * 生成流水号
     */
    private String getFlowNo() {
        return sequenceDao.nextFlowNO();
    }

    private BigDecimal getBigDecimalValue(BigDecimal value) {
        return null == value ? BigDecimal.ZERO : value;
    }

}
