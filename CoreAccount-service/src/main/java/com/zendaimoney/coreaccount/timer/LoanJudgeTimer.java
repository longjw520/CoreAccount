package com.zendaimoney.coreaccount.timer;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.email.SimpleMailService;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.BusinessType;
import com.zendaimoney.coreaccount.rmi.vo.AccountStaUpdateVo;
import com.zendaimoney.coreaccount.service.BusinessTypeService;
import com.zendaimoney.coreaccount.service.LedgerFinanceService;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.coreaccount.util.SystemUtil;

/**
 * Copyright (c) 2012 ZENDAI. All Rights Reserved. This software is published
 * under the terms of the ZENDAI Software
 * 
 * @description:债权有效性判断
 */
@Service
public class LoanJudgeTimer {
	private static Logger logger = LoggerFactory.getLogger(LoanJudgeTimer.class);
	@Inject
	private LedgerFinanceService ledgerFinanceService;
	@Inject
	private BusinessTypeService businessTypeService;
	@Inject
	private LedgerLoanService ledgerLoanService;
	@Inject
	private SimpleMailService simpleMailService;
	
	/**
	 * @description: step1:更新理财分户1=>>2,
	 *       		 step2:更新贷款分户3=>>6
	 */
	public void execute() {
		logger.info("债权有效性判断，开始........");
		List<AccountStaUpdateVo> voList = new ArrayList<AccountStaUpdateVo>();//待更新数据
		List<StringBuilder> result = new ArrayList<StringBuilder>();//记录失败信息
		int success = 0;//记录成功笔数
		String errinfo = null;//记录错误信息
		
		try {
			/*取得待更新的理财分户*/
			List<Object> finances = ledgerFinanceService.queryForLoanJudgeTimer();
			
			if(finances != null & finances.size() > 0){
				//取得待更新理财分户数据
				voList.addAll(this.getLedgerFinanceList(finances));
			}
			result.addAll(statusProgress(voList));
			success += voList.size();
			voList.clear();
			
			/*取得待更新的贷款分户id*/
			List<Object> loanids = ledgerLoanService.queryForLoanJudgeTimer();
			
			if(loanids != null && loanids.size() > 0){
				voList.addAll(this.getLedgerLoanList(loanids));
			}
			
			/**更新分户状态**/
			result = statusProgress(voList);
			success += voList.size();
			
			logger.info("债权有效性判断，结束........");
		} catch (Exception e) {
			logger.info("债权有效性判断失败");
			e.printStackTrace();
			errinfo = e.getMessage();
		} finally {
			String subject = PropertiesReader.readAsString("loan.judge.timer.title");
			subject = MessageFormat.format(subject, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), SystemUtil.getIpAddr());
			StringBuilder content = new StringBuilder("");
			if(result != null && result.size()>0 ){
				for(StringBuilder sb : result){
					content.append(sb);
				}
			}
			if(errinfo != null){
				simpleMailService.sendMail(subject, null, "债权有效性判断失败:"+errinfo);
			}else{
				simpleMailService.sendMail(subject, null, "总共:" + (success+result.size()) + "笔,成功" + success + "笔,失败" + result.size() + "笔 \n " + content.toString());
			}
		}

	}
	
	private List<AccountStaUpdateVo>  getLedgerLoanList(List<Object> loanids){
		List<AccountStaUpdateVo> voList = new ArrayList<AccountStaUpdateVo>();
		for (Object objArray : loanids) {
			Long loanid = Long.valueOf(objArray.toString());
			AccountStaUpdateVo accountStaUpdateVo = new AccountStaUpdateVo();
			accountStaUpdateVo.setId(loanid);
			accountStaUpdateVo.setAcctStatus("6");
			accountStaUpdateVo.setBusiType(Constants.BUSINESS_TYPE_LOAN);
			voList.add(accountStaUpdateVo);
		}
		return voList;
	}
	
	private List<StringBuilder> statusProgress(List<AccountStaUpdateVo> voList){
		BusinessType businessType = businessTypeService.getBusinessTypeBy(Constants.MESSAGE_BUSINESS_TYPE_ACCOUNT_STA_UPDATE);
		List<StringBuilder> reslult = new ArrayList<StringBuilder>();
		
		String busiType = "";
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setBusinessTypeId(businessType.getId());
		for (AccountStaUpdateVo accountStaUpdateVo : voList) {
			try {
				if (Constants.BUSINESS_TYPE_LOAN.equals(accountStaUpdateVo.getBusiType())) {// 贷款
					busiType = "贷款分户";
					ledgerLoanService.updateStatus(accountStaUpdateVo, businessInfo);
				} else {// 理财
					busiType = "理财分户";
					ledgerFinanceService.updateStatus(accountStaUpdateVo, businessInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringBuilder content = new StringBuilder(busiType + accountStaUpdateVo.getId() + "更新失败:" + e.getMessage() + "\n");
				reslult.add(content);
			}
		}
		
		return reslult;
	} 
	
	public List<AccountStaUpdateVo> getLedgerFinanceList(List<Object> finances) {
		List<AccountStaUpdateVo> voList = new ArrayList<AccountStaUpdateVo>();
		for (Object objArray : finances) {
			Long financeid = Long.valueOf(objArray.toString());
			AccountStaUpdateVo accountStaUpdateVo = new AccountStaUpdateVo();
			accountStaUpdateVo.setId(financeid);
			accountStaUpdateVo.setAcctStatus("2");
			accountStaUpdateVo.setBusiType(Constants.BUSINESS_TYPE_FINANCING);
			voList.add(accountStaUpdateVo);
		}
		return voList;
	}
	
}
