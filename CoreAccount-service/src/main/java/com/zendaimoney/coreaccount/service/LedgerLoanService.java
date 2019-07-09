package com.zendaimoney.coreaccount.service;

import static com.zendaimoney.coreaccount.constants.Constants.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.LedgerDao;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.dao.RepaymentPlanDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.AccountStaUpdateVo;
import com.zendaimoney.coreaccount.rmi.vo.CalculatePvVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.LoanHouseholdVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.util.NPresentValueUtil;
import com.zendaimoney.coreaccount.util.ObjectHelper;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;
import com.zendaimoney.utils.DateUtils;

/**
 * 贷款分户服务
 *
 * @author Jianlong Ma
 * @update:[2013-11-12] [ShiMing]
 */
@Named
@Transactional
public class LedgerLoanService {

    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private RepaymentPlanService repaymentPlanService;
    @Inject
    private LedgerLoanDao ledgerLoanDao;
    @Inject
    private LedgerDao ledgerDao;
    @Inject
    private PvService pvService;
    @Inject
    private RepaymentPlanDao repaymentPlanDao;
    @Inject
    private WorkFlowDao workFlowDao;
    @Inject
    private SequenceDao sequenceDao;

    /**
     * 查询PV
     *
     * @param calculatePvVo
     * @return
     */
    public BigDecimal queryPV(CalculatePvVo calculatePvVo) {
        Long loanId = calculatePvVo.getId();
        LedgerLoan ledgerLoan = ledgerLoanDao.findUniqueBy("id", loanId);
        /** 如果Loan不存在 */
        if (null == ledgerLoan) {
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerloan.not.exist"));
        }
        Boolean deducted = calculatePvVo.getDeducted();
        Date interestStart = DateUtils.truncate(ledgerLoan.getInterestStart(), Calendar.DATE);
        Date pvDate = DateUtils.nullSafeParseDate(calculatePvVo.getDate(), Constants.DATE_FORMAT);
        BigDecimal rate = ledgerLoan.getRate();

        if (pvDate.before(interestStart)) {
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("pv.calculate.date.delay.interest.start"));
        }
        // 去掉是否存在还款计划的判断
//        if (ledgerLoan.getRepaymentPlans().size() == 0) {
//            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("pv.calculate.repayment.plan.no.exist"));
//        }
        return calculatePV(deducted, rate, loanId, calculatePvVo.getDate());
    }


    /**
     * 查询PV
     *
     * @param loanId 债权ID
     * @param rate 利率
     * @param interestStart 起息日
     * @param date 查询日期
     * @param deducted 是否扣除当日回款
     * @return
     */
    public BigDecimal queryPV(Long loanId, BigDecimal rate, Date interestStart, String date, boolean deducted) {
        Date interestStartDate = DateUtils.truncate(interestStart, Calendar.DATE);
        Date pvDate = DateUtils.nullSafeParseDate(date, Constants.DATE_FORMAT);
        if (pvDate.before(interestStartDate)) {
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("pv.calculate.date.delay.interest.start"));
        }
        return calculatePV(deducted, rate, loanId, date);

    }

    public Object[] getAllLoanIdByStatus(String... acctStatus) {
        if (acctStatus == null || acctStatus.length == 0)
            throw new UnsupportedOperationException("must type string array of status");
        return ledgerLoanDao.getAllLoanIdByStatus(acctStatus);
    }

    /**
     * 计算PV
     *
     * @param deducted ，rate，loanId，pvDate
     * @return BigDecimal PV
     */
    public BigDecimal calculatePV(Boolean deducted, BigDecimal rate, long loanId, String pvDate) {
        BigDecimal pv = pvService.redisPV(deducted, loanId, pvDate);
        if (pv != null)
            return pv;
        BigDecimal[] cashFlow = repaymentPlanService.getBigDecimalAfterCashFLow(loanId, DateUtils.nullSafeParseDate(pvDate, Constants.DATE_FORMAT));
        return NPresentValueUtil.realtimeCalculatePV(deducted, rate, cashFlow);

    }

    /**
     * 查询债权接口
     *
     * @param queryLedgerLoanVo (报文VO)
     * @param isIncludeCustomer 是否包含客户信息
     * @return 查询出来的贷款分户消息表和核心客户信息表相关内容
     */
    public Page<LedgerLoan> queryBy(QueryLedgerLoanVo queryLedgerLoanVo,boolean isIncludeCustomer) {
        Page<LedgerLoan> page = ledgerLoanDao.queryBy(queryLedgerLoanVo);
        if(isIncludeCustomer) {
            for (LedgerLoan ledgerLoan : page.getResult()){
                ledgerLoan.setCustomer(ledgerLoan.getLedger().getCustomer());
            }   
        }
        logger.debug("债权查询成功！");
        return page;
    }
    
    public List<Object> queryForLoanJudgeTimer(){
    	return ledgerLoanDao.queryForLoanJudgeTimer();
    }


    /**
     * 010004保存贷款分户信息
     *
     * @param datagram (报文对象)
     * @return
     */
    public String create(Datagram datagram) {
        LoanHouseholdVo loanHouseholdVo = (LoanHouseholdVo) datagram.getDatagramBody();
        if (ledgerLoanDao.exist(loanHouseholdVo))
            throw new BusinessException(PROCESS_STATUS_FAIL, "导入债权重复");
        //用于逾期债权导入时放置主债权的逾期总期数，得到值后置空
        Long overdueTerm = loanHouseholdVo.getOverdueTerm();
        if(overdueTerm!=null){
            loanHouseholdVo.setOverdueTerm(null);
        }
        LedgerLoan ledgerLoan = new LedgerLoan();
        Ledger ledger = ledgerDao.findUniqueBy("account", loanHouseholdVo.getLegerNo());
        ValidateUtil.validateLedger(ledger, Constants.BUSINESS_TYPE_LOAN);
        ObjectHelper.copy(loanHouseholdVo, ledgerLoan, "repaymentPlans");
        ledgerLoan.setLedger(ledger);
        ledgerLoan.setOpenacctDate(DateUtils.nullSafeParseDate(loanHouseholdVo.getOpenAcctDate(), Constants.DATE_FORMAT));
        if (StringUtils.isEmpty(ledgerLoan.getAcctStatus())) {
            ledgerLoan.setAcctStatus("1");
        }
        ledgerLoanDao.save(ledgerLoan);
        //逾期子债权处理
        Long fatherLoanId = ledgerLoan.getFatherLoanId();
        if(fatherLoanId!=null){
            LedgerLoan fatherLoan = ledgerLoanDao.findUniqueBy("id", fatherLoanId);
            fatherLoan.setOverdueTerm(overdueTerm);
            ledgerLoanDao.update(fatherLoan);
        }
        loanHouseholdVo.setAcctStatus(ledgerLoan.getAcctStatus());
        loanHouseholdVo.setOperateCode(Constants.PROCESS_STATUS_OK);
        loanHouseholdVo.setId(ledgerLoan.getId());
        // 写还款计划表
        repaymentPlanService.save(ledgerLoan, loanHouseholdVo.getRepaymentPlans());
        logger.info("新建贷款分户成功！");
        //逾期子债权处理
        if(null != loanHouseholdVo.getFatherLoanId()){
        	//计算合同金额-子债权起息日价值
        	CalculatePvVo calculatePvVo = new CalculatePvVo();
        	calculatePvVo.setId(ledgerLoan.getId());//债权编号
        	calculatePvVo.setDate(com.zendaimoney.coreaccount.util.DateUtils.format(ledgerLoan.getInterestStart(), "yyyy-MM-dd"));//查询日期-起息日
        	calculatePvVo.setDeducted(false);//不扣除回款额
        	BigDecimal loan = queryPV(calculatePvVo);
        	//合同金额
        	ledgerLoan.setLoan(loan);
        	
        	calculatePvVo.setDate(loanHouseholdVo.getOpenAcctDate());//债权导入日
        	BigDecimal outstanding = queryPV(calculatePvVo);
        	//债权剩余本金
        	ledgerLoan.setOutstanding(outstanding);
        	//金额备用
        	ledgerLoan.setAmountSpare(outstanding);
        	
        	loanHouseholdVo.setLoan(ledgerLoan.getLoan());
        	loanHouseholdVo.setOutstanding(ledgerLoan.getOutstanding());
        	loanHouseholdVo.setAmountSpare(ledgerLoan.getAmountSpare());
        }
        return JsonHelper.toJson(datagram);
    }

    /**
     * 查询债权接口
     *
     * @param queryLedgerLoanVo (报文VO)
     * @return 查询出来的贷款分户消息表和核心客户信息表相关内容
     */
    public Page<LedgerLoan> queryBy(QueryLedgerLoanVo queryLedgerLoanVo) {
        return queryBy(queryLedgerLoanVo, true);
    }

    /**
     * 分户状态修改 010006
     *
     * @return response(修改后信息)
     */
    public void updateStatus(AccountStaUpdateVo accountStaUpdateVo, BusinessInfo businessInfo) {
        // 贷款
        LedgerLoan ledgerLoan = ledgerLoanDao.getById(accountStaUpdateVo.getId());
        ValidateUtil.validateLedgerLoanNull(ledgerLoan);

        String startValue = ledgerLoan.getAcctStatus();
        ledgerLoan.setAcctStatus(accountStaUpdateVo.getAcctStatus());
        if (ACCOUNT_STATUS_OVERDUE.equals(accountStaUpdateVo.getAcctStatus())) {// 逾期
            ledgerLoan.setLastBreachDate(new Date());
        }
        //逾期垫付债权导入-当逾期总期数为0是，设置主债权逾期总期数为0
        if(accountStaUpdateVo.getOverDueTerm()!=null&&0L==accountStaUpdateVo.getOverDueTerm()&&ledgerLoan.getFatherLoanId()!=null){
            //查询父债权
            LedgerLoan mainLedgerLoan = ledgerLoanDao.getById(ledgerLoan.getFatherLoanId());
            mainLedgerLoan.setOverdueTerm(0L);
        }
        accountStaUpdateVo.setOperateCode(Constants.PROCESS_STATUS_OK);
        workFlowDao.workFlowSave(businessInfo.getId(), null, ledgerLoan.getId(), startValue, null, ledgerLoan.getAcctStatus(), sequenceDao.nextWorkFlowNo(), LEDGER_LOAN, businessInfo.getBusinessTypeId());
        logger.info(ledgerLoan.getId()+"贷款分户状态已更新");
    }

    public BigDecimal pvTest(Long loanId, String date, Boolean deducted) {
        LedgerLoan ledgerLoan = ledgerLoanDao.findUniqueBy("id", loanId);
        if (ledgerLoan == null) {
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerLoan.id.not.exist"));
        }
        Date interestStart = DateUtils.truncate(ledgerLoan.getInterestStart(), Calendar.DATE);
        if (DateUtils.nullSafeParseDate(date, "yyyy-MM-dd").before(interestStart)) {
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("pv.calculate.date.delay.interest.start"));
        }
        BigDecimal[] cashFlow = repaymentPlanService.getBigDecimalAfterCashFLow(loanId, DateUtils.nullSafeParseDate(date, Constants.DATE_FORMAT));
        return NPresentValueUtil.realtimeCalculatePV(deducted, ledgerLoan.getRate(), cashFlow);

    }

    /**
     * 还款债权
     *
     * @return
     */
    public void repaymentLoan(LedgerLoan ledgerLoan, BigDecimal creditorAmt, BigDecimal creditorInterestAmt, Date payDate) {
        // 贷款余额-当前应付本金：AC_T_LEDGER_LOAN.OUTSTANDING=OUTSTANDING-债权人应收本金
        // 当前应付利息：AC_T_LEDGER_LOAN.INTEREST_PAYABLE=AC_T_LEDGER_LOAN.INTEREST_PAYABLE-借款人应还利息
        ledgerLoan.setOutstanding(ledgerLoan.getOutstanding().subtract(creditorAmt));
        ledgerLoan.setInterestPayable(ledgerLoan.getInterestPayable().subtract(creditorInterestAmt));
        ledgerLoan.setCurrNum(ledgerLoan.getCurrNum() + 1);
        ledgerLoan.setLastExpiry(payDate);
        // 判断债权是否失效
        Long totalNum = ledgerLoan.getTotalNum();// 总期数
        Long currNum = ledgerLoan.getCurrNum(); // 当前期数
        if (currNum.compareTo(totalNum) == 0)
            ledgerLoan.setAcctStatus(ACCOUNT_STATUS_DISABLE);// 停用
        else if (currNum.compareTo(totalNum) == -1)
            ledgerLoan.setNextExpiry(repaymentPlanDao.getNextPayDate(ledgerLoan.getId(), currNum));// 更新下一还款日

    }
    
    /**
     * 债权提前结清(记录结清金额、时间，更新债权状态)
     * 
     * @param ledgerLoan
     * @param settleAmount
     * @param settleDate
     */
    public void earlySettleLoan(LedgerLoan ledgerLoan, BigDecimal settleAmount, Date settleDate, BusinessInfo businessInfo){
    	String startValue = ledgerLoan.getAcctStatus();
    	
    	ledgerLoan.setEarlySettleAmount(settleAmount);
    	ledgerLoan.setEarlySettleDate(settleDate);
    	ledgerLoan.setAcctStatus(ACCOUNT_STATUS_EARLYSTL);
    	//写事务流水
    	workFlowDao.workFlowSave(businessInfo.getId(), null, ledgerLoan.getId(), startValue, null, ledgerLoan.getAcctStatus(), sequenceDao.nextWorkFlowNo(), LEDGER_LOAN, businessInfo.getBusinessTypeId());
    }

}
