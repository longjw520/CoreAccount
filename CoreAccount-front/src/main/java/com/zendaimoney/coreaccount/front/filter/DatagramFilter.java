package com.zendaimoney.coreaccount.front.filter;

import org.apache.log4j.Logger;

import com.zendaimoney.exception.BusinessException;

/**
 * 请求报文过滤器
 * 
 * @author Jianlong Ma
 * 
 */
public abstract class DatagramFilter {
	protected Logger logger = Logger.getLogger(this.getClass());

	public abstract void doFilter(Object obj) throws BusinessException;
}
