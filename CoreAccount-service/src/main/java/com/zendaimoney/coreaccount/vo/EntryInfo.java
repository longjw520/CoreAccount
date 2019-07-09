package com.zendaimoney.coreaccount.vo;

import com.zendaimoney.coreaccount.constants.Subject;
import com.zendaimoney.coreaccount.entity.Flow;
import com.zendaimoney.coreaccount.entity.FlowInterest;
import com.zendaimoney.coreaccount.entity.Ledger;

import java.math.BigDecimal;

/**
 * 分录的信息
 *
 * @author ShiMing
 */
final public class EntryInfo {
    private BigDecimal startBalance;
    private BigDecimal endBalance;
    private BigDecimal tradeAmount;
    private Ledger ledger;
    private String detailType;
    private String entryNo;
    private String account;

    private EntryInfo(Ledger ledger, BigDecimal tradeAmount, String entryNo) {
        this.tradeAmount = tradeAmount;
        this.entryNo = entryNo;
        this.ledger = ledger;
    }

    /**
     * @param account
     * @param detailType
     * @param tradeAmount
     * @param entryNo
     * @param startBalance
     * @author kimi
     * @since 2014-11-10
     */
    public EntryInfo(String account, String detailType, BigDecimal tradeAmount, String entryNo, BigDecimal startBalance) {
        this.account = account;
        this.detailType = detailType;
        this.tradeAmount = tradeAmount;
        this.entryNo = entryNo;
        this.startBalance = startBalance;
    }


    public EntryInfo calculateEndBalance(OPERATOR opt) {
        switch (opt) {
            case ADD:
                this.endBalance = this.startBalance.add(this.tradeAmount);
                break;
            case SUBTRACT:
                this.endBalance = this.startBalance.subtract(this.tradeAmount);
                break;
        }
        return this;
    }

    public EntryInfo(Ledger ledger, String detailType, BigDecimal tradeAmount,
                     String entryNo) {
        this(ledger, tradeAmount, entryNo);
        if (detailType == null) {
            throw new AssertionError("must type correct detailType!");
        } else {
            this.startBalance = ledger.getDetailValue(detailType);
        }
        this.detailType = detailType;

    }

    public EntryInfo updateAmt(OPERATOR opt) {
        switch (opt) {
            case ADD:
                this.endBalance = this.startBalance.add(this.tradeAmount);
                break;
            case SUBTRACT:
                this.endBalance = this.startBalance.subtract(this.tradeAmount);
        }
        if (this.detailType.equals(Subject.ACCT_TITLE_CASH))
            this.ledger.setAmount(this.endBalance);
        else if (this.detailType.equals(Subject.FROZEN_CASH))
            this.ledger.setFrozenAmt(this.endBalance);
        else
            this.ledger.setDetailValue(this.detailType, this.endBalance);
        return this;
    }

    public EntryInfo updateAmt(OPERATOR opt, int scale, int roundingMode) {
        switch (opt) {
            case ADD:
                this.endBalance = this.startBalance.add(this.tradeAmount);
                break;
            case SUBTRACT:
                this.endBalance = this.startBalance.subtract(this.tradeAmount);
        }
        if (this.detailType.equals(Subject.ACCT_TITLE_CASH))
            this.ledger.setAmount(this.endBalance);
        else if (this.detailType.equals(Subject.FROZEN_CASH))
            this.ledger.setFrozenAmt(this.endBalance);
        else
            this.ledger.setDetailValue(this.detailType,
                    this.endBalance.setScale(scale, roundingMode));
        return this;
    }

    /**
     * 将参数包装为flow对象
     *
     * @param businessId
     * @param memo
     * @param flowNo
     * @param groupNo
     * @param dorc
     * @return flow
     */
    public Flow wrap(Long businessId, String memo, String flowNo, Long groupNo,
                     String dorc) {
        Flow flow = new Flow();
        flow.setBusinessId(businessId);
        flow.setTradeAmount(this.tradeAmount);
        flow.setAccount(this.ledger == null ? this.account : ledger.getAccount());
        flow.setAcctTitle(this.detailType);
        flow.setMemo(memo);
        flow.setStartBalance(this.startBalance);
        flow.setEndBalance(this.endBalance);
        flow.setFlowNo(flowNo);
        flow.setGroupNo(groupNo);
        flow.setDorc(dorc);
        flow.setEntryNo(this.entryNo);
        return flow;
    }

    /**
     * 将参数包装为flow对象
     *
     * @param businessId
     * @param memo
     * @param dorc
     * @return flow
     */
    public Flow wrap(Long businessId, String memo, String dorc) {
        Flow flow = new Flow();
        flow.setBusinessId(businessId);
        flow.setTradeAmount(this.tradeAmount);
        flow.setAccount(this.ledger.getAccount());
        flow.setAcctTitle(this.detailType);
        flow.setMemo(memo);
        flow.setStartBalance(this.startBalance);
        flow.setEndBalance(this.endBalance);
        flow.setDorc(dorc);
        flow.setEntryNo(this.entryNo);
        return flow;
    }

    /**
     * 包装EntryInfo成为FlowInterest
     * @author kimi
     * @since 2014-11-19
     * @param businessId
     * @param memo
     * @param flowNo
     * @param groupNo
     * @param dorc
     * @return FlowInterest
     */
    public FlowInterest init(Long businessId, String memo, String flowNo, Long groupNo,
                             String dorc) {
        FlowInterest flowInterest=new FlowInterest();
        flowInterest.setBusinessId(businessId);
        flowInterest.setTradeAmount(this.tradeAmount);
        flowInterest.setAccount(this.ledger == null ? this.account : ledger.getAccount());
        flowInterest.setAcctTitle(this.detailType);
        flowInterest.setMemo(memo);
        flowInterest.setStartBalance(this.startBalance);
        flowInterest.setEndBalance(this.endBalance);
        flowInterest.setFlowNo(flowNo);
        flowInterest.setGroupNo(groupNo);
        flowInterest.setDorc(dorc);
        flowInterest.setEntryNo(this.entryNo);
        return flowInterest;
    }

    /**
     * 标识操作类型(+/-)
     *
     * @author binliu
     */
    public enum OPERATOR {
        ADD, SUBTRACT
    }

}
