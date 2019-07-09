package com.zendaimoney.coreaccount.front.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "FB_T_MESSAGE_CONTENT")
public class MessageContent implements Idable {

	/**
	 * 对应日志ID
	 */
	private Long id;

	/**
	 * 接收报文内容
	 */
	private String receiverContent;

	/**
	 * 返回报文内容
	 */
	private String callbackContent;

	/**
	 * 操作时间
	 */
	private Date operateTime = new Date();

	@Column(name = "CREATE_DATE")
	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "RECEIVER_CONTENT")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getReceiverContent() {
		return receiverContent;
	}

	public void setReceiverContent(String receiverContent) {
		this.receiverContent = receiverContent;
	}

	@Column(name = "CALLBACK_CONTENT")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getCallbackContent() {
		return callbackContent;
	}

	public void setCallbackContent(String callbackContent) {
		this.callbackContent = callbackContent;
	}

}
