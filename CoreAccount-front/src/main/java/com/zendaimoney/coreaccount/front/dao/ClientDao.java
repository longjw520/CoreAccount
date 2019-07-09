package com.zendaimoney.coreaccount.front.dao;

import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.front.entity.Client;

@Named
public class ClientDao extends HibernateDao<Client, Long> {

}
