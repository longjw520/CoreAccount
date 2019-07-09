package com.zendaimoney.coreaccount.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.dao.BusinessTypeDao;
import com.zendaimoney.coreaccount.entity.BusinessType;

@Named
@Transactional
public class BusinessTypeService {

	@Inject
	private BusinessTypeDao businessTypeDao;
	/**
	 * 使用spring的缓存机制缓存业务类型信息
	 * @param code
	 * @return
	 */
	@Cacheable(value = "businessTypeCache")
	public BusinessType getBusinessTypeBy(String code) {
		return businessTypeDao.findUniqueBy("code", code);
	}
}
