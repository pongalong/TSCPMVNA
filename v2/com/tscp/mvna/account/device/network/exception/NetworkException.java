package com.tscp.mvna.account.device.network.exception;

import com.tscp.mvna.account.device.network.NetworkInfo;

public class NetworkException extends Exception {
	private static final long serialVersionUID = -7412354082633059506L;
	protected NetworkInfo networkInfo;

	public NetworkException() {
		super();
	}

	public NetworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkException(String message) {
		super(message);
	}

	public NetworkException(Throwable cause) {
		super(cause);
	}

	public NetworkInfo getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(
			NetworkInfo networkInfo) {
		this.networkInfo = networkInfo;
	}

}
