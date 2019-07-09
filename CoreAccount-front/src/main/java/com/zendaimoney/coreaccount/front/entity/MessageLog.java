package com.zendaimoney.coreaccount.front.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 报文日志
 * 
 * @author liubin
 * 
 */
@Entity
@Table(name = "FB_T_MESSAGE_LOG")
public class MessageLog implements Idable {

	private Long id;
	/**
	 * 报文序列号
	 */
	private String messageSequence;

	/**
	 * 请求状态
	 */
	private String requestStatus;

	/**
	 * 请求时间
	 */
	private Date requestDate;

	/**
	 * 报文类型
	 */
	private Long messageTypeId;

	/**
	 * 备注
	 */
	private String memo;

	/**
	 * 请求用户名
	 */
	private String requestName;

	/**
	 * 请求密码
	 */
	private String requestPwd;

	/**
	 * 请求操作人
	 */
	private String requestOperator;

	/**
	 * 请求IP
	 */
	private String requestIp;

	/**
	 * 请求系统
	 */
	private String requestSystem;

	/**
	 * 接收线程
	 */
	private String receiveThread;

	/**
	 * 回调线程
	 */
	private String callbackThread;

	/**
	 * 处理队列ID
	 */
	private String handleQueueId;

	/**
	 * 回调队列ID
	 */
	private String callbackQueueId;

	/**
	 * 回调时间
	 */
	private Date callbackDate;

	private MessageContent messageContent;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_FB_T_MESSAGE_LOG")
	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public MessageContent getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(MessageContent messageContent) {
		this.messageContent = messageContent;
	}

	@Column(name = "MESSAGE_SEQUENCE", length = 30)
	public String getMessageSequence() {
		return messageSequence;
	}

	public void setMessageSequence(String messageSequence) {
		this.messageSequence = messageSequence;
	}

	@Column(name = "REQUEST_STATUS", length = 8)
	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	@Column(name = "REQUEST_DATE")
	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	@Column(name = "MESSAGE_TYPE_ID", length = 18)
	public Long getMessageTypeId() {
		return messageTypeId;
	}

	public void setMessageTypeId(Long messageTypeId) {
		this.messageTypeId = messageTypeId;
	}

	@Column(name = "MEMO", length = 300)
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "REQUEST_NAME", length = 30)
	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	@Column(name = "REQUEST_PWD", length = 64)
	public String getRequestPwd() {
		return requestPwd;
	}

	public void setRequestPwd(String requestPwd) {
		this.requestPwd = requestPwd;
	}

	@Column(name = "REQUEST_OPRATOR", length = 30)
	public String getRequestOperator() {
		return requestOperator;
	}

	public void setRequestOperator(String requestOperator) {
		this.requestOperator = requestOperator;
	}

	@Column(name = "REQUEST_IP", length = 30)
	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	@Column(name = "REQUEST_SYSTEM", length = 30)
	public String getRequestSystem() {
		return requestSystem;
	}

	public void setRequestSystem(String requestSystem) {
		this.requestSystem = requestSystem;
	}

	@Column(name = "RECEIVING_THREAD", length = 150)
	public String getReceiveThread() {
		return receiveThread;
	}

	public void setReceiveThread(String receiveThread) {
		this.receiveThread = receiveThread;
	}

	@Column(name = "CALLBACK_THREAD", length = 150)
	public String getCallbackThread() {
		return callbackThread;
	}

	public void setCallbackThread(String callbackThread) {
		this.callbackThread = callbackThread;
	}

	@Column(name = "HANDLE_QUEUE_ID", length = 4)
	public String getHandleQueueId() {
		return handleQueueId;
	}

	public void setHandleQueueId(String handleQueueId) {
		this.handleQueueId = handleQueueId;
	}

	@Column(name = "CALLBACK_QUEUE_ID", length = 4)
	public String getCallbackQueueId() {
		return callbackQueueId;
	}

	public void setCallbackQueueId(String callbackQueueId) {
		this.callbackQueueId = callbackQueueId;
	}

	@Column(name = "CALLBACK_DATE")
	public Date getCallbackDate() {
		return callbackDate;
	}

	public void setCallbackDate(Date callbackDate) {
		this.callbackDate = callbackDate;
	}

}
