package com.zendaimoney.coreaccount.front.filter.auth;

/**
 * 校验用户信息
 * 
 * @author binliu
 * 
 */
public final class User {

	private String userName;
	private String ip;
	private String queryPwd;
	private String pwd;
	private boolean queryAction;

	public User(String userName, String ip, String queryPwd, String pwd, boolean queryAction) {
		this.userName = userName;
		this.ip = ip;
		this.queryPwd = queryPwd;
		this.pwd = pwd;
		this.queryAction = queryAction;
	}

	public boolean isQueryAction() {
		return queryAction;
	}

	public String getUserName() {
		return userName;
	}

	public String getIp() {
		return ip;
	}

	public String getQueryPwd() {
		return queryPwd;
	}

	public String getPwd() {
		return pwd;
	}

	@Override
	public boolean equals(Object otherObj) {
		if (otherObj instanceof User) {
			User otherUser = (User) otherObj;

			if (queryAction) {
				return this.userName.equals(otherUser.getUserName()) && this.queryPwd.equals(otherUser.getQueryPwd()) && this.ip.equals(otherUser.getIp());
			}
			return this.userName.equals(otherUser.getUserName()) && this.pwd.equals(otherUser.getPwd()) && this.ip.equals(otherUser.getIp());
		}
		return false;

	}

	@Override
	public int hashCode() {
		int hashCode = this.queryAction ? this.queryPwd.hashCode() : this.pwd.hashCode();
		return 37 + this.userName.hashCode() + hashCode + this.ip.hashCode();
	}
}
