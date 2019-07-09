package com.zendaimoney.coreaccount.service;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.dao.SysglDao;
import com.zendaimoney.coreaccount.entity.Sysgl;

/**
 * 系统总账开户数累加
 * 
 * @author binliu
 * 
 */
@Named
@Transactional
public class SysglService {

	@Inject
	private SysglDao sysglDao;

	/**
	 * 维护系统总账信息
	 */
	public void maintainMainAccount() {
		Sysgl sysgl = sysglDao.getSysglOfToday();
		if (null == sysgl) {// 首次开户
			sysgl = new Sysgl();
			sysgl.setLogDate(new Date());
			sysgl.setOpenacctTotal(1L);
		} else {
			sysgl.setOpenacctTotal(sysgl.getOpenacctTotal() + 1);
		}
		sysglDao.save(sysgl);
	}
}
