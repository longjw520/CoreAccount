package com.zendaimoney.coreaccount.service;

import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_OVERDUE;
import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_REGULAR;
import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_IDLE;
import static com.zendaimoney.coreaccount.constants.Constants.LEDGERFINANCE_STATUS_FRO;
import static com.zendaimoney.coreaccount.constants.Constants.LEDGER_FINANCE;
import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_OK;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.dao.LedgerFinanceDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.WorkFlow;
import com.zendaimoney.coreaccount.rmi.vo.AccountStaUpdateVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.FroOrUnfreProportionVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerFinanceVo;
import com.zendaimoney.coreaccount.util.NumberUtil;
import com.zendaimoney.coreaccount.util.Strings;

/**
 * 理财分户相关操作
 *
 * @author Jianlong Ma
 */
@Named
@Transactional
public class LedgerFinanceService {
    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private LedgerFinanceDao ledgerFinanceDao;
    @Inject
    private WorkFlowDao workFlowDao;
    @Inject
    private SequenceDao sequenceDao;

    public void updateStatus(AccountStaUpdateVo accountStaUpdateVo, BusinessInfo businessInfo) {
        LedgerFinance ledgerFinance = ledgerFinanceDao.getById(accountStaUpdateVo.getId());
        ValidateUtil.validateLedgerFinanceNull(ledgerFinance, accountStaUpdateVo.getId());
        String startValue = ledgerFinance.getAcctStatus();
        ledgerFinance.setAcctStatus(accountStaUpdateVo.getAcctStatus());
        ledgerFinance.setLastModified(new Date());
        accountStaUpdateVo.setOperateCode(PROCESS_STATUS_OK);
        workFlowDao.workFlowSave(businessInfo.getId(), null, ledgerFinance.getId(), startValue, null, ledgerFinance.getAcctStatus(), sequenceDao.nextWorkFlowNo(), LEDGER_FINANCE, businessInfo.getBusinessTypeId());
        logger.info(ledgerFinance.getId()+"理财账户更新成功！");
    }

    /**
     * 查询投资明细
     *
     * @author Jianlong Ma
     */
    public Page<LedgerFinance> queryBy(QueryLedgerFinanceVo queryLedgerFinanceVo) {
//        if(queryLedgerFinanceVo.getPageSize()>30){
//            queryLedgerFinanceVo.setPageSize(30);
//        }
        Page<LedgerFinance> page = ledgerFinanceDao.queryBy(queryLedgerFinanceVo);
        for (LedgerFinance finance : page.getResult()) {
            finance.getLedgerLoan().setCustomer(finance.getLedgerLoan().getLedger().getCustomer());
            finance.setCustomer(finance.getLedger().getCustomer());
        }
        logger.debug("查询投资明细");
        return page;
    }
    
    /**
     * 查询投资明细
     */
    public List<Object> queryForLoanJudgeTimer() {
        return ledgerFinanceDao.queryForLoanJudger();
    }

    //TODO
    /**
     * 根据loanId查询买方对债权持有信息
     * @param loanId
     * @param ledgerId
     * @return
     */
    public List<LedgerFinance> queryLedgerFinances(long loanId, long ledgerId){
    	String status[] = new String[]{ACCOUNT_STATUS_REGULAR, ACCOUNT_STATUS_OVERDUE, ACCOUNT_STATUS_IDLE};
        List<LedgerFinance> buyFinances = ledgerFinanceDao.queryBuyFinances(ledgerId, loanId, status);
    	return buyFinances;
    }
    
    /**
     * 冻结解冻持有比例
     *
     * @author huangna
     */
    public void froOrUnfreProportion(Datagram datagram, BusinessInfo businessInfo) {
        FroOrUnfreProportionVo froOrUnfreProportionVo = (FroOrUnfreProportionVo) datagram.getDatagramBody();
        Map<Long, BigDecimal> values = froOrUnfreProportionVo.getProportion();// financeId-->proportion
        ArrayList<Long> financeIds = new ArrayList<Long>(values.keySet());
        List<LedgerFinance> ledgerFinances = new ArrayList<LedgerFinance>();
        if(financeIds.size()<1000){
        	ledgerFinances = ledgerFinanceDao.get(values.keySet());
        }else{
    		int size = financeIds.size();
    		int max = 1000;//单次查询最大笔数
			int page = size / max + ((size % max == 0) ? 0 : 1);
//    		int page = (size + len - 1) / len;//设置分页
    		for (int i = 0; i < page; i++) {
    			List<Long> subList = financeIds.subList(i * max, ((i + 1) * max > size ? size : max * (i + 1)));
    			ledgerFinances.addAll(ledgerFinanceDao.get(subList));
    		}
        }
        
        ValidateUtil.assertTrue(ledgerFinances.size() == values.size(), "ledger.finance.not.in");
        String status = froOrUnfreProportionVo.getStatus().intern();
        WorkFlow flow = new WorkFlow();
        flow.setBusinessId(businessInfo.getId());
        flow.setBusinessTypeId(businessInfo.getBusinessTypeId());
        flow.setMemo(status);
        for (LedgerFinance ledgerFinance : ledgerFinances) {
            long id = ledgerFinance.getId();
            ValidateUtil.validateLedgerFinance(ledgerFinance, id);
            BigDecimal proportion = values.get(id);
            flow.setObjectId(id);
            if (status == LEDGERFINANCE_STATUS_FRO) {// 1冻结
                ValidateUtil.validateFrozenProportions(proportion, ledgerFinance.getDebtProportion(), id);
                BigDecimal frozenProportion = NumberUtil.getBigNum(ledgerFinance.getFrozenPorportion());
                WorkFlow workFlow = flow.copy();
                workFlow.setStartValue(String.valueOf(ledgerFinance.getDebtProportion()));
                ledgerFinance.setDebtProportion(ledgerFinance.getDebtProportion().subtract(proportion));
                ledgerFinance.setFrozenPorportion(frozenProportion.add(proportion));
                ledgerFinance.setLastModified(new Date());
                workFlow.setEndValue(String.valueOf(ledgerFinance.getDebtProportion()));
                workFlow.setFlowNo(sequenceDao.nextWorkFlowNo());
                workFlow.setMemo("DEBT_PROPORTION");
                workFlow.setTradeValue(String.valueOf(proportion.negate()));
                workFlowDao.save(workFlow);
                WorkFlow workFlow2 = flow.copy();
                workFlow2.setStartValue(String.valueOf(frozenProportion));
                workFlow2.setMemo("FROZEN_PORPORTION");
                workFlow2.setEndValue(String.valueOf(ledgerFinance.getFrozenPorportion()));
                workFlow2.setFlowNo(sequenceDao.nextWorkFlowNo());
                workFlow2.setTradeValue(String.valueOf(proportion));
                workFlowDao.save(workFlow2);
                continue;
            }
            ValidateUtil.validateUnfreezeProportions(proportion, ledgerFinance.getFrozenPorportion(), id);
            WorkFlow workFlow = flow.copy();
            workFlow.setMemo("FROZEN_PORPORTION");
            BigDecimal unFreezeProportion = NumberUtil.getBigNum(ledgerFinance.getDebtProportion());
            workFlow.setStartValue(String.valueOf(ledgerFinance.getFrozenPorportion()));
            ledgerFinance.setFrozenPorportion(ledgerFinance.getFrozenPorportion().subtract(proportion));
            ledgerFinance.setDebtProportion(unFreezeProportion.add(proportion));
            ledgerFinance.setLastModified(new Date());
            workFlow.setEndValue(String.valueOf(ledgerFinance.getFrozenPorportion()));
            workFlow.setFlowNo(sequenceDao.nextWorkFlowNo());
            workFlow.setTradeValue(String.valueOf(proportion.negate()));
            workFlowDao.save(workFlow);
            WorkFlow workFlow2 = flow.copy();
            workFlow2.setMemo("DEBT_PROPORTION");
            workFlow2.setStartValue(String.valueOf(unFreezeProportion));
            workFlow2.setEndValue(String.valueOf(ledgerFinance.getDebtProportion()));
            workFlow2.setFlowNo(sequenceDao.nextWorkFlowNo());
            workFlow2.setTradeValue(String.valueOf(proportion));

            workFlowDao.save(workFlow2);
        }
        froOrUnfreProportionVo.setOperateCode(PROCESS_STATUS_OK);
    }

    /**
     * 交易投资明细
     *
     * @param sellLedgerFinance 卖方投资明细、 buyLedger 卖方分账 , BigDecimal 本金、比例、利息、误差
     *                          兼容jieyu(默认不支持合并)
     * @author ShiMing
     */
    public LedgerFinance tradeFinance(LedgerFinance sellLedgerFinance, Ledger buyLedger, BigDecimal tradeDebtAmount, BigDecimal tradeInterestReceivable, BigDecimal interestDeviation, BigDecimal debtProportion, BigDecimal unfreezeProportion, List<LedgerFinance> buyFinances,
                                      BusinessInfo businessInfo) {
        return tradeFinance(sellLedgerFinance, buyLedger, tradeDebtAmount, tradeInterestReceivable, interestDeviation, debtProportion, unfreezeProportion, buyFinances, businessInfo, buyFinances.size()==0?false:true);
//        return tradeFinance(sellLedgerFinance, buyLedger, tradeDebtAmount, tradeInterestReceivable, interestDeviation, debtProportion, unfreezeProportion, buyFinances, businessInfo, false);
    }

    /**
     * 交易投资明细
     *
     * @param sellLedgerFinance 卖方投资明细、 buyLedger 卖方分账 , BigDecimal 本金、比例、利息、误差
     * @author ShiMing
     */
    public LedgerFinance tradeFinance(LedgerFinance sellLedgerFinance, Ledger buyLedger, BigDecimal tradeDebtAmount, BigDecimal tradeInterestReceivable, BigDecimal interestDeviation, BigDecimal debtProportion, BigDecimal unfreezeProportion, List<LedgerFinance> buyFinances,
                                      BusinessInfo businessInfo, boolean allowMerge) {

        // 卖方finance卖出
        sellLedgerFinance.setDebtProportion(sellLedgerFinance.getDebtProportion().add(unfreezeProportion).subtract(debtProportion));
        sellLedgerFinance.setFrozenPorportion(sellLedgerFinance.getFrozenPorportion().subtract(unfreezeProportion));
        sellLedgerFinance.setDebtAmount(sellLedgerFinance.getDebtAmount().subtract(tradeDebtAmount));
        sellLedgerFinance.setInterestReceivable(sellLedgerFinance.getInterestReceivable().subtract(tradeInterestReceivable));
        sellLedgerFinance.setInterestDeviation(sellLedgerFinance.getInterestDeviation().subtract(interestDeviation));
        sellLedgerFinance.setLastModified(new Date());
        
        if (allowMerge) {
            //买方已经有待转入的债权
        	LedgerFinance finance = buyFinances.get(0);//合并至最大持有比例的finance中
            if (finance.getLedgerLoan().getId() == sellLedgerFinance.getLedgerLoan().getId()) {

                WorkFlow debtAmountF = new WorkFlow();
                debtAmountF.setBusinessId(businessInfo.getId());
                debtAmountF.setBusinessTypeId(businessInfo.getBusinessTypeId());
                debtAmountF.setStartValue(Strings.truncate(finance.getDebtAmount().toPlainString()));
                debtAmountF.setTradeValue(Strings.truncate(tradeDebtAmount.toPlainString()));
                finance.setDebtAmount(finance.getDebtAmount().add(tradeDebtAmount));
                debtAmountF.setEndValue(Strings.truncate(finance.getDebtAmount().toPlainString()));
                debtAmountF.setFlowNo(sequenceDao.nextWorkFlowNo());
                debtAmountF.setMemo("DEBT_AMOUNT");
                debtAmountF.setObjectId(finance.getId());
                workFlowDao.save(debtAmountF);

                WorkFlow debtProportionF = new WorkFlow();
                debtProportionF.setBusinessId(businessInfo.getId());
                debtProportionF.setBusinessTypeId(businessInfo.getBusinessTypeId());
                debtProportionF.setStartValue(String.valueOf(finance.getDebtProportion()));
                debtProportionF.setTradeValue(String.valueOf(debtProportion));
                finance.setDebtProportion(finance.getDebtProportion().add(debtProportion));
                debtProportionF.setEndValue(String.valueOf(finance.getDebtProportion()));
                debtProportionF.setFlowNo(sequenceDao.nextWorkFlowNo());
                debtProportionF.setMemo("DEBT_PROPORTION");
                debtProportionF.setObjectId(finance.getId());
                workFlowDao.save(debtProportionF);

                WorkFlow interestReceivableF = new WorkFlow();
                interestReceivableF.setBusinessId(businessInfo.getId());
                interestReceivableF.setBusinessTypeId(businessInfo.getBusinessTypeId());
                interestReceivableF.setStartValue(Strings.truncate(finance.getInterestReceivable().toPlainString()));
                interestReceivableF.setTradeValue(Strings.truncate(tradeInterestReceivable.toPlainString()));
                finance.setInterestReceivable(finance.getInterestReceivable().add(tradeInterestReceivable));
                interestReceivableF.setEndValue(Strings.truncate(finance.getInterestReceivable().toPlainString()));
                interestReceivableF.setMemo("INTEREST_RECEIVABLE");
                interestReceivableF.setFlowNo(sequenceDao.nextWorkFlowNo());
                interestReceivableF.setObjectId(finance.getId());
                workFlowDao.save(interestReceivableF);

                WorkFlow interestDeviationF = new WorkFlow();
                interestDeviationF.setBusinessId(businessInfo.getId());
                interestDeviationF.setBusinessTypeId(businessInfo.getBusinessTypeId());
                interestDeviationF.setStartValue(Strings.truncate(finance.getInterestDeviation().toPlainString()));
                interestDeviationF.setTradeValue(Strings.truncate(interestDeviation.toPlainString()));
                finance.setInterestDeviation(finance.getInterestDeviation().add(interestDeviation));
                interestDeviationF.setEndValue(Strings.truncate(finance.getInterestDeviation().toPlainString()));
                interestDeviationF.setFlowNo(sequenceDao.nextWorkFlowNo());
                interestDeviationF.setMemo("INTEREST_DEVIATION");
                interestDeviationF.setObjectId(finance.getId());
                workFlowDao.save(interestDeviationF);
                finance.setLastModified(new Date());
                return finance;
            }
        }

        // 买方finance买入,原先没有这些债权,所以新建一笔投资
        LedgerFinance buyLedgerFinance = new LedgerFinance();
        buyLedgerFinance.setAcctStatus(sellLedgerFinance.getAcctStatus());
        buyLedgerFinance.setDebtAmount(tradeDebtAmount);
        buyLedgerFinance.setDebtProportion(debtProportion);
        buyLedgerFinance.setIntersetStart(sellLedgerFinance.getIntersetStart());
        buyLedgerFinance.setLedgerLoan(sellLedgerFinance.getLedgerLoan());
        buyLedgerFinance.setInterestReceivable(tradeInterestReceivable);
        buyLedgerFinance.setLedger(buyLedger);
        buyLedgerFinance.setInterestDeviation(interestDeviation);
        buyLedgerFinance.setFrozenPorportion(BigDecimal.ZERO);
//        buyLedger.getLedgerFinances().add(buyLedgerFinance);
        return buyLedgerFinance;

    }
}
