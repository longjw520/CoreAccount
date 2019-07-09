package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.Valid;

/**
 * 报文对象(包含报文头和报文体)
 * 
 * @author liubin
 * @version 1.0
 */
public class Datagram implements Serializable {
	private static final long serialVersionUID = 1L;

	public Datagram() {
	}

	public Datagram(DatagramHeader datagramHeader, DatagramBody datagramBody) {
		this.setDatagramBody(datagramBody);
		this.setDatagramHeader(datagramHeader);
	}

	@Valid
	private DatagramHeader datagramHeader;

	@Valid
	private DatagramBody datagramBody;

	public DatagramHeader getDatagramHeader() {
		return datagramHeader;
	}

	public void setDatagramHeader(DatagramHeader datagramHeader) {
		this.datagramHeader = datagramHeader;
	}

	public DatagramBody getDatagramBody() {
		return datagramBody;
	}

	public void setDatagramBody(DatagramBody datagramBody) {
		this.datagramBody = datagramBody;
	}

	@Override
	public String toString() {
		return this.datagramHeader.toString() + this.datagramBody.toString();
	}

}
