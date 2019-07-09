package com.zendaimoney.coreaccount.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 事务性流水
 * 
 * @author larry
 * 
 */
@Entity
@Table(name = "AC_T_WORK_FLOW")
public class WorkFlow {
	private long id;
	/** 对应事务ID */
	private Long businessId;

	/** 交易值 */
	private String tradeValue;

	/** 操作对象ID */
	private Long objectId;

	/** 起初值 */
	private String startValue;

	/** 冲正号 */
	private Long reversedNo;

	/** 期末值 */
	private String endValue;

	/** 流水号 */
	private Long flowNo;

	/** 备注 */
	private String memo;

	/** 事务类型ID */
	private Long businessTypeId;
    /**记录最后修改时间*/
    private Date lastModified=new Date();

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_WORK_FLOW")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "BUSINESS_ID", precision = 18, scale = 0)
	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	@Column(name = "TRADE_VALUE", length = 30)
	public String getTradeValue() {
		return tradeValue;
	}

	public void setTradeValue(String tradeValue) {
		this.tradeValue = tradeValue;
	}

	@Column(name = "OBJCET_ID", precision = 18, scale = 0)
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@Column(name = "START_VALUE", length = 30)
	public String getStartValue() {
		return startValue;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	@Column(name = "REVERSED_NO", precision = 18, scale = 0)
	public Long getReversedNo() {
		return reversedNo;
	}

	public void setReversedNo(Long reversedNo) {
		this.reversedNo = reversedNo;
	}

	@Column(name = "END_VALUE", length = 30)
	public String getEndValue() {
		return endValue;
	}

	public void setEndValue(String endValue) {
		this.endValue = endValue;
	}

	@Column(name = "FLOW_NO", length = 20)
	public Long getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(Long flowNo) {
		this.flowNo = flowNo;
	}

	@Column(name = "MEMO", length = 150)
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "BUSINESS_TYPE_ID", precision = 18, scale = 0)
	public Long getBusinessTypeId() {
		return businessTypeId;
	}

	public void setBusinessTypeId(Long businessTypeId) {
		this.businessTypeId = businessTypeId;
	}

    @Column(name="LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

	@Transient
	public WorkFlow copy() {
		WorkFlow flow = new WorkFlow();
		flow.setBusinessId(this.getBusinessId());
		flow.setBusinessTypeId(this.getBusinessTypeId());
		flow.setTradeValue(this.getTradeValue());
		flow.setObjectId(this.getObjectId());
		return flow;
	}

}
