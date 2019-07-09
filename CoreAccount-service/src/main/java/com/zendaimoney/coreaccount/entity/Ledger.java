package com.zendaimoney.coreaccount.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.zendaimoney.coreaccount.constants.Constants.*;
import static com.zendaimoney.coreaccount.constants.Subject.ACCT_TITLE_CASH;
import static com.zendaimoney.coreaccount.constants.Subject.FROZEN_CASH;

/**
 * 新建理财分账010002; 新建贷款分账010003
 *
 * @author Jianlong Ma
 */
@Entity
@Table(name = "AC_T_LEDGER")
public class Ledger {

    private long id;
    /**
     * 分账号
     */
    private String account;
    /**
     * 账户状态
     */
    private String acctStatus;
    /**
     * 开户日期
     */
    private Date openacctDate = new Date();
    /**
     * 开户柜员
     */
    private String operator;
    /**
     * 业务类别
     */
    private String busiType;
    /**
     * 现金
     */
    private BigDecimal amount;
    /**
     * 冻结金额
     */
    private BigDecimal frozenAmt;
    /**
     * 销户日期
     */
    private Date cancelacctDate;
    /**
     * 备注
     */
    private String memo;
    /**
     * 对应卡号
     */
    private String cardNumber;

    private Set<LedgerLoan> ledgerLoans = new HashSet<LedgerLoan>(0);
    private Set<LedgerDetail> ledgerDetails = new HashSet<LedgerDetail>(0);
    private Set<LedgerFinance> ledgerFinances = new HashSet<LedgerFinance>(0);
    /**
     * 用于取得'核心客户信息表'中的总账号
     */
    private Customer customer;

    /**
     * 重要方法：根据分账明细类型取分账明细值
     */
    @Transient
    public BigDecimal getDetailValue(String detailType) {
        if (ACCT_TITLE_CASH.equals(detailType)) {
            return amount;
        } else if (FROZEN_CASH.equals(detailType)) {
            return frozenAmt;
        } else {
            if (CollectionUtils.isNotEmpty(this.getLedgerDetails())) {
                for (LedgerDetail l : this.getLedgerDetails()) {
                    if (l.getType().equals(detailType) && StringUtils.isNotBlank(l.getDetailValue())) {
                        return new BigDecimal(l.getDetailValue());
                    }
                }
            }
            return BigDecimal.ZERO;
        }
    }

    /**
     * 重要方法：根据分账明细类型更新分账明细值
     */
    @Transient
    public void setDetailValue(String detailType, BigDecimal detailValue) {
        if (CollectionUtils.isNotEmpty(this.getLedgerDetails())) {
            for (LedgerDetail l : this.getLedgerDetails()) {
                if (l.getType().equals(detailType)) {
                    l.setDetailValue(detailValue);
                    return;
                }
            }
        }
        LedgerDetail ld = new LedgerDetail();
        ld.setLedger(this);
        this.getLedgerDetails().add(ld);
        ld.setType(detailType);
        ld.setDetailValue(detailValue);
    }

    public void resetValue(String detailType, BigDecimal detailValue) {
        if (ACCT_TITLE_CASH.equals(detailType)) {
            this.setAmount(detailValue);
            return;
        } else if (FROZEN_CASH.equals(detailType)) {
            this.setFrozenAmt(detailValue);
            return;
        }
        setDetailValue(detailType, detailValue);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_LEDGER")
    @Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "ACCOUNT", nullable = false, length = 30)
    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Column(name = "ACCT_STATUS", nullable = false, length = 2)
    public String getAcctStatus() {
        return acctStatus;
    }

    public void setAcctStatus(String acctStatus) {
        this.acctStatus = acctStatus;
    }

    @Column(name = "OPENACCT_DATE")
    public Date getOpenacctDate() {
        return openacctDate;
    }

    public void setOpenacctDate(Date openacctDate) {
        this.openacctDate = openacctDate;
    }

    @Column(name = "OPERATOR", length = 20)
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Column(name = "BUSI_TYPE", nullable = false, length = 1)
    public String getBusiType() {
        return this.busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    @Column(name = "AMOUNT", precision = 22, scale = 7)
    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        if (null == amount) {
            this.amount = BigDecimal.ZERO;
            return;
        }
        this.amount = amount;
    }

    @Column(name = "FROZEN_AMT", precision = 22, scale = 7)
    public BigDecimal getFrozenAmt() {
        return this.frozenAmt;
    }

    public void setFrozenAmt(BigDecimal frozenAmt) {
        if (null == frozenAmt) {
            this.frozenAmt = BigDecimal.ZERO;
            return;
        }
        this.frozenAmt = frozenAmt;
    }

    @Column(name = "CANCELACCT_DATE", length = 7)
    public Date getCancelacctDate() {
        return this.cancelacctDate;
    }

    public void setCancelacctDate(Date cancelacctDate) {
        this.cancelacctDate = cancelacctDate;
    }

    @Column(name = "MEMO", length = 150)
    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ledger")
    public Set<LedgerLoan> getLedgerLoans() {
        return this.ledgerLoans;
    }

    public void setLedgerLoans(Set<LedgerLoan> ledgerLoans) {
        this.ledgerLoans = ledgerLoans;
    }

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "ledger")
    public Set<LedgerDetail> getLedgerDetails() {
        return this.ledgerDetails;
    }

    public void setLedgerDetails(Set<LedgerDetail> ledgerDetails) {
        this.ledgerDetails = ledgerDetails;
    }

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "ledger")
    public Set<LedgerFinance> getLedgerFinances() {
        return this.ledgerFinances;
    }

    public Set<LedgerFinance> findValidFinance() {
        Set<LedgerFinance> result = new HashSet<LedgerFinance>();
        String status;
        for (LedgerFinance ledgerFinance : this.ledgerFinances) {
            if (ACCOUNT_STATUS_REGULAR.equals(status = ledgerFinance.getAcctStatus()) || status.equals(ACCOUNT_STATUS_IDLE) || status.equals(ACCOUNT_STATUS_OVERDUE))
                result.add(ledgerFinance);
        }
        return result;
    }

    public void setLedgerFinances(Set<LedgerFinance> ledgerFinances) {
        this.ledgerFinances = ledgerFinances;
    }

    @Column(name = "CARD_NUMBER", length = 30)
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @ManyToOne
    @JoinColumn(name = "TOTAL_ACCOUNT_ID")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
