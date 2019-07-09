package com.zendaimoney.coreaccount.dao;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Restrictions;
import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.Sysgl;

@Named
public class SysglDao extends HibernateDao<Sysgl, Long> {
	
	/**
	 * 获取当前总账信息
	 * @return
	 */
	public Sysgl getSysglOfToday() {
		String today = DateFormatUtils.format(Calendar.getInstance(), Constants.DATE_FORMAT);
		Date startTime = null, endTime = null;
		try {
			startTime = DateUtils.parseDate(today + " 00:00:00", Constants.DATE_TIME_FORMAT);
		    endTime = DateUtils.parseDate(today + " 23:59:59", Constants.DATE_TIME_FORMAT);
		} catch (ParseException e) {
		}
		List<Sysgl> result = find(createCriteria().add(Restrictions.between("logDate", startTime, endTime)));
		if (result.isEmpty())
			return null;
		return result.get(0);
	}
}
