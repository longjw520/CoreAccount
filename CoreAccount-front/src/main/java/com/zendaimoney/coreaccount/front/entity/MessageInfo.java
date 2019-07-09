package com.zendaimoney.coreaccount.front.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 报文属性
 * 
 * @author liubin
 * 
 */
@Entity
@Table(name = "FB_T_MESSAGE_INFO")
public class MessageInfo implements Idable {

	private Long id;
	/**
	 * 报文类型
	 */
	private String messageType;

	/**
	 * 报文类型名称
	 */
	private String messageName;

	/**
	 * 数据项个数
	 */
	private Integer items;

	/**
	 * 报文版本号
	 */
	private String messageVer;

	/**
	 * 报文事务类型编码
	 */
	private String messageCode;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_FB_T_MESSAGE_INFO")
	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "MESSAGE_TYPE", length = 2)
	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	@Column(name = "MESSAGE_NAME", length = 30)
	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	@Column(name = "ITEMS", length = 3)
	public Integer getItems() {
		return items;
	}

	public void setItems(Integer items) {
		this.items = items;
	}

	@Column(name = "MESSAGE_VER", length = 8)
	public String getMessageVer() {
		return messageVer;
	}

	public void setMessageVer(String messageVer) {
		this.messageVer = messageVer;
	}

	@Column(name = "MESSAGE_CODE", length = 8)
	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

}
