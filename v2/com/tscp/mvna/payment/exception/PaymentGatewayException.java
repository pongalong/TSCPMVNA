package com.tscp.mvna.payment.exception;

public class PaymentGatewayException extends RuntimeException {
	private static final long serialVersionUID = -831360358390560532L;

	public PaymentGatewayException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PaymentGatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public PaymentGatewayException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PaymentGatewayException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public PaymentGatewayException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
