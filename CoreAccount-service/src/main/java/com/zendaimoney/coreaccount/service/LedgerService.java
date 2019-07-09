package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.constants.EntryNo;
import com.zendaimoney.coreaccount.constants.Subject;
import com.zendaimoney.coreaccount.dao.*;
import com.zendaimoney.coreaccount.entity.*;
import com.zendaimoney.coreaccount.rmi.vo.*;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.util.*;
import com.zendaimoney.coreaccount.vo.EntryInfo;
import com.zendaimoney.coreaccount.vo.EntryInfo.OPERATOR;
import com.zendaimoney.exception.BusinessException;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import javax.inject.Inject;
import javax.inject.Named;

import java.math.BigDecimal;
import java.util.*;

import static com.zendaimoney.coreaccount.constants.Constants.*;
import static com.zendaimoney.coreaccount.constants.EntryNo.*;
import static com.zendaimoney.coreaccount.constants.Subject.*;
import static com.zendaimoney.coreaccount.vo.EntryInfo.OPERATOR.ADD;
import static com.zendaimoney.coreaccount.vo.EntryInfo.OPERATOR.SUBTRACT;
import static java.math.BigDecimal.ROUND_HALF_UP;

@Named
@Transactional
public class LedgerService {
    private Logger logger = Logger.getLogger(getClass());
    @Inject
    private SysglService sysglService;
    @Inject
    private LedgerDao ledgerDao;
    @Inject
    private CustomerDao customerDao;
    @Inject
    private SequenceDao sequenceDao;
    @Inject
    private LedgerLoanDao ledgerLoanDao;
    @Inject
    private LedgerFinanceDao ledgerFinanceDao;
    @Inject
    private FlowDao flowDao;
    @Inject
    private AccountManagerService accountManagerService;
    @Inject
    private DebtInfoDao debtInfoDao;
    @Inject
    private LedgerFinanceService ledgerFinanceService;
    @Inject
    private PvService pvService;
    @Inject
    private WorkFlowDao workFlowDao;

    /**
     * 新建分账(010002:理财分账,010003:贷款分账)
     *
     * @param datagram
     * @return
     */
    public String createAccount(Datagram datagram) {
        LedgerVo ledgerVo = (LedgerVo) datagram.getDatagramBody();
        String messageCode = datagram.getDatagramHeader().getMessageCode();
        String busiType, errCode;
        if (MESSAGE_BUSINESS_TYPE_FINANCING.equals(messageCode)) {
            busiType = BUSINESS_TYPE_FINANCING;
            errCode = "customer.finance.not.open";
        } else {
            busiType = BUSINESS_TYPE_LOAN;
            errCode = "customer.loan.not.open";
        }
        Customer customer = customerDao.findUniqueBy("totalAcct", ledgerVo.getTotalAccountId());
        if (null == customer) {
            ledgerVo.setOperateTime(DateFormatUtils.format(Calendar.getInstance(), Constants.DATE_FORMAT));
            ledgerVo.setMemo(PropertiesReader.readAsString(errCode));
            ledgerVo.setOperateCode(Constants.PROCESS_STATUS_FAIL);

            logger.info(PropertiesReader.readAsString(errCode));
            return JsonHelper.toJson(datagram);
        }
        Ledger ledger = new Ledger();
        ObjectHelper.copy(ledgerVo, ledger);
        ledger.setCustomer(customer);
        ledger.setBusiType(busiType);
        /** 账户状态 */
        ledger.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);

        int count = ledgerDao.getRowCountBy(ledgerVo.getTotalAccountId());
        // 共用同一个总账号的分账号集
        Set<String> existAcct = ledgerDao.queryLedgerAccountBy(ledgerVo.getTotalAccountId());
        /** 生成分账号 */
        ledger.setAccount(accountManagerService.generateAccount(ledgerVo.getTotalAccountId(), existAcct, count + 1));
        ledgerDao.save(ledger);
        sysglService.maintainMainAccount();
        /** 返回报文时，设置生成的分账号 */
        ((LedgerVo) datagram.getDatagramBody()).setAccount(ledger.getAccount());
        /** 操作时间 */
        ledgerVo.setOperateTime(DateFormatUtils.format(Calendar.getInstance(), Constants.DATE_FORMAT));
        /** 操作码 */
        ledgerVo.setOperateCode(Constants.PROCESS_STATUS_OK);

        logger.info("新建理财分账成功！");
        return JsonHelper.toJson(datagram);
    }

    /**
     * 查询分账
     *
     * @param queryObligationsVo
     * @return Page<Ledger>
     */
    public Page<Ledger> queryLedger(QueryObligationsVo queryObligationsVo) {
        Page<Ledger> page = ledgerDao.queryObligationsPage(queryObligationsVo);
        logger.debug("分账查询成功！");
        return page;
    }

    /**
     * 充值
     *
     * @param rechargeVo
     * @param businessId
     * @return null
     */
    public void recharge(RechargeVo rechargeVo, long businessId) {
        String account = rechargeVo.getAccount();
        BigDecimal rechargeAmount = rechargeVo.getRechargeAmount();
        BigDecimal rechargeCommission = rechargeVo.getRechargeCommission();
        String rechargeAmountMemo = rechargeVo.getRechargeAmountMemo();
        String rechargeCommissionMemo = rechargeVo.getRechargeCommissionMemo();
        Ledger customerLedger = ledgerDao.loadByAccount(account);
        ValidateUtil.validateLedger(customerLedger, null);
        ValidateUtil.validateLedgerAmount(rechargeCommission, customerLedger.getAmount().add(rechargeAmount));
        Ledger companyCashLedger = ledgerDao.loadByAccount(COMPANY_CASH_LEDGER_ACCOUNT);
        Ledger companyLedger = ledgerDao.loadByAccount(COMPANY_LEDGER_ACCOUNT);
        ValidateUtil.validateCompanyCashLedgerAmount(rechargeCommission, companyCashLedger.getDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE).add(rechargeAmount));

        long flowGroupNo = sequenceDao.nextFlowGroupNo();
        // 充值金额现金-客户账户
        flowDao.save(new EntryInfo(customerLedger, ACCT_TITLE_CASH, rechargeAmount, RECHARGE_AMOUNT).updateAmt(OPERATOR.ADD).wrap(businessId, rechargeAmountMemo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
        // 充值金额 添加 预收款项-公司现金账户
        flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, rechargeAmount, RECHARGE_AMOUNT).updateAmt(OPERATOR.ADD).wrap(businessId, rechargeAmountMemo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
        // 手续费 处理
        this.chargeFee(companyCashLedger, companyLedger, businessId, rechargeCommission, rechargeCommissionMemo, customerLedger, Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE, Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME, EntryNo.RECHARGE_COMMISSION);
    }

    /**
     * 取现业务
     *
     * @param enchashmentVo (取现参数)
     * @param businessId
     */
    public void withdrawCash(final EnchashmentVo enchashmentVo, long businessId) {
        String account = enchashmentVo.getAccount();
        BigDecimal withdrawAmt = enchashmentVo.getAmount();
        BigDecimal chargeAmt = enchashmentVo.getChargeAmount();
        String chargeMemo = enchashmentVo.getChargeMemo();
        String withdrawMemo = enchashmentVo.getEnchashmentMemo();
        Ledger ledger = ledgerDao.loadByAccount(account);
        ValidateUtil.validateLedger(ledger, null);
        ValidateUtil.validateLedgerAmount(withdrawAmt.add(chargeAmt), ledger.getAmount());
        Ledger companyCashLedger = ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
        Ledger companyLedger = ledgerDao.loadByAccount(Constants.COMPANY_LEDGER_ACCOUNT);
        ValidateUtil.validateCompanyCashLedgerAmount(withdrawAmt.add(chargeAmt), companyCashLedger.getDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));

        long cashGroupNo = sequenceDao.nextFlowGroupNo();
        // 预收款项-公司现金账户
        flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, withdrawAmt, WITHDRAWAL).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, withdrawMemo, getFlowNo(), cashGroupNo, DORC_TYPE_DEBIT));
        // 现金-客户账户
        flowDao.save(new EntryInfo(ledger, ACCT_TITLE_CASH, withdrawAmt, WITHDRAWAL).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, withdrawMemo, getFlowNo(), cashGroupNo, DORC_TYPE_CREDIT));
        // 取现手续费
        this.chargeFee(companyCashLedger, companyLedger, businessId, chargeAmt, chargeMemo, ledger, WITHDRAWAL_CHARGE_PAY, WITHDRAWAL_CHARGE_INCOME, WITHDRAWAL_COMMISSION);

    }

    /**
     * 原始债权交易
     *
     * @param originalLoanTradeVo
     * @param businessId
     */
    public void originalLoanTrade(OriginalLoanTradeVo originalLoanTradeVo, long businessId) {
        String account = originalLoanTradeVo.getAccount();
        long loanId = originalLoanTradeVo.getLoanId();
        BigDecimal amount = originalLoanTradeVo.getAmount();
        String amountMemo = originalLoanTradeVo.getAmountMemo();
        BigDecimal unfreezeAmount = originalLoanTradeVo.getUnfreezeAmount();
        String unfreezeAmountMemo = originalLoanTradeVo.getUnfreezeAmountMemo();
        BigDecimal debtProportion = originalLoanTradeVo.getDebtProportion();
        String tradeMemo = originalLoanTradeVo.getTradeMemo();
        Ledger customerLedger = ledgerDao.loadByAccount(account);
        ValidateUtil.validateLedger(customerLedger, Constants.BUSINESS_TYPE_FINANCING);

        LedgerLoan ledgerLoan = ledgerLoanDao.getById(loanId);
        ValidateUtil.validateLedgerLoan(ledgerLoan);
        ValidateUtil.validateUnfreezeAmount(unfreezeAmount, NumberUtil.getBigNum(customerLedger.getFrozenAmt()));
        ValidateUtil.validateLedgerAmount(amount, NumberUtil.getBigNum(customerLedger.getAmount()).add(unfreezeAmount));

        // 解冻
        unfreeze(businessId, customerLedger, unfreezeAmount, unfreezeAmountMemo, EntryNo.ORIGINALLOANTRADE_UNFREEZE);

        // 生成 理财分户信息 记录
        LedgerFinance ledgerFinance = new LedgerFinance();
        ledgerFinance.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        ledgerFinance.setDebtAmount(amount);
        //逾期垫付债权导入时判断是否为逾期，如果是那么债权本金设置子债权的贷款余额
        if(ledgerLoan.getFatherLoanId()!=null){
            ledgerFinance.setDebtAmount(ledgerLoan.getOutstanding());
        }
        ledgerFinance.setDebtProportion(debtProportion);
        ledgerFinance.setIntersetStart(ledgerLoan.getInterestStart());
        ledgerFinance.setLedger(customerLedger);
        ledgerFinance.setLedgerLoan(ledgerLoan);
        ledgerFinance.setMemo(tradeMemo);
        ledgerFinance.setInterestReceivable(BigDecimal.ZERO);
        ledgerFinance.setInterestDeviation(BigDecimal.ZERO);
        ledgerFinance.setFrozenPorportion(BigDecimal.ZERO);
        ledgerFinanceDao.save(ledgerFinance);
//        customerLedger.getLedgerFinances().add(ledgerFinance);

        Ledger sellLedger = ledgerLoan.getLedger();
        long groupNo = sequenceDao.nextFlowGroupNo();
        // 1、债权人分账明细明细类别为"投资金额"的明细值 加上 交易金额，AC_T_LEDGER_DETAIL.DETAIL_VALUE
        flowDao.save(new EntryInfo(customerLedger, INVESTMENT_AMOUNT, amount, ORIGINALLOANTRADE_TRADE).updateAmt(ADD).wrap(businessId, amountMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 2、借款人分账明细中明细类别为"借款金额"的明细值 加上 交易金额，AC_T_LEDGER_DETAIL.DETAIL_VALUE
        flowDao.save(new EntryInfo(sellLedger, LEDGER_DETAIL_TYPE_DEBT_BALANCE, amount, ORIGINALLOANTRADE_TRADE).updateAmt(ADD).wrap(businessId, amountMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

        groupNo = sequenceDao.nextFlowGroupNo();
        // 3、借款人分账信息金额 加上 交易金额，AC_T_LEDGER.AMOUNT*/
        flowDao.save(new EntryInfo(sellLedger, ACCT_TITLE_CASH, amount, ORIGINALLOANTRADE_TRADE).updateAmt(ADD).wrap(businessId, amountMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 4、债权人分账信息金额 减去 交易金额，AC_T_LEDGER.AMOUNT
        flowDao.save(new EntryInfo(customerLedger, ACCT_TITLE_CASH, amount, ORIGINALLOANTRADE_TRADE).updateAmt(SUBTRACT).wrap(businessId, amountMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
    }

    /**
     * 回归债权交易030008
     *
     * @param regressLoanTradeVo
     * @param businessId
     */
    public void regressLoanTrade(RegressLoanTradeVo regressLoanTradeVo, long businessId) {
    	String account = regressLoanTradeVo.getAccount();//买方
        long loanId = regressLoanTradeVo.getLoanId();
        BigDecimal amount = regressLoanTradeVo.getAmount();//债权价值
        BigDecimal unfreezeAmount = regressLoanTradeVo.getUnfreezeAmount();//解冻金额
        String unfreezeAmountMemo = regressLoanTradeVo.getUnfreezeAmountMemo();
        BigDecimal debtProportion = regressLoanTradeVo.getDebtProportion();//比例
        String tradeMemo = regressLoanTradeVo.getTradeMemo();
        Ledger customerLedger = ledgerDao.loadByAccount(account);
        ValidateUtil.validateLedger(customerLedger, null);

        LedgerLoan ledgerLoan = ledgerLoanDao.getById(loanId);
        ValidateUtil.validateLedgerLoanAll(ledgerLoan);
        ValidateUtil.validateUnfreezeAmount(unfreezeAmount, NumberUtil.getBigNum(customerLedger.getFrozenAmt()));
        ValidateUtil.validateLedgerAmount(unfreezeAmount, NumberUtil.getBigNum(customerLedger.getAmount()).add(unfreezeAmount));

        // 解冻
        unfreeze(businessId, customerLedger, unfreezeAmount, unfreezeAmountMemo, EntryNo.REGRESSLOANTRADE_UNFREEZE);

        Set<RepaymentPlan> repaymentPlans = ledgerLoan.getRepaymentPlans();
        long currNum = ledgerLoan.getCurrNum();// 当前期数
        BigDecimal initAmount = amount; // 导入日PV
        BigDecimal payOutStanding = BigDecimal.ZERO; // 应付剩余本金
        BigDecimal payInterest = BigDecimal.ZERO; // 应付利息
        BigDecimal lastExpiryPV; // 上一回款日PV
        BigDecimal dateInterval1; // 总的回款周期长度
        BigDecimal dateInterval2; // 当前时间长度
        Date initDate = SystemUtil.currentDate();// 初始化日期
        BigDecimal receiveOutStanding; // 应收剩余本金
        BigDecimal receiveInterest; // 应收利息
        BigDecimal totalDeviation; // 总误差

        if (currNum == 0) {// 首期
            payOutStanding = ledgerLoan.getLoan();
            dateInterval1 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(ledgerLoan.getInterestStart(), ledgerLoan.getNextExpiry()));
            dateInterval2 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(ledgerLoan.getInterestStart(), initDate));
            lastExpiryPV = ledgerLoan.getLoan();
            RepaymentPlan repaymentPlan = ledgerLoan.getFirstRepaymentPlan();
            payInterest = Arith.mul(Arith.div(repaymentPlan.getInterestAmt(), dateInterval1), dateInterval2);
        }else {// 非首期
            dateInterval1 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(ledgerLoan.getLastExpiry(), ledgerLoan.getNextExpiry()));
            dateInterval2 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(ledgerLoan.getLastExpiry(), initDate));
            lastExpiryPV = pvService.getPvAlways(ledgerLoan, Boolean.TRUE);
            for (RepaymentPlan repaymentPlan : repaymentPlans) {
                if (repaymentPlan.getCurrNum() == currNum - 1L) {
                    payOutStanding = repaymentPlan.getOutstanding();
                } else if (repaymentPlan.getCurrNum() == currNum) {
                    payInterest = Arith.mul(Arith.div(repaymentPlan.getInterestAmt(), dateInterval1), dateInterval2);
                }
            }
        }
        // 记应收剩余本+应收利息
        // 应收剩余本金 = 上一回款日pv 应收利息= 初始金额-应收剩余本金 总误差= 应付利息+应付剩余本金-应收利息-应收剩余本金
        receiveOutStanding = lastExpiryPV;
        receiveInterest = Arith.sub(initAmount, receiveOutStanding);
        totalDeviation = Arith.sub(Arith.add(payOutStanding, payInterest), initAmount);

		/*
         * 更新贷款分户信息表 AC_T_LEDGER_LOAN.AMOUNT_SPARE=PV
		 * 借款人应付利息AC_T_LEDGER_LOAN.INTEREST_PAYABLE=应付利息
		 * AC_T_LEDGER_LOAN.OUTSTANDING = 应收剩余本金
		 */
        ledgerLoan.setAmountSpare(initAmount);
        ledgerLoan.setInterestPayable(payInterest);
        ledgerLoan.setOutstanding(receiveOutStanding);

		/*
         * 添加一条AC_T_LEDGER_FINANCE记录: 状态，债务本金=应收剩余本金，持有比例，起息日期， 当前应收利息=应收利息，利息误差
		 * =总误差, 债权编号，分帐id，备注
		 */

        LedgerFinance ledgerFinance = new LedgerFinance();
        ledgerFinance.setLedger(customerLedger);
        ledgerFinance.setLedgerLoan(ledgerLoan);
        ledgerFinance.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        ledgerFinance.setDebtAmount(receiveOutStanding);
        ledgerFinance.setDebtProportion(debtProportion);
        ledgerFinance.setIntersetStart(ledgerLoan.getInterestStart());
        ledgerFinance.setInterestReceivable(receiveInterest);
        ledgerFinance.setInterestDeviation(totalDeviation);
        ledgerFinance.setFrozenPorportion(BigDecimal.ZERO);
        ledgerFinance.setMemo(tradeMemo);
//        externalLedger.getLedgerFinances().add(ledgerFinance);
        ledgerFinanceDao.save(ledgerFinance);
        
        // 本
        long groupNo = sequenceDao.nextFlowGroupNo();
        // 1、债权人分账明细明细类别为"投资金额"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE =应收剩余本金
        flowDao.save(new EntryInfo(customerLedger, INVESTMENT_AMOUNT, receiveOutStanding, REGRESSLOANTRADE_DEBT_AMOUNT).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 2 借款人分账明细明细类别为"借款金额"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE = 应付剩余本金
        flowDao.save(new EntryInfo(ledgerLoan.getLedger(), LEDGER_DETAIL_TYPE_DEBT_BALANCE, payOutStanding, REGRESSLOANTRADE_DEBT_AMOUNT).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

        // 息
        groupNo = sequenceDao.nextFlowGroupNo();
        // 3、债权人分账明细明细类别为"应收利息"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE =应收利息
        flowDao.save(new EntryInfo(customerLedger, OVERDUEINTEREST_INTERESTRECEIVABLE, receiveInterest, REGRESSLOANTRADE_INTEREST).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 4、债权人分账明细明细类别为"利息误差"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE =总误差
        flowDao.save(new EntryInfo(customerLedger, LEDGER_DETAIL_TYPE_DEVIATION, totalDeviation, REGRESSLOANTRADE_INTEREST).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 5、 借款人分账明细明细类别为"应付利息"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE = 应付利息
        flowDao.save(new EntryInfo(ledgerLoan.getLedger(), LEDGER_DETAIL_TYPE_PAYABLE, payInterest, REGRESSLOANTRADE_INTEREST).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

        // 现金
        groupNo = sequenceDao.nextFlowGroupNo();
        // 6、现金-借款人分账信息金额 加上 交易金额，AC_T_LEDGER.AMOUNT*/
        flowDao.save(new EntryInfo(ledgerLoan.getLedger(), ACCT_TITLE_CASH, amount, REGRESSLOANTRADE_CASH).updateAmt(ADD).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 7、现金-债权人分账信息金额 减去 交易金额，AC_T_LEDGER.AMOUNT
        flowDao.save(new EntryInfo(customerLedger, ACCT_TITLE_CASH, amount, REGRESSLOANTRADE_CASH).updateAmt(SUBTRACT).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
    }
    /**
     * 债权转让030003
     *
     * @param debtAssignmentsVo
     * @param businessInfo
     */
    public void debtAssignment(DebtAssignmentsVo debtAssignmentsVo, BusinessInfo businessInfo) {
        BigDecimal frozenAmount = debtAssignmentsVo.getFrozenAmount();
        String frozenAmountMemo = debtAssignmentsVo.getFrozenAmountMemo();
        BigDecimal unfreezeAmount = debtAssignmentsVo.getUnfreezeAmount();
        String unfreezeAmountMemo = debtAssignmentsVo.getUnfreezeAmountMemo();
        BigDecimal managementFee = debtAssignmentsVo.getManagementFee();
        String managementFeeMemo = debtAssignmentsVo.getManagementFeeMemo();
        BigDecimal fixProduTranSerFee = debtAssignmentsVo.getFixProduTranSerFee();
        String fixProduTranSerFeeMemo = debtAssignmentsVo.getFixProduTranSerFeeMemo();
        BigDecimal urgSerFee = debtAssignmentsVo.getUrgSerFee();
        String urgSerFeeMemo = debtAssignmentsVo.getUrgSerFeeMemo();
        //买方分账
        Ledger buyLedger = ledgerDao.loadByAccount(debtAssignmentsVo.getBuyerAccount());
        ValidateUtil.validateLedger(buyLedger, Constants.BUSINESS_TYPE_FINANCING);
        ValidateUtil.validateUnfreezeAmount(unfreezeAmount, buyLedger.getFrozenAmt());

        BigDecimal allAmount = BigDecimal.ZERO;// 实际交易总额：参数[约定交割日PV] * 买方持有比例
        BigDecimal allFees = managementFee.add(fixProduTranSerFee).add(urgSerFee);
        List<DebtAssignmentVo> debtAssignments = debtAssignmentsVo.getDebtAssignments();
        Ledger sellLedger = null;

        // Map<financeId, sum(frozenPorportion)>
        // 参与交易的 某一financeId解冻比例之和, 同一financeId出现多次的状况
        Map<Long, BigDecimal> unfreezeProportions = new HashMap<Long, BigDecimal>();

        // Map<financeId, sum(debtProportion)>
        // 参与交易的 某一financeId买方持有比例和, 同一financeId出现多次的状况
        Map<Long, BigDecimal> debtProportions = new HashMap<Long, BigDecimal>();
        List<LedgerFinance> ledgerFinanceCom = new ArrayList<LedgerFinance>();
        for (DebtAssignmentVo debtAssignmentVo : debtAssignments) {
            // finance，状态，总金额， 总持有比
            long financeId = debtAssignmentVo.getFinanceId();//债权持有人
            BigDecimal debtProportion = debtAssignmentVo.getDebtProportion();//债权交易比例
            allAmount = allAmount.add(debtProportion.multiply(debtAssignmentVo.getContractDeliveryDatePV()));//约定交割日债权价值

            LedgerFinance ledgerFinance = ledgerFinanceDao.getById(financeId);
            ValidateUtil.validateLedgerFinance(ledgerFinance, financeId);

            if (sellLedger == null) {
                sellLedger = ledgerFinance.getLedger();
            }
            if (sellLedger.getId() != ledgerFinance.getLedger().getId()) {
                logger.info("理财分户不属于同一个卖方分账");
                throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerFinance.ledger.not.same"));
            }

            ledgerFinanceCom.add(ledgerFinance);
            BigDecimal sumUnfreezeProportion = NumberUtil.getBigNum(unfreezeProportions.get(financeId)).add(debtAssignmentVo.getUnfreezeProportion());//解冻比例
            ValidateUtil.validateUnfreezeProportions(sumUnfreezeProportion, NumberUtil.getBigNum(ledgerFinance.getFrozenPorportion()), financeId);
            unfreezeProportions.put(financeId, sumUnfreezeProportion);

            BigDecimal sumdebtProportion = NumberUtil.getBigNum(debtProportions.get(financeId)).add(debtProportion);//债权交易比例总和
            ValidateUtil.validateFrozenProportions(sumdebtProportion, NumberUtil.getBigNum(ledgerFinance.getDebtProportion()).add(sumUnfreezeProportion), financeId);

            debtProportions.put(financeId, sumdebtProportion);
        }
        ValidateUtil.validateLedgerAmount(allAmount, buyLedger.getAmount().add(unfreezeAmount));
        BigDecimal tempAmount = sellLedger.getAmount().add(allAmount);
        //检查卖方理财分账的账面金额是否足够支付各项手续费
        ValidateUtil.validateLedgerAmountForId(allFees, tempAmount);
        //检查卖方理财分账的账户金额在减去各项服务费后是否足够冻结
        ValidateUtil.validateFrozenAmount(frozenAmount, tempAmount.subtract(allFees));

        // 解冻(现金-买方 冻结类现金-买方)
        unfreeze(businessInfo.getId(), buyLedger, unfreezeAmount, unfreezeAmountMemo, DEBTASSIGNMENT_UNFREEZE);

        int listIndex = 0;
        for (DebtAssignmentVo debtAssVo : debtAssignments) {
            // finance，状态，总金额， 总持有比
            long financeId = debtAssVo.getFinanceId();
            BigDecimal contractDeliveryDatePV = debtAssVo.getContractDeliveryDatePV();//约定交割日PV
            BigDecimal deliveryDatePV = debtAssVo.getDeliveryDatePV();//实际交割日PV
            BigDecimal debtProportion = debtAssVo.getDebtProportion();//交易比例
            BigDecimal unfreezeProportion = debtAssVo.getUnfreezeProportion();//解冻比例

            LedgerFinance sellLedgerFinance = (LedgerFinance) ledgerFinanceCom.get(listIndex++);
            // 卖方finance的总持有比例 = 持有比例 + 冻结比例
            BigDecimal financeAllDebtProportion = sellLedgerFinance.getDebtProportion().add(sellLedgerFinance.getFrozenPorportion());
            // 交易债权本金
            BigDecimal tradeDebtAmount = sellLedgerFinance.getLedgerLoan().getOutstanding().multiply(debtProportion);
            // 交易利息,约定交割日利息: 约定交割日PV-loan.“贷款余额-当前应付本金"
            BigDecimal tradeInterest = contractDeliveryDatePV.multiply(debtProportion).subtract(tradeDebtAmount);
            // 交易部分 当前应收利息,必须放在这里，以finance未重设之前计算
            BigDecimal tradeInterestReceivable = Arith.div(NumberUtil.getBigNum(sellLedgerFinance.getInterestReceivable()), financeAllDebtProportion).multiply(debtProportion);
            // 交易部分 当前应收利息误差
            BigDecimal interestDeviation = Arith.div(sellLedgerFinance.getInterestDeviation(), financeAllDebtProportion).multiply(debtProportion);
            WorkFlow debtProportionF = new WorkFlow();
            debtProportionF.setBusinessId(businessInfo.getId());
            debtProportionF.setBusinessTypeId(businessInfo.getBusinessTypeId());
            debtProportionF.setStartValue(String.valueOf(sellLedgerFinance.getDebtProportion()));
            debtProportionF.setFlowNo(sequenceDao.nextWorkFlowNo());
            debtProportionF.setMemo("DEBT_PROPORTION");
            debtProportionF.setObjectId(sellLedgerFinance.getId());
            debtProportionF.setTradeValue(String.valueOf(unfreezeProportion.subtract(debtProportion)));

            WorkFlow frozenPorportionF = new WorkFlow();
            frozenPorportionF.setBusinessId(businessInfo.getId());
            frozenPorportionF.setBusinessTypeId(businessInfo.getBusinessTypeId());
            frozenPorportionF.setStartValue(String.valueOf(sellLedgerFinance.getFrozenPorportion()));
            frozenPorportionF.setFlowNo(sequenceDao.nextWorkFlowNo());
            frozenPorportionF.setMemo("FROZEN_PORPORTION");
            frozenPorportionF.setObjectId(sellLedgerFinance.getId());
            frozenPorportionF.setTradeValue(String.valueOf(unfreezeProportion.negate()));

            WorkFlow debtAmountF = new WorkFlow();
            debtAmountF.setBusinessId(businessInfo.getId());
            debtAmountF.setBusinessTypeId(businessInfo.getBusinessTypeId());
            debtAmountF.setStartValue(Strings.truncate(sellLedgerFinance.getDebtAmount().toPlainString()));
            debtAmountF.setFlowNo(sequenceDao.nextWorkFlowNo());
            debtAmountF.setMemo("DEBT_AMOUNT");
            debtAmountF.setObjectId(sellLedgerFinance.getId());
            debtAmountF.setTradeValue(Strings.truncate(tradeDebtAmount.negate().toPlainString()));

            WorkFlow interestReceivableF = new WorkFlow();
            interestReceivableF.setBusinessId(businessInfo.getId());
            interestReceivableF.setBusinessTypeId(businessInfo.getBusinessTypeId());
            interestReceivableF.setStartValue(Strings.truncate(sellLedgerFinance.getInterestReceivable().toPlainString()));
            interestReceivableF.setFlowNo(sequenceDao.nextWorkFlowNo());
            interestReceivableF.setMemo("INTEREST_RECEIVABLE");
            interestReceivableF.setObjectId(sellLedgerFinance.getId());
            interestReceivableF.setTradeValue(Strings.truncate(tradeInterestReceivable.negate().toPlainString()));

            WorkFlow interestDeviationF = new WorkFlow();
            interestDeviationF.setBusinessId(businessInfo.getId());
            interestDeviationF.setBusinessTypeId(businessInfo.getBusinessTypeId());
            interestDeviationF.setStartValue(Strings.truncate(sellLedgerFinance.getInterestDeviation().toPlainString()));
            interestDeviationF.setFlowNo(sequenceDao.nextWorkFlowNo());
            interestDeviationF.setMemo("INTEREST_DEVIATION");
            interestDeviationF.setObjectId(sellLedgerFinance.getId());
            interestDeviationF.setTradeValue(Strings.truncate(interestDeviation.negate().toPlainString()));
            
            // 如买方是居间人，查询买方持有待转让债权信息，按持有债权本金降序
            List<LedgerFinance> buyLedgerFinances = new ArrayList<LedgerFinance>();
            if(Constants.loanMergeAccounts.contains(buyLedger.getAccount())){
            	long loanId = sellLedgerFinance.getLedgerLoan().getId();
            	long ledgerId = buyLedger.getId();
            	buyLedgerFinances = ledgerFinanceService.queryLedgerFinances(loanId, ledgerId);
            }
            
            // 投资明细转让
            LedgerFinance buyLedgerFinance = ledgerFinanceService.tradeFinance(sellLedgerFinance, buyLedger, tradeDebtAmount, tradeInterestReceivable, interestDeviation, debtProportion, unfreezeProportion, buyLedgerFinances, businessInfo);
            if (buyLedgerFinance.getId() == 0) {
                ledgerFinanceDao.save(buyLedgerFinance);
                WorkFlow required = new WorkFlow();
                required.setBusinessId(businessInfo.getId());
                required.setBusinessTypeId(businessInfo.getBusinessTypeId());
                required.setObjectId(buyLedgerFinance.getId());
                required.setMemo("AC_T_LEDGER_FINANCE");
                required.setFlowNo(sequenceDao.nextWorkFlowNo());
                workFlowDao.save(required);
            }

            debtProportionF.setEndValue(String.valueOf(sellLedgerFinance.getDebtProportion()));
            frozenPorportionF.setEndValue(String.valueOf(sellLedgerFinance.getFrozenPorportion()));
            debtAmountF.setEndValue(Strings.truncate(sellLedgerFinance.getDebtAmount().toPlainString()));
            interestReceivableF.setEndValue(Strings.truncate(sellLedgerFinance.getInterestReceivable().toPlainString()));
            interestDeviationF.setEndValue(Strings.truncate(sellLedgerFinance.getInterestDeviation().toPlainString()));

            workFlowDao.save(debtProportionF);
            workFlowDao.save(frozenPorportionF);
            workFlowDao.save(debtAmountF);
            workFlowDao.save(interestReceivableF);
            workFlowDao.save(interestDeviationF);
            String memo = String.valueOf(financeId);

            // 投资金额-买方(借) 投资金额-卖方(贷)
            long flowGroupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(buyLedger, INVESTMENT_AMOUNT, tradeDebtAmount, DEBTASSIGNMENT_DEBT_AMOUNT).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(sellLedger, INVESTMENT_AMOUNT, tradeDebtAmount, DEBTASSIGNMENT_DEBT_AMOUNT).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));

            // 现金-卖方(借) 现金-买方(贷)
            flowGroupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(sellLedger, ACCT_TITLE_CASH, tradeDebtAmount, DEBTASSIGNMENT_DEBT_AMOUNT).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(buyLedger, ACCT_TITLE_CASH, tradeDebtAmount, DEBTASSIGNMENT_DEBT_AMOUNT).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));


            // 现金-卖方(借) 现金-买方(贷)
            flowGroupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(sellLedger, ACCT_TITLE_CASH, tradeInterest, DEBTASSIGNMENT_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(buyLedger, ACCT_TITLE_CASH, tradeInterest, DEBTASSIGNMENT_INTEREST).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));

            if (deliveryDatePV.compareTo(contractDeliveryDatePV) != 0) {
                //逾期利息
                BigDecimal overdueInterest = (deliveryDatePV.subtract(contractDeliveryDatePV)).multiply(debtProportion).setScale(7, BigDecimal.ROUND_DOWN);
                flowGroupNo = sequenceDao.nextFlowGroupNo();

                // 将 逾期付款利息 记入 债务信息表
                Debt debt = new Debt();
                debt.setAccount(buyLedger.getAccount());
                debt.setApposedAcct(sellLedger.getAccount());
                debt.setAmount(overdueInterest);
                debt.setFinance(buyLedgerFinance);
                debt.setApposedFinance(sellLedgerFinance);
                debt.setStatus(UNEXECTUED);
                debt.setExDate(DateUtils.truncate(sellLedgerFinance.getLedgerLoan().getNextExpiry(), Calendar.DATE));
                debt.setTradeNo(String.valueOf(flowGroupNo));
                debtInfoDao.save(debt);
                // 逾期付款利息处理 应收逾期利息-卖方(借) 应付逾期利息-买方(贷)
                flowDao.save(new EntryInfo(sellLedger, OVERDUEINTEREST_RECEIVABLE, overdueInterest, DEBTASSIGNMENT_OVERDUE_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
                flowDao.save(new EntryInfo(buyLedger, OVERDUEINTEREST_PAYABLE, overdueInterest, DEBTASSIGNMENT_OVERDUE_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));

                WorkFlow debtF = new WorkFlow();
                debtF.setBusinessId(businessInfo.getId());
                debtF.setBusinessTypeId(businessInfo.getBusinessTypeId());
                debtF.setObjectId(debt.getId());
                debtF.setMemo("AC_T_DEBT");
                debtF.setFlowNo(sequenceDao.nextWorkFlowNo());
                workFlowDao.save(debtF);
            }

            // 应收利息-买方(借) 应收利息-卖方(贷)
            flowGroupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(buyLedger, OVERDUEINTEREST_INTERESTRECEIVABLE, tradeInterestReceivable, DEBTASSIGNMENT_INTEREST_RECEIVABLE).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(sellLedger, OVERDUEINTEREST_INTERESTRECEIVABLE, tradeInterestReceivable, DEBTASSIGNMENT_INTEREST_RECEIVABLE).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));

            // 误差处理-买方(借) 误差处理-卖方(贷)
            flowGroupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(buyLedger, LEDGER_DETAIL_TYPE_DEVIATION, interestDeviation, DEBTASSIGNMENT_INTEREST_RECEIVABLE).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(sellLedger, LEDGER_DETAIL_TYPE_DEVIATION, interestDeviation, DEBTASSIGNMENT_INTEREST_RECEIVABLE).updateAmt(OPERATOR.SUBTRACT).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));

            // 利息支出-买方(借) 利息收入-卖方(贷)
            flowGroupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(buyLedger, INTEREST_PAY_OUT, tradeInterest, DEBTASSIGNMENT_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(sellLedger, INTEREST_INCOME, tradeInterest, DEBTASSIGNMENT_INTEREST).updateAmt(OPERATOR.ADD).wrap(businessInfo.getId(), memo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));


        }
        // 公司现金账户
        Ledger companyCashLedger = ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
        // 公司账户
        Ledger companyLedger = ledgerDao.loadByAccount(Constants.COMPANY_LEDGER_ACCOUNT);

        // 管理费 处理
        this.chargeFee(companyCashLedger, companyLedger, businessInfo.getId(), managementFee, managementFeeMemo, sellLedger, MANAGEMENT_FEE_EXPENSENSE, MANAGEMENT_FEE_INCOME, DEBTASSIGNMENT_MANAGEMENT_FEE);
        // 固定产品转让服务费处理
        this.chargeFee(companyCashLedger, companyLedger, businessInfo.getId(), fixProduTranSerFee, fixProduTranSerFeeMemo, sellLedger, TRANSFERABLE_SERVICE_CHARGE_FEE_EXPENSENSE, TRANSFERABLE_SERVICE_CHARGE_FEE_INCOME, DEBTASSIGNMENT_FIXPRODUCTTRADESERVICE_FEE);
        // 紧急转让服务费 处理
        this.chargeFee(companyCashLedger, companyLedger, businessInfo.getId(), urgSerFee, urgSerFeeMemo, sellLedger, URGE_TRANSFERABLE_SERVICE_CHARGE_FEE_EXPENSENSE, URGE_TRANSFERABLE_SERVICE_CHARGE_FEE_INCOME, DEBTASSIGNMENT_URGENT_SERVICE_FEE);

        // 冻结类现金-借款人(借) 现金-卖方(贷)
        this.frozen(businessInfo.getId(), sellLedger, frozenAmount, frozenAmountMemo, DEBTASSIGNMENT_FROZEN);
    }

    /**
     * 逾期付款付息 020045
     *
     * @param latePaymentInterestVo
     * @param businessId
     * @return
     */
    public void payInterestLate(LatePaymentInterestVo latePaymentInterestVo, long businessId) {
        String exDate = latePaymentInterestVo.getExDate();
        List<Debt> debts = debtInfoDao.findForPayment(exDate);
        if (debts.size() > 0) {
            for (Debt debt : debts) {
                Ledger buyer = ledgerDao.loadByAccount(debt.getAccount());
                ValidateUtil.validateLedger(buyer, null);

                Ledger seller = ledgerDao.loadByAccount(debt.getApposedAcct());
                ValidateUtil.validateLedger(seller, null);

                BigDecimal debtAmount = debt.getAmount();// 逾期利息
                String memo = "" + debt.getId();
                if (debtAmount.compareTo(buyer.getAmount()) == 1) {
                    Debt newDebt = debt.copy();
                    newDebt.setAmount(debtAmount.subtract(buyer.getAmount()));
                    newDebt.setExDate(DateUtils.addMonths(debt.getExDate(), 1));
                    debtInfoDao.save(newDebt);
                    debtAmount = buyer.getAmount();
                }
                // 修改debt属性
                debt.setStatus(EXECTUED);// 已执行

                // 1、应付逾期利息-买方， 应收逾期利息-卖方
                Long groupNo = sequenceDao.nextFlowGroupNo();
                flowDao.save(new EntryInfo(buyer, OVERDUEINTEREST_PAYABLE, debtAmount, LATE_PAYMENT_INTEREST).updateAmt(SUBTRACT).wrap(businessId, memo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
                flowDao.save(new EntryInfo(seller, OVERDUEINTEREST_RECEIVABLE, debtAmount, LATE_PAYMENT_INTEREST).updateAmt(SUBTRACT).wrap(businessId, memo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
                // 2、现金-卖方， 现金-买方

                groupNo = sequenceDao.nextFlowGroupNo();
                flowDao.save(new EntryInfo(seller, ACCT_TITLE_CASH, debtAmount, LATE_PAYMENT_INTEREST).updateAmt(ADD).wrap(businessId, memo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
                flowDao.save(new EntryInfo(buyer, ACCT_TITLE_CASH, debtAmount, LATE_PAYMENT_INTEREST).updateAmt(SUBTRACT).wrap(businessId, memo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
                // 3、利息收入-买方， 利息收入-卖方
                groupNo = sequenceDao.nextFlowGroupNo();
                flowDao.save(new EntryInfo(buyer, INTEREST_INCOME, debtAmount, LATE_PAYMENT_INTEREST).updateAmt(SUBTRACT).wrap(businessId, memo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
                flowDao.save(new EntryInfo(seller, INTEREST_INCOME, debtAmount, LATE_PAYMENT_INTEREST).updateAmt(ADD).wrap(businessId, memo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
            }

        }
    }

    /**
     * 收取费用
     *
     * @return null
     * @param：对应事务ID、费用数额、费用备注、客户方分账、第二组流水借方分录、第二组流水贷方分录（第一组流水是固定的）
     */
    public void chargeFee(Ledger companyCashLedger, Ledger companyLedger, Long businessId, BigDecimal fee, String feeMemo, Ledger ledger, String debitSubject, String creditSubject, String entryNo) {
        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            // 公司账户
            if (companyLedger == null || companyLedger.getId() == 0) {
                companyLedger = ledgerDao.loadByAccount(Constants.COMPANY_LEDGER_ACCOUNT);
            }
            // 公司现金账户
            if (companyCashLedger == null || companyCashLedger.getId() == 0) {
                companyCashLedger = ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
            }
            // 1、 预收款项-公司现金账户(借) 现金-客户方账户(贷)
            Long flowGroupNo1 = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, fee, entryNo).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, feeMemo, getFlowNo(), flowGroupNo1, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(ledger, ACCT_TITLE_CASH, fee, entryNo).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, feeMemo, getFlowNo(), flowGroupNo1, DORC_TYPE_CREDIT));
            // 2、XX费支出-客户方账户(借) XX费收入-公司账户(贷)
            flowGroupNo1 = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(ledger, debitSubject, fee, entryNo).updateAmt(OPERATOR.ADD).wrap(businessId, feeMemo, getFlowNo(), flowGroupNo1, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(companyLedger, creditSubject, fee, entryNo).updateAmt(OPERATOR.ADD).wrap(businessId, feeMemo, getFlowNo(), flowGroupNo1, DORC_TYPE_CREDIT));
        }
    }

    private String getFlowNo() {
        return sequenceDao.nextFlowNO();
    }

    /**
     * 放款-020042
     *
     * @return null
     * @param：grantLoanVo,messageSequence
     */
    public void makeLoan(GrantLoanVo grantLoanVo, long businessId) {
        String account = grantLoanVo.getAccount();
        BigDecimal amount = grantLoanVo.getAmount();
        String amountMemo = grantLoanVo.getAmountMemo();
        Ledger ledger = ledgerDao.loadByAccount(account);
        ValidateUtil.validateLedger(ledger, null);
        ValidateUtil.validateLedgerAmount(amount, ledger.getAmount());
        Ledger companyCashLedger = ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
        ValidateUtil.validateCompanyCashLedgerAmount(amount, companyCashLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));

        Long groupNo = sequenceDao.nextFlowGroupNo();
        flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, amount, MAKE_LOAN).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, amountMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        flowDao.save(new EntryInfo(ledger, ACCT_TITLE_CASH, amount, MAKE_LOAN).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, amountMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

    }

    /**
     * 分账转账--020043
     *
     * @param transferAccountVo
     * @param businessId
     */
    public void transferAccount(final TransferAccountVo transferAccountVo, long businessId) {
        BigDecimal amount = transferAccountVo.getTransferAmount(); // 转账金额
        String transferAmountMemo = transferAccountVo.getTransferAmountMemo(); // 转账金额备注
        Ledger ledgerIn = ledgerDao.loadByAccount(transferAccountVo.getAccountIn());
        Ledger ledgerOut = ledgerDao.loadByAccount(transferAccountVo.getAccountOut());

        /** 分账户不存在 ||账户状态不正常 */
        ValidateUtil.validateLedger(ledgerIn, null);
        ValidateUtil.validateLedger(ledgerOut, null);
        /** 判断是否属于一个客户的分账号 */
        ValidateUtil.ifNotSameCustomer(ledgerIn, ledgerOut);
        /** 判断转出分账户余额是否大于转账金额 */
        ValidateUtil.validateLedgerAmount(amount, ledgerOut.getAmount());
        /** 判断公司现金账户资金余额是否大于取现金额 */
        Ledger companyCashLedger = ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
        ValidateUtil.validateCompanyCashLedgerAmount(amount, companyCashLedger.getDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));

        long groupNo = sequenceDao.nextFlowGroupNo(); // 流水组号
        /** 更新公司现金账户--转出后 */
        // 写入(预收款项-公司现金账户)流水信息
        flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, amount, TRANSFERACCOUNT_ENCHASHMENT).updateAmt(SUBTRACT).wrap(businessId, transferAmountMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 写入分账转出(现金-客户账户)流水信息
        flowDao.save(new EntryInfo(ledgerOut, ACCT_TITLE_CASH, amount, TRANSFERACCOUNT_ENCHASHMENT).updateAmt(SUBTRACT).wrap(businessId, transferAmountMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

        /** 更新客户转入分账户 */
        groupNo = sequenceDao.nextFlowGroupNo(); // 流水组号
        // 写入分账转入(现金-客户账户)流水信息
        flowDao.save(new EntryInfo(ledgerIn, ACCT_TITLE_CASH, amount, TRANSFERACCOUNT_RECHARGE).updateAmt(ADD).wrap(businessId, transferAmountMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));

        /** 更新公司现金账户--转入后 */
        // 写入转入(预收款项-公司现金账户)流水信息
        flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, amount, TRANSFERACCOUNT_RECHARGE).updateAmt(ADD).wrap(businessId, transferAmountMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

    }

    /**
     * 冻结
     *
     * @param
     */
    protected void frozen(Long businessId, Ledger ledger, BigDecimal frozenAmount, String frozenAmountMemo, String entryNo) {
        if (frozenAmount.compareTo(BigDecimal.ZERO) >= 0) {
            long flowGroupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(ledger, FROZEN_CASH, frozenAmount, entryNo).updateAmt(OPERATOR.ADD).wrap(businessId, frozenAmountMemo, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(ledger, ACCT_TITLE_CASH, frozenAmount, entryNo).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, frozenAmountMemo, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
        }
    }

    /**
     * 解冻
     *
     * @param
     */
    protected void unfreeze(Long businessId, Ledger ledger, BigDecimal unfreezeAmt, String unfreezeAmountMemo, String entryNo) {
        if (unfreezeAmt.compareTo(BigDecimal.ZERO) >= 0) {
            long groupNo = sequenceDao.nextFlowGroupNo();
            flowDao.save(new EntryInfo(ledger, ACCT_TITLE_CASH, unfreezeAmt, entryNo).updateAmt(OPERATOR.ADD).wrap(businessId, unfreezeAmountMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
            flowDao.save(new EntryInfo(ledger, FROZEN_CASH, unfreezeAmt, entryNo).updateAmt(OPERATOR.SUBTRACT).wrap(businessId, unfreezeAmountMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
        }
    }

    /**
     * 收取管理费--020044
     *
     * @param chargeManagementCostVo
     * @param businessId
     */
    public void chargeManagementCost(final ChargeManagementCostVo chargeManagementCostVo, long businessId) {
        BigDecimal managementCostAmount = chargeManagementCostVo.getManagementCostAmount(); // 管理费金额
        String managementCostMemo = chargeManagementCostVo.getManagementCostMemo(); // 管理费备注
        Ledger ledger = ledgerDao.findUniqueBy("account", chargeManagementCostVo.getAccount());
        /** 判断分账号是否有效 */
        ValidateUtil.validateLedger(ledger, null);
        /** 判断分账户余额是否大于管理费金额 */
        ValidateUtil.validateLedgerAmount(managementCostAmount, ledger.getAmount());
        /** 更新公司现金账户 */
        Ledger companyCashLedger = ledgerDao.loadByAccount(COMPANY_CASH_LEDGER_ACCOUNT);
        /** 判断公司现金余额是否大于管理费金额 */
        ValidateUtil.validateCompanyCashLedgerAmount(managementCostAmount, companyCashLedger.getDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));
        long groupNo = sequenceDao.nextFlowGroupNo(); // 流水组号
        // 写入公司现金账户(预收款项-公司现金账户)流水
        flowDao.save(new EntryInfo(companyCashLedger, LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, managementCostAmount, CHARGE_MANAGEMENT_COST).updateAmt(SUBTRACT).wrap(businessId, managementCostMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));
        // 写入客户分账(现金-债权人)流水信息
        flowDao.save(new EntryInfo(ledger, ACCT_TITLE_CASH, managementCostAmount, CHARGE_MANAGEMENT_COST).updateAmt(SUBTRACT).wrap(businessId, managementCostMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
        /** 管理费支出-债权人 */
        /** 更新分账号对应的分账明细表"明细类型"为"管理费支出"3023的条目的“明细值”的字段（即管理费金额增加） */
        // 写入客户分账号对应分账明细的(管理费支出-债权人)流水
        groupNo = sequenceDao.nextFlowGroupNo(); // 管理费流水组号
        flowDao.save(new EntryInfo(ledger, MANAGEMENT_FEE_EXPENSENSE, managementCostAmount, CHARGE_MANAGEMENT_COST).updateAmt(ADD).wrap(businessId, managementCostMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));

        /** 更新公司账户 */
        Ledger companyLedger = ledgerDao.loadByAccount(COMPANY_LEDGER_ACCOUNT);
        // 写入公司账户(管理费收入-公司账户)流水
        flowDao.save(new EntryInfo(companyLedger, MANAGEMENT_FEE_INCOME, managementCostAmount, CHARGE_MANAGEMENT_COST).updateAmt(ADD).wrap(businessId, managementCostMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));
    }

    /**
     * 冻结解冻现金
     *
     * @param frozenOrUnfreezeAmountVo ,messageSequence
     * @return
     */
    public void frozenOrUnfreezeAmount(FrozenOrUnfreezeAmountVo frozenOrUnfreezeAmountVo, long businessId) {
        String account = frozenOrUnfreezeAmountVo.getAccount();
        Boolean flag = frozenOrUnfreezeAmountVo.isFlag();
        BigDecimal amount = frozenOrUnfreezeAmountVo.getAmount();
        String amountMemo = frozenOrUnfreezeAmountVo.getAmountMemo();
        Ledger ledger = ledgerDao.loadByAccount(account);
        ValidateUtil.validateLedger(ledger, null);
        if (flag) { // 冻结
            ValidateUtil.validateFrozenAmount(amount, ledger.getAmount());
            this.frozen(businessId, ledger, amount, amountMemo, FROZEN_AMOUNT);
        } else { // 解冻
            ValidateUtil.validateUnfreezeAmount(amount, ledger.getFrozenAmt());
            this.unfreeze(businessId, ledger, amount, amountMemo, UNFREEZE_AMOUNT);
        }
    }

    /**
     * 外部债权初始化 030007
     *
     * @param externalLoanInitializationVo ,messageSequence
     * @return
     */
    public void externalLoanInitialization(ExternalLoanInitializationVo externalLoanInitializationVo, long businessId) {
        String account = externalLoanInitializationVo.getAccount();
        long loanId = externalLoanInitializationVo.getLoanId();
        BigDecimal amount = externalLoanInitializationVo.getAmount();
        BigDecimal debtProportion = externalLoanInitializationVo.getDebtProportion();
        String tradeMemo = externalLoanInitializationVo.getTradeMemo();
        Ledger externalLedger = ledgerDao.loadByAccount(account);// 理财
        ValidateUtil.validateLedger(externalLedger, null);

        LedgerLoan externalLoan = ledgerLoanDao.getById(loanId);
        ValidateUtil.validateLedgerLoanAll(externalLoan);

        Set<RepaymentPlan> repaymentPlans = externalLoan.getRepaymentPlans();
        long currNum = externalLoan.getCurrNum();// 当前期数
        BigDecimal initAmount = amount; // 导入日PV
        BigDecimal payOutStanding = BigDecimal.ZERO; // 应付剩余本金
        BigDecimal payInterest = BigDecimal.ZERO; // 应付利息
        BigDecimal lastExpiryPV; // 上一回款日PV
        BigDecimal dateInterval1; // 总的回款周期长度
        BigDecimal dateInterval2; // 当前时间长度
        Date initDate = SystemUtil.currentDate();// 初始化日期
        BigDecimal receiveOutStanding; // 应收剩余本金
        BigDecimal receiveInterest; // 应收利息
        BigDecimal totalDeviation; // 总误差

        if (currNum == 0) {
            // 首期
            payOutStanding = externalLoan.getLoan();
            dateInterval1 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(externalLoan.getInterestStart(), externalLoan.getNextExpiry()));
            dateInterval2 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(externalLoan.getInterestStart(), initDate));
            lastExpiryPV = externalLoan.getLoan();
            RepaymentPlan repaymentPlan = externalLoan.getFirstRepaymentPlan();
            payInterest = Arith.mul(Arith.div(repaymentPlan.getInterestAmt(), dateInterval1), dateInterval2);
        }
        // 非首期
        else {
            dateInterval1 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(externalLoan.getLastExpiry(), externalLoan.getNextExpiry()));
            dateInterval2 = new BigDecimal(com.zendaimoney.coreaccount.util.DateUtils.getDayCount(externalLoan.getLastExpiry(), initDate));
            lastExpiryPV = pvService.getPvAlways(externalLoan, Boolean.TRUE);
            for (RepaymentPlan repaymentPlan : repaymentPlans) {
                if (repaymentPlan.getCurrNum() == currNum - 1L) {
                    payOutStanding = repaymentPlan.getOutstanding();
                } else if (repaymentPlan.getCurrNum() == currNum) {
                    payInterest = Arith.mul(Arith.div(repaymentPlan.getInterestAmt(), dateInterval1), dateInterval2);
                }
            }
        }
        // 记应收剩余本+应收利息
        // 应收剩余本金 = 上一回款日pv 应收利息= 初始金额-应收剩余本金 总误差= 应付利息+应付剩余本金-应收利息-应收剩余本金
        receiveOutStanding = lastExpiryPV;
        receiveInterest = Arith.sub(initAmount, receiveOutStanding);
        totalDeviation = Arith.sub(Arith.add(payOutStanding, payInterest), initAmount);

		/*
         * 更新贷款分户信息表 AC_T_LEDGER_LOAN.AMOUNT_SPARE=PV
		 * 借款人应付利息AC_T_LEDGER_LOAN.INTEREST_PAYABLE=应付利息
		 * AC_T_LEDGER_LOAN.OUTSTANDING = 应收剩余本金
		 */
        externalLoan.setAmountSpare(initAmount);
        externalLoan.setInterestPayable(payInterest);
        externalLoan.setOutstanding(receiveOutStanding);

		/*
         * 添加一条AC_T_LEDGER_FINANCE记录: 状态，债务本金=应收剩余本金，持有比例，起息日期， 当前应收利息=应收利息，利息误差
		 * =总误差, 债权编号，分帐id，备注
		 */

        LedgerFinance ledgerFinance = new LedgerFinance();
        ledgerFinance.setLedger(externalLedger);
        ledgerFinance.setLedgerLoan(externalLoan);
        ledgerFinance.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        ledgerFinance.setDebtAmount(receiveOutStanding);
        ledgerFinance.setDebtProportion(debtProportion);
        ledgerFinance.setIntersetStart(externalLoan.getInterestStart());
        ledgerFinance.setInterestReceivable(receiveInterest);
        ledgerFinance.setInterestDeviation(totalDeviation);
        ledgerFinance.setFrozenPorportion(BigDecimal.ZERO);
        ledgerFinance.setMemo(tradeMemo);
//        externalLedger.getLedgerFinances().add(ledgerFinance);
        ledgerFinanceDao.save(ledgerFinance);
        // 分账信息处理
        long groupNo = sequenceDao.nextFlowGroupNo();

        // 1、债权人分账明细明细类别为"投资金额"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE =应收剩余本金
        flowDao.save(new EntryInfo(externalLedger, INVESTMENT_AMOUNT, receiveOutStanding, LOAN_INITIALIZATION).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));

        // 2 借款人分账明细明细类别为"借款金额"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE = 应付剩余本金
        flowDao.save(new EntryInfo(externalLoan.getLedger(), LEDGER_DETAIL_TYPE_DEBT_BALANCE, payOutStanding, LOAN_INITIALIZATION).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

        // 3、债权人分账明细明细类别为"应收利息"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE =应收利息
        flowDao.save(new EntryInfo(externalLedger, OVERDUEINTEREST_INTERESTRECEIVABLE, receiveInterest, LOAN_INITIALIZATION).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));

        // 4 借款人分账明细明细类别为"应付利息"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE = 应付利息
        flowDao.save(new EntryInfo(externalLoan.getLedger(), LEDGER_DETAIL_TYPE_PAYABLE, payInterest, LOAN_INITIALIZATION).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_CREDIT));

        // 5 、债权人分账明细明细类别为"利息误差"的明细值 AC_T_LEDGER_DETAIL.DETAIL_VALUE =总误差
        flowDao.save(new EntryInfo(externalLedger, LEDGER_DETAIL_TYPE_DEVIATION, totalDeviation, LOAN_INITIALIZATION).updateAmt(ADD, 7, ROUND_HALF_UP).wrap(businessId, tradeMemo, getFlowNo(), groupNo, DORC_TYPE_DEBIT));

    }

    /**
     * 分账停用010005
     *
     * @param datagram
     * @return
     */
    public void disableLedger(Datagram datagram) {
        LedgerDisableVo ledgerDisableVo = (LedgerDisableVo) datagram.getDatagramBody();
        Ledger ledger = ledgerDao.findUniqueBy("account", ledgerDisableVo.getAccount());
        ValidateUtil.validateLedger(ledger);

        /** 更新分账信息表 */
        ledger.setAcctStatus(Constants.ACCOUNT_STATUS_DISABLE);
        /** 操作时间 */
        ledgerDisableVo.setOperateTime(DateFormatUtils.format(Calendar.getInstance(), Constants.DATE_FORMAT));
        /** 操作码 */
        ledgerDisableVo.setOperateCode(Constants.PROCESS_STATUS_OK);
        logger.info("分账信息停用成功！");

    }

}
