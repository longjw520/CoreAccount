package com.zendaimoney.coreaccount.dao;

import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.entity.BusinessInfo;

@Named
public class BusinessInfoDao extends HibernateDao<BusinessInfo, Long> {
}
