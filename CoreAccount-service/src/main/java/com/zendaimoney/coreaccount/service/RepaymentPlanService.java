package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.DebtInfoDao;
import com.zendaimoney.coreaccount.dao.FlowDao;
import com.zendaimoney.coreaccount.dao.LedgerDao;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.dao.RepaymentPlanDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.entity.*;
import com.zendaimoney.coreaccount.rmi.vo.EarlySettlementVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryRepaymentPlanVo;
import com.zendaimoney.coreaccount.rmi.vo.RepaymentPlanVo;
import com.zendaimoney.coreaccount.rmi.vo.RepaymentVo;
import com.zendaimoney.coreaccount.task.FlowTask;
import com.zendaimoney.coreaccount.util.CashFlowUtil;
import com.zendaimoney.coreaccount.util.DateUtils;
import com.zendaimoney.coreaccount.util.NumberUtil;
import com.zendaimoney.coreaccount.util.ObjectHelper;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.coreaccount.vo.EntryInfo;
import com.zendaimoney.coreaccount.vo.EntryInfo.OPERATOR;
import com.zendaimoney.exception.BusinessException;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import javax.inject.Inject;
import javax.inject.Named;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static com.zendaimoney.coreaccount.constants.Constants.*;
import static com.zendaimoney.coreaccount.constants.EntryNo.*;
import static com.zendaimoney.coreaccount.constants.Subject.*;
import static com.zendaimoney.coreaccount.service.ValidateUtil.validateLedgerLoanAll;
import static com.zendaimoney.coreaccount.service.ValidateUtil.validateRepaymentPlan;

/**
 * 债权还款
 * 
 * @author longjw
 *
 */
@Named
@Transactional
public class RepaymentPlanService {
    private final Logger logger = Logger.getLogger(getClass());
    @Inject
    private RepaymentPlanDao repaymentPlanDao;
    @Inject
    private LedgerDao ledgerDao;
    @Inject
    private LedgerLoanService ledgerLoanService;
    @Inject
    private LedgerLoanDao ledgerLoanDao;
    @Inject
    private ExecutorService executor;
    @Inject
    private FlowDao flowDao;
    @Inject
    private SequenceDao sequenceDao;
    @Inject
    private DebtInfoDao debtInfoDao;
    @Inject
    private WorkFlowDao workFlowDao;

    /**
     * 取现金流
     *
     * @param loanID  债权id
     * @param appDate 约定交割日期
     * @return
     * @throws Exception Bigdecimal pv = NPresentValueUtil.pV(loan.getRate(), cf);
     */
    public BigDecimal[] getBigDecimalAfterCashFLow(Long loanID, Date appDate) {
        List<RepaymentPlan> list = repaymentPlanDao.getAfterCashFLow(loanID,
                appDate);
        if (list.size() == 0) {
            return new BigDecimal[]{BigDecimal.ZERO};
        }
        try {
            return CashFlowUtil.getAfterCashFlowArray(appDate, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 功能同getBigDecimalAfterCashFLow
     *
     * @param loanId
     * @param appDate
     * @return
     * @throws Exception
     */
    public BigDecimal[] getCashFLow(Long loanId, Date appDate) throws Exception {
        List<Object[]> list = repaymentPlanDao.getCashFlow(loanId, appDate);
        if (list.size() == 0) {
            return new BigDecimal[]{BigDecimal.ZERO};
        }
        return CashFlowUtil.getCashFlowArray(appDate, list);
    }

    /**
     * 查询还款计划
     *
     * @param queryRepaymentPlanVo
     * @return Page
     */
    public Page<RepaymentPlan> queryBy(QueryRepaymentPlanVo queryRepaymentPlanVo) {
        return repaymentPlanDao.queryBy(queryRepaymentPlanVo);
    }

    public RepaymentPlan findByPayDate(Collection<RepaymentPlan> repaymentPlans, Date repayDate) {
        for (RepaymentPlan repaymentPlan : repaymentPlans) {
            if (DateUtils.isSameDay(repaymentPlan.getRepayDay(), repayDate)) {
                return repaymentPlan;
            }
        }
        return null;
    }

    /**
     * 还款(020046)
     *
     * @param repaymentVo
     * @param businessId
     */
    public void repayment(final RepaymentVo repaymentVo, long businessId) {
        Long loanId = repaymentVo.getLoanId();
        logger.info("进入还款……，loanId=" + loanId);
        Date payDate = DateUtils.parse(repaymentVo.getPayDate(), DATE_FORMAT);
        LedgerLoan ledgerLoan = ledgerLoanDao.getById(loanId);
        validateLedgerLoanAll(ledgerLoan);
        // 获取该笔债权对应的还款计划
        RepaymentPlan repaymentPlan = repaymentPlanDao.queryByLedgerLoanAndRepayDay(ledgerLoan.getId(), repaymentVo.getPayDate());
        validateRepaymentPlan(repaymentPlan);
        BigDecimal payAmt = repaymentPlan.getAmt();// 还款金额
        BigDecimal principalAmt = repaymentPlan.getPrincipalAmt();// 还款本金
        BigDecimal interestAmt = repaymentPlan.getInterestAmt();// 还款利息
        Ledger borrowerLedger = ledgerLoan.getLedger();
        Ledger companyCashLedger = ledgerDao.loadByAccount(COMPANY_CASH_LEDGER_ACCOUNT);
        ArrayList<Flow> flowList = new ArrayList<Flow>(62);
        // 充值(02004101)
        flowList.add(new EntryInfo(borrowerLedger, ACCT_TITLE_CASH, payAmt, REPAYMENT_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessId, "", DORC_TYPE_DEBIT));
        flowList.add(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, payAmt, REPAYMENT_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessId, "", DORC_TYPE_CREDIT));
        // 债权还款
        ledgerLoanService.repaymentLoan(ledgerLoan, principalAmt, interestAmt, payDate);
        // 查找持有该债权的投资明细
        Set<LedgerFinance> ledgerFinances = ledgerLoan.getValidLedgerFinances();// 获取状态有效的finance
        int batch = 0;
        for (LedgerFinance ledgerFinance : ledgerFinances) {
            Ledger creditorLedger = ledgerFinance.getLedger();// 债权人
            String flowmemo = String.valueOf(ledgerFinance.getId());
            // 总比例=持有比例+冻结比例
            BigDecimal defFrozenPorportion = NumberUtil.getBigNum(ledgerFinance.getFrozenPorportion());
            BigDecimal allProportion = ledgerFinance.getDebtProportion().add(defFrozenPorportion);
            BigDecimal detailAmt = principalAmt.multiply(allProportion);// 债权人应收本金*持有比例
            BigDecimal detailInterest = interestAmt.multiply(allProportion);// 债权人本期利息*持有比例

            // 还款投资明细
            // 债务本金=AC_T_LEDGER_FINANCE.DEBT_AMOUNT-债权人本期应收本金*持有比例
            // 当前应收利息=AC_T_LEDGER_FINANCE.INTEREST_RECEIVABLE-债权人本期应收利息*持有比例
            ledgerFinance.setDebtAmount(ledgerFinance.getDebtAmount().subtract(detailAmt));
            ledgerFinance.setInterestReceivable(ledgerFinance.getInterestReceivable().subtract(detailInterest));
            // 息转本
            BigDecimal restInterestReceivable = ledgerFinance.getInterestReceivable();// 剩余应收利息
            ledgerFinance.setDebtAmount(ledgerFinance.getDebtAmount().add(restInterestReceivable));
            ledgerLoan.setOutstanding(ledgerLoan.getOutstanding().add(restInterestReceivable));
            ledgerFinance.setInterestReceivable(BigDecimal.ZERO);
            ledgerFinance.setLastModified(new Date());
            // 还本金(02004102)
            // 3 借款金额-借款人（2007）
            flowList.add(new EntryInfo(borrowerLedger, LEDGER_DETAIL_TYPE_DEBT_BALANCE, detailAmt, PAY_PRINCIPAL).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, flowmemo, DORC_TYPE_DEBIT));

            // 4 投资金额-债权人账户(1011)
            flowList.add(new EntryInfo(creditorLedger, INVESTMENT_AMOUNT, detailAmt, PAY_PRINCIPAL).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, flowmemo, DORC_TYPE_CREDIT));

            // 5现金-债权人账户(1001)
            flowList.add(new EntryInfo(creditorLedger, ACCT_TITLE_CASH, detailAmt, PAY_PRINCIPAL).updateAmt(OPERATOR.ADD).wrap(businessId, flowmemo, DORC_TYPE_DEBIT));
            // 6现金-借款人（1001）
            flowList.add(new EntryInfo(borrowerLedger, ACCT_TITLE_CASH, detailAmt, PAY_PRINCIPAL).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, flowmemo, DORC_TYPE_CREDIT));

            // 还利息(02004103)
            // 7应付利息-借款人(2002)
            flowList.add(new EntryInfo(borrowerLedger, LEDGER_DETAIL_TYPE_PAYABLE, detailInterest, PAY_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, flowmemo, DORC_TYPE_DEBIT));

            // 8应收利息-债权人账户(1005)
            flowList.add(new EntryInfo(creditorLedger, OVERDUEINTEREST_INTERESTRECEIVABLE, detailInterest, PAY_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, flowmemo, DORC_TYPE_CREDIT));

            // 9现金-债权人账户(1001)
            flowList.add(new EntryInfo(creditorLedger, ACCT_TITLE_CASH, detailInterest, PAY_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessId, flowmemo, DORC_TYPE_DEBIT));

            // 10现金-借款人（1001)
            flowList.add(new EntryInfo(borrowerLedger, ACCT_TITLE_CASH, detailInterest, PAY_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, flowmemo, DORC_TYPE_CREDIT));

            // 11利息支出-借款人(3036)
            flowList.add(new EntryInfo(borrowerLedger, INTEREST_PAY_OUT, detailInterest, PAY_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessId, flowmemo, DORC_TYPE_DEBIT));

            // 12利息收入-债权人账户(3001)
            flowList.add(new EntryInfo(creditorLedger, INTEREST_INCOME, detailInterest, PAY_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessId, flowmemo, DORC_TYPE_CREDIT));
            // 息转本(02004104)
            // 13投资金额-债权人账户(1011)
            flowList.add(new EntryInfo(creditorLedger, INVESTMENT_AMOUNT, restInterestReceivable, INTEREST_SWITCH_PRINCIPAL).updateAmt(OPERATOR.ADD).wrap(businessId, flowmemo, DORC_TYPE_DEBIT));

            // 14应收利息-债权人账户(1005)
            flowList.add(new EntryInfo(creditorLedger, OVERDUEINTEREST_INTERESTRECEIVABLE, restInterestReceivable, INTEREST_SWITCH_PRINCIPAL).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, flowmemo, DORC_TYPE_CREDIT));
            if (++batch == 5) {
                executor.execute(new FlowTask(flowList));
                flowList = new ArrayList<Flow>(60);
                batch = 0;
            }

        }
        if (flowList.size() > 0) {
            executor.execute(new FlowTask(flowList));
        }
    }

    /**
     * 新建还款计划
     *
     * @param ledgerLoan
     * @param repaymentPlans<RepaymentPlanVo>
     *
     */
    public void save(LedgerLoan ledgerLoan, Set<RepaymentPlanVo> repaymentPlans) {
        for (RepaymentPlanVo repaymentPlanVo : repaymentPlans) {
            RepaymentPlan repaymentPlan = new RepaymentPlan();
            repaymentPlan.setLedgerLoan(ledgerLoan);
            repaymentPlan.setCreateDate(new Date()); // 创建日期
            ObjectHelper.copy(repaymentPlanVo, repaymentPlan);
            ledgerLoan.getRepaymentPlans().add(repaymentPlan);
            repaymentPlanDao.save(repaymentPlan);
        }
    }

    /**
     * 提前结清020047
     * 
     * @param earlySettlementVo
     * @param businessInfo
     */
    public void earlySettlement(EarlySettlementVo earlySettlementVo, BusinessInfo businessInfo){
    	Long loanId = earlySettlementVo.getLoanId();//债权编号
    	logger.info("进入债权提前结清，债权编号:" + loanId);
		BigDecimal settleAmount = earlySettlementVo.getSettleAmount();//结算金额
		String settleAmountMemo = earlySettlementVo.getSettleAmountMemo();//结算金额备注
		Date settleDate = DateUtils.parse(earlySettlementVo.getSettleDate(), DATE_FORMAT);
		//查询债权
		LedgerLoan ledgerLoan = ledgerLoanDao.getById(loanId);
		//校验债权
		ValidateUtil.validateLedgerLoanAll(ledgerLoan);
		//债务人分户
		Ledger borrowerLedger = ledgerLoan.getLedger();
		//校验债务人分户
		ValidateUtil.validateLedger(borrowerLedger, Constants.BUSINESS_TYPE_LOAN);
		Ledger companyCashLedger = ledgerDao.loadByAccount(COMPANY_CASH_LEDGER_ACCOUNT);
		// 充值(02004701)
		long flowGroupNo = sequenceDao.nextFlowGroupNo();
		flowDao.save(new EntryInfo(borrowerLedger, ACCT_TITLE_CASH, settleAmount, EARLYSETTLE_CASH).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), "", getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
		flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, settleAmount, EARLYSETTLE_CASH).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), "", getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
		//查询持有该债权的有效投资明细
		Set<LedgerFinance> ledgerFinances = ledgerLoan.getValidLedgerFinances();
		for(LedgerFinance ledgerFinance : ledgerFinances){
			Ledger creditorledger = ledgerFinance.getLedger();//债权人
			//总持有比例 = 持有比例+冻结比例
			BigDecimal allPropertion = ledgerFinance.getDebtProportion().add(NumberUtil.getBigNum(ledgerFinance.getFrozenPorportion()));
			//结算金额
			BigDecimal amount = settleAmount.multiply(allPropertion);
			//金额结算
			flowGroupNo = sequenceDao.nextFlowGroupNo();
			//现金（1001） -债务人
			flowDao.save(new EntryInfo(borrowerLedger, ACCT_TITLE_CASH, amount, EARLYSETTLE_REPAYMENT).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), settleAmountMemo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
			//现金（1001） -债权人
			flowDao.save(new EntryInfo(creditorledger, ACCT_TITLE_CASH, amount, EARLYSETTLE_REPAYMENT).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), settleAmountMemo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
			
			//提前结清-逾期付款付息
			latePaymentInterest(creditorledger, ledgerFinance.getId(), settleDate, businessInfo);
			
			String startValue = ledgerFinance.getAcctStatus();
			//理财分户状态更新为 5-提前结清
			ledgerFinance.setAcctStatus(ACCOUNT_STATUS_EARLYSTL);
			ledgerFinance.setLastModified(new Date());
			//写事务流水
			workFlowDao.workFlowSave(businessInfo.getId(), null, ledgerFinance.getId(), startValue, null, ledgerFinance.getAcctStatus(), sequenceDao.nextWorkFlowNo(), LEDGER_FINANCE, businessInfo.getBusinessTypeId());
		}
		//债权提前结清
		ledgerLoanService.earlySettleLoan(ledgerLoan, settleAmount, settleDate, businessInfo);
    }
    
    /**
     * 提前结清处理逾期应收应付
     * 
     * @param buyerLedger 买方
     * @param financeId 债权人理财分户id
     * @param settleDate 日期
     */
    private void latePaymentInterest(Ledger buyerLedger, Long financeId, Date settleDate, BusinessInfo businessInfo){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("financeId", financeId);
    	map.put("status", UNEXECTUED);
    	List<Debt> list = debtInfoDao.findByParamMap(map);
    	if(null != list && !list.isEmpty()){
    		for(Debt debt : list){
    			if(!debt.getAccount().equals(buyerLedger.getAccount())){
    				logger.info("理财分户负债帐号不属于同一个买方分账");
                    throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerFinance.debt.account.not.same"));
    			}
    			Ledger apposedAcctLedger = ledgerDao.loadByAccount(debt.getApposedAcct());//负债信息-卖方
    			
    			BigDecimal amount = debt.getAmount();//逾期金额
    			String memo = debt.getId() + "";

    			long flowGroupNo = sequenceDao.nextFlowGroupNo();
    			//买方-应付逾期利息  卖方-应收逾期利息
    			flowDao.save(new EntryInfo(buyerLedger, OVERDUEINTEREST_PAYABLE, amount, EARLYSETTLE_LATE_PAYMENT_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
    			flowDao.save(new EntryInfo(apposedAcctLedger, OVERDUEINTEREST_RECEIVABLE, amount, EARLYSETTLE_LATE_PAYMENT_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
    			
    			//卖方-现金  买方-现金
    			flowDao.save(new EntryInfo(apposedAcctLedger, ACCT_TITLE_CASH, amount, EARLYSETTLE_LATE_PAYMENT_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
    			flowDao.save(new EntryInfo(buyerLedger, ACCT_TITLE_CASH, amount, EARLYSETTLE_LATE_PAYMENT_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
    			
    			//买方-利息收入  卖方-利息收入
    			flowDao.save(new EntryInfo(buyerLedger, INTEREST_INCOME, amount, EARLYSETTLE_LATE_PAYMENT_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
    			flowDao.save(new EntryInfo(apposedAcctLedger, INTEREST_INCOME, amount, EARLYSETTLE_LATE_PAYMENT_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
    			
    			debt.setStatus(EXECTUED);//标注状态为“已执行”
    			debt.setExDate(settleDate);//执行时间
    			
    			workFlowDao.workFlowSave(businessInfo.getId(), null, debt.getId(), null, null, null, sequenceDao.nextWorkFlowNo(), "AC_T_DEBT", businessInfo.getBusinessTypeId());
    		}
    	}
    }

    private String getFlowNo() {
        return sequenceDao.nextFlowNO();
    }
}