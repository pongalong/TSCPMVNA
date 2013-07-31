package com.tscp.mvna.account.exception;

import com.tscp.mvna.account.kenan.exception.AccountUpdateException;

public class AccountSuspendException extends AccountUpdateException {
	private static final long serialVersionUID = -3539299316800124583L;

	public AccountSuspendException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountSuspendException(String methodName, String message, Throwable cause) {
		super(methodName, message, cause);
		// TODO Auto-generated constructor stub
	}

	public AccountSuspendException(String methodName, String message) {
		super(methodName, message);
		// TODO Auto-generated constructor stub
	}

	public AccountSuspendException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AccountSuspendException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AccountSuspendException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
