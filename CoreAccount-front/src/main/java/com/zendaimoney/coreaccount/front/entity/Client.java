package com.zendaimoney.coreaccount.front.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 客户端信息
 * 
 * @author liubin
 * 
 */
@Entity
@Table(name = "FB_T_CLIENT")
public class Client implements Idable {

	private Long id;
	/**
	 * 客户端请求的IP地址
	 */
	private String ipInfo;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 操作密码
	 */
	private String operationPwd;

	/**
	 * 查询密码
	 */
	private String queryPwd;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_FB_T_CLIENT")
	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "IP_INFO", length = 30)
	public String getIpInfo() {
		return ipInfo;
	}

	public void setIpInfo(String ipInfo) {
		this.ipInfo = ipInfo;
	}

	@Column(name = "USER_NAME", length = 30)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "OPERATION_PASSWORD", length = 64)
	public String getOperationPwd() {
		return operationPwd;
	}

	public void setOperationPwd(String operationPwd) {
		this.operationPwd = operationPwd;
	}

	@Column(name = "QUERY_PASSWORD", length = 64)
	public String getQueryPwd() {
		return queryPwd;
	}

	public void setQueryPwd(String queryPwd) {
		this.queryPwd = queryPwd;
	}

}
