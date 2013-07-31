package com.tscp.mvne.payment;

import com.tscp.mvne.exception.MVNEException;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;

public class PaymentException extends MVNEException {
	private static final long serialVersionUID = 8144586037731331146L;
	private PaymentUnitResponse response;

	public PaymentException() {
		super();
	}

	public PaymentException(String methodName, String message, Throwable cause) {
		super(methodName, message, cause);
	}

	public PaymentException(String methodName, String message) {
		super(methodName, message);
	}

	public PaymentException(String message, Throwable cause) {
		super(message, cause);
	}

	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(Throwable cause) {
		super(cause);
	}

	public PaymentUnitResponse getResponse() {
		return response;
	}

	public void setResponse(
			PaymentUnitResponse response) {
		this.response = response;
	}

}