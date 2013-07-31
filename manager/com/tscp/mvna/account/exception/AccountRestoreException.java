package com.tscp.mvna.account.exception;

import com.tscp.mvna.account.kenan.exception.AccountUpdateException;

public class AccountRestoreException extends AccountUpdateException {
	private static final long serialVersionUID = 7352740281811972321L;

	public AccountRestoreException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountRestoreException(String methodName, String message, Throwable cause) {
		super(methodName, message, cause);
		// TODO Auto-generated constructor stub
	}

	public AccountRestoreException(String methodName, String message) {
		super(methodName, message);
		// TODO Auto-generated constructor stub
	}

	public AccountRestoreException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AccountRestoreException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AccountRestoreException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
