package com.zendaimoney.coreaccount.service.message.mixin;

import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;

public class LedgerLoanPvMixIn extends DatagramBody {
	private static final long serialVersionUID = 1L;
	private String pv;

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}
}
