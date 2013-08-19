package com.tscp.mvna;

public class Problem {
	private Object obj;
	private String description;

	public Problem(Object obj, String description) {
		this.obj = obj;
		this.description = description;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(
			Object obj) {
		this.obj = obj;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(
			String description) {
		this.description = description;
	}

}
