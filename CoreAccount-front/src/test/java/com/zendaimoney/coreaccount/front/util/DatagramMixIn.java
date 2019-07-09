package com.zendaimoney.coreaccount.front.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;

@JsonIgnoreProperties({ "datagramBody" })
public class DatagramMixIn extends Datagram {
	private static final long serialVersionUID = 1L;

}
