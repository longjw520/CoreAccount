package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 查询结果集
 * 
 */
public class QueryResultVo extends PageVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryResultVo() {

	}

	public QueryResultVo(Integer pageNo, Integer pageSize) {
		this.setPageSize(pageSize);
		this.setPageNo(pageNo);
	}

	private List<?> result;

	public List<?> getResult() {
		return result;
	}

	public void setResult(List<?> result) {
		this.result = result;
	}

}
