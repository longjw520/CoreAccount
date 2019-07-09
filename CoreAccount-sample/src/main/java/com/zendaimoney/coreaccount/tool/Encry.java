package com.zendaimoney.coreaccount.tool;

public class Encry {
	public static String encode(String msg) {
		try {
			String key = DESCoder.initKey("uaLxzT1wiko=");
			byte[] inputData = msg.getBytes();
			inputData = DESCoder.encrypt(inputData, key);
			return DESCoder.encryptBASE64(inputData);
		} catch (Exception e) {
		}
		return null;
	}
}
