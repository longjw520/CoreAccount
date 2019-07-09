package com.zendaimoney.coreaccount.front.dao;

import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.front.entity.MessageInfo;

@Named
public class MessageInfoDao extends HibernateDao<MessageInfo, Long> {

}
