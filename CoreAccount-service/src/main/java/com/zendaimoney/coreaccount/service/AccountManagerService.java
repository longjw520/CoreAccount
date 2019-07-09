package com.zendaimoney.coreaccount.service;

import java.util.Set;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

/**
 * 提供按照规则生成账号的服务
 * 
 * @author binliu
 * 
 */
@Named
public class AccountManagerService {

	/**
	 * 按照生成规则生成理财分账号(16位总账号+4位流水号)
	 * 
	 * @return 生成的分账号
	 */
	public String generateAccount(String totalAccount, Set<String> existAcct, int nextSeqNo) {
		String curSeqNo = StringUtils.leftPad("" + nextSeqNo, 4, '0');
		if (existAcct.contains(totalAccount + curSeqNo))
			return curSeqNo = generateAccount(totalAccount, existAcct, ++nextSeqNo);
		return totalAccount + curSeqNo;
	}
}
