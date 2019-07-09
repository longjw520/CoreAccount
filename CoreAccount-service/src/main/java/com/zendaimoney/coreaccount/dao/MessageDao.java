package com.zendaimoney.coreaccount.dao;

import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.entity.Message;

@Named
public class MessageDao extends HibernateDao<Message, Long> {

}
