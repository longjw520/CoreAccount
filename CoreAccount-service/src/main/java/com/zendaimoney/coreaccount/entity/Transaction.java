package com.zendaimoney.coreaccount.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "FT_T_TRANSACTION@TEST")
public class Transaction {
	
	private long id;
	/** 分账号 */
	
	private long holdId;
	/** 分账号 */

	
	@Id
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHoldId() {
		return holdId;
	}

	public void setHoldId(long holdId) {
		this.holdId = holdId;
	}
	
	
	
	

}
