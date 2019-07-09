package com.zendaimoney.coreaccount.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * 主要用于从.json文件中读取字符串(测试用)
 * 
 * @author liubin
 * @version 1.0
 * 
 */
public class BufferedInputFile {

	private BufferedInputFile() {
	}

	/**
	 * 返回文件内容的字符串表示
	 * 
	 * @param filePath
	 *            (文件路径)
	 * @return
	 */
	public static String read(String filePath) {
		StringBuilder buf = new StringBuilder();
		String txt = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new ClassPathResource(filePath).getInputStream()));
			while ((txt = reader.readLine()) != null) {
				buf.append(txt).append("\n");
			}
		} catch (IOException ignore) {
		} finally {
			IOUtils.closeQuietly(reader);
		}
		if (buf.length() > 0)
			return buf.deleteCharAt(buf.length() - 1).toString();
		return "";
	}

}
