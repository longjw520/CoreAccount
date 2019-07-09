package com.zendaimoney.coreaccount.dao;

import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.entity.BusinessType;

@Named
public class BusinessTypeDao extends HibernateDao<BusinessType, Long> {
	
}
