package com.zendaimoney.coreaccount.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "AC_T_MESSAGE")
public class Message {

	private long id;
	private String messageSequence;
	private Long handleQueueId;
	private Long callbackQueueId;
	private Date handleDate = new Date();
	private String content;
	/**
	 * 请求系统
	 */
	private String requestSystem;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_MESSAGE")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "MESSAGE_SEQUENCE", length = 30)
	public String getMessageSequence() {
		return this.messageSequence;
	}

	public void setMessageSequence(String messageSequence) {
		this.messageSequence = messageSequence;
	}

	@Column(name = "HANDLE_QUEUE_ID", precision = 18, scale = 0)
	public Long getHandleQueueId() {
		return this.handleQueueId;
	}

	public void setHandleQueueId(Long handleQueueId) {
		this.handleQueueId = handleQueueId;
	}

	@Column(name = "CALLBACK_QUEUE_ID", precision = 18, scale = 0)
	public Long getCallbackQueueId() {
		return this.callbackQueueId;
	}

	public void setCallbackQueueId(Long callbackQueueId) {
		this.callbackQueueId = callbackQueueId;
	}

	@Column(name = "HANDLE_DATE", length = 7)
	public Date getHandleDate() {
		return this.handleDate;
	}

	public void setHandleDate(Date handleDate) {
		this.handleDate = handleDate;
	}

	@Column(name = "CONTENT")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "REQUEST_SYSTEM", length = 30)
	public String getRequestSystem() {
		return requestSystem;
	}

	public void setRequestSystem(String requestSystem) {
		this.requestSystem = requestSystem;
	}

}
