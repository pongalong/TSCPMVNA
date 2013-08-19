package com.tscp.mvna.dao;

import java.io.Serializable;

public class GeneralSPResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String status;
	private String msg;
	private int code;

	public boolean isSuccess() {
		return status != null && status.equals("Y");
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(
			String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(
			String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(
			int code) {
		this.code = code;
	}

}