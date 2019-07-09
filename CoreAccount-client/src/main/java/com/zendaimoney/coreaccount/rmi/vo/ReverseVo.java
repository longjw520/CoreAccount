package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 冲正
 * 
 * @author larry
 * 
 */
public class ReverseVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = -325481471847140303L;

	/**
	 * 报文序列号
	 */
	@NotBlank
	@Size(max = 30)
	private String reverseMessageSequence;

	/**
	 * 业务类型
	 */
	@NotBlank
	@Size(max = 8)
	private String messageCode;

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getReverseMessageSequence() {
		return reverseMessageSequence;
	}

	public void setReverseMessageSequence(String reverseMessageSequence) {
		this.reverseMessageSequence = reverseMessageSequence;
	}

}
