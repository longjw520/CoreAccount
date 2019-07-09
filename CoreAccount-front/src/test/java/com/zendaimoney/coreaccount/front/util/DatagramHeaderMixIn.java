package com.zendaimoney.coreaccount.front.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;

@JsonIgnoreProperties(value = { "messageVer" })
public class DatagramHeaderMixIn extends DatagramHeader {
	private static final long serialVersionUID = 1L;

}
