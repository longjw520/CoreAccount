package com.zendaimoney.coreaccount.front.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.front.dao.ClientDao;
import com.zendaimoney.coreaccount.front.entity.Client;

@Named
@Transactional
public class ClientService {

	@Inject
	private ClientDao clientDao;

	/**
	 * 使用spring的缓存机制缓存业务类型信息
	 * 
	 * @param code
	 * @return
	 */
	@Transactional(readOnly = true)
	public Client getClient(String userName) {
		return clientDao.findUniqueBy("userName", userName);
	}
}
