package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import com.zendaimoney.coreaccount.rmi.builder.ToStringBuilder;

/**
 * 报文头
 * 
 * @author binliu
 * 
 */
public class DatagramHeader implements Serializable {
	private static final long serialVersionUID = 7409353293039683346L;

	/** 版本号 */
	@NotBlank
	@Size(max = 8)
	private String messageVer;

	/** 加密标识 */
	@NotBlank
	@Size(max = 64)
	private String encryptionTag;

	/** 用户名 */
	@NotBlank
	@Size(max = 30)
	private String userName;

	/** 密码 */
	@NotBlank
	@Size(max = 64)
	private String password;

	/** 报文发起人ID */
	@NotBlank
	@Size(max = 30)
	private String senderId;

	/** 报文发送系统 */
	@NotBlank
	@Size(max = 30)
	private String senderSystemCode;

	/** 接收系统 */
	@NotBlank
	@Size(max = 30)
	private String receiverSystem;

	/** 报文发起时间 */
	@NotBlank
	@Size(max = 19)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String sendTime;

	/** 格式类型 */
	@NotBlank
	@Size(max = 18)
	private String format;

	/** 报文序列号 */
	@NotBlank
	@Size(max = 30)
	private String messageSequence;

	/** 报文业务类型编码 */
	@NotBlank
	@Size(max = 8)
	private String messageCode;

	/** 报文体长度 */
	@Min(1)
	@Max(3000)
	private int length;

	/** 报文优先级 */
	@Min(1)
	@Max(99)
	private int priority;

	/** 报文执行情况 */
	@NotBlank
	@Size(max = 8)
	private String requestStatus;

	/** 实际交易时间 */
	@NotBlank
	@Size(max = 19)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String actualTradeTime;
	
	/**  businessID for performance*/
	@JsonIgnore
	private long businessId;
	@JsonIgnore
	private long businessTypeId;
	
	public long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(long businessId) {
		this.businessId = businessId;
	}
	
	public long getBusinessTypeId() {
		return businessTypeId;
	}

	public void setBusinessTypeId(long businessTypeId) {
		this.businessTypeId = businessTypeId;
	}

	public String getMessageVer() {
		return messageVer;
	}

	public void setMessageVer(String messageVer) {
		this.messageVer = messageVer;
	}

	public String getEncryptionTag() {
		return encryptionTag;
	}

	public void setEncryptionTag(String encryptionTag) {
		this.encryptionTag = encryptionTag;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderSystemCode() {
		return senderSystemCode;
	}

	public void setSenderSystemCode(String senderSystemCode) {
		this.senderSystemCode = senderSystemCode;
	}

	public String getReceiverSystem() {
		return receiverSystem;
	}

	public void setReceiverSystem(String receiverSystem) {
		this.receiverSystem = receiverSystem;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMessageSequence() {
		return messageSequence;
	}

	public void setMessageSequence(String messageSequence) {
		this.messageSequence = messageSequence;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getActualTradeTime() {
		return actualTradeTime;
	}

	public void setActualTradeTime(String actualTradeTime) {
		this.actualTradeTime = actualTradeTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.build(this);
	}
}
