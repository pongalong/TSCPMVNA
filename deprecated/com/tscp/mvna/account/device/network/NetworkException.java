package com.tscp.mvna.account.device.network;

@Deprecated
public class NetworkException extends RuntimeException {
	private static final long serialVersionUID = -6908296295050562837L;
	OldNetworkInfo networkinfo;


	public OldNetworkInfo getNetworkinfo() {
		return networkinfo;
	}

	public void setNetworkinfo(
			OldNetworkInfo networkinfo) {
		this.networkinfo = networkinfo;
	}

	public NetworkException(String message, String cause) {

		// TODO Auto-generated constructor stub
	}

	public NetworkException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NetworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NetworkException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NetworkException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
