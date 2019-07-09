package com.zendaimoney.coreaccount.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "AC_T_FLOW")
public class Flow implements Serializable {

	private long id;
	private Long businessId;
	private BigDecimal tradeAmount;
	private String account;
	private String acctTitle;
	private String memo;
	/** 冲正号 */
	private Long reversedNo;
	/** 期初余额 */
	private BigDecimal startBalance;
	/** 期末余额 */
	private BigDecimal endBalance;
	/** 流水号 */
	private String flowNo;
	/** 流水组号 */
	private Long groupNo;

	/** 借贷别 */
	private String dorc;

	/** 分录号 */
	private String entryNo;
    /**记录最后修改时间*/
    private Date lastModified=new Date();

	public Flow() {
	}

	public Flow(Long businessId, BigDecimal tradeAmount, String account, String acctTitle, String memo, Long reversedNo, BigDecimal startBalance, BigDecimal endBalance, String flowNo, Long groupNo, String dorc, String entryNo) {
		super();
		this.businessId = businessId;
		this.tradeAmount = tradeAmount == null ? BigDecimal.ZERO : tradeAmount;
		this.account = account;
		this.acctTitle = acctTitle;
		this.memo = memo;
		this.reversedNo = reversedNo;
		this.startBalance = startBalance == null ? BigDecimal.ZERO : startBalance;
		this.endBalance = endBalance == null ? BigDecimal.ZERO : endBalance;
		this.flowNo = flowNo;
		this.groupNo = groupNo;
		this.dorc = dorc;
		this.entryNo = entryNo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_FLOW")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "BUSINESS_ID", precision = 18, scale = 0)
	public Long getBusinessId() {
		return this.businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	@Column(name = "TRADE_AMOUNT", precision = 33, scale = 18)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		if (null == tradeAmount) {
			this.tradeAmount = BigDecimal.ZERO;
			return;
		}
		this.tradeAmount = tradeAmount;
	}

	@Column(name = "ACCOUNT", length = 30)
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "ACCT_TITLE", length = 6)
	public String getAcctTitle() {
		return this.acctTitle;
	}

	public void setAcctTitle(String acctTitle) {
		this.acctTitle = acctTitle;
	}

	@Column(name = "MEMO", length = 150)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "REVERSED_NO", precision = 18, scale = 0)
	public Long getReversedNo() {
		return reversedNo;
	}

	public void setReversedNo(Long reversedNo) {
		this.reversedNo = reversedNo;
	}

	@Column(name = "START_BALANCE", precision = 33, scale = 18)
	public BigDecimal getStartBalance() {
		return startBalance;
	}

	public void setStartBalance(BigDecimal startBalance) {
		if (null == startBalance) {
			this.startBalance = BigDecimal.ZERO;
			return;
		}
		this.startBalance = startBalance;
	}

	@Column(name = "END_BALANCE", precision = 33, scale = 18)
	public BigDecimal getEndBalance() {
		return endBalance;
	}

	public void setEndBalance(BigDecimal endBalance) {
		if (null == endBalance) {
			this.endBalance = BigDecimal.ZERO;
			return;
		}
		this.endBalance = endBalance;
	}

	@Column(name = "FLOW_NO", length = 20)
	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	@Column(name = "DORC", length = 1)
	public String getDorc() {
		return dorc;
	}

	public void setDorc(String dorc) {
		this.dorc = dorc;
	}

	@Column(name = "GROUP_NO", precision = 18, scale = 0)
	public Long getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(Long groupNo) {
		this.groupNo = groupNo;
	}

	@Column(name = "ENTRY_NO", precision = 18, scale = 0)
	public String getEntryNo() {
		return entryNo;
	}

	public void setEntryNo(String entryNo) {
		this.entryNo = entryNo;
	}

    @Column(name="LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Flow copy() {
		Flow copy = new Flow();
		copy.setAccount(this.account);
		copy.setAcctTitle(this.acctTitle);
		copy.setTradeAmount(this.tradeAmount);
		copy.setDorc(this.dorc);
		copy.setMemo(this.memo);
		copy.setEntryNo(this.entryNo);
		return copy;
	}
}
