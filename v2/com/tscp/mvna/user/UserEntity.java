package com.tscp.mvna.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UserEntity implements Serializable {
	private static final long serialVersionUID = -1289492699833662745L;
	private int id;

	@Id
	@Column(name = "CUST_ID")
	public int getId() {
		return id;
	}

	public void setId(
			int id) {
		this.id = id;
	}

}