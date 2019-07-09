package com.zendaimoney.coreaccount.util;

import com.zendaimoney.utils.DateUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

/**
 * 系统工具类
 * 
 * @author binliu
 * 
 */
public abstract class SystemUtil {

	private static InetAddress inetAddress;
	static {
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
	}
	private static String ipAddr = inetAddress != null ? inetAddress.getHostAddress() : "Unknown address";

	static public String getIpAddr() {
		return ipAddr;
	}

	static public Date currentDate() {
		return DateUtils.truncate(new Date(), Calendar.DATE);
	}

}
