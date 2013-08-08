package com.tscp.mvna.payment.service;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PaymentGatewayResponse extends PaymentGatewayResponseEntity {
	private static final long serialVersionUID = -4334544877266056217L;

	@Override
	@Id
	public int getGatewayTransactionId() {
		return gatewayTransactionId;
	}

	@Override
	public String toString() {
		return "PaymentGatewayResponse [gatewayTransactionId=" + gatewayTransactionId + ", confirmationCode=" + confirmationCode + ", confirmationMsg=" + confirmationMsg + ", authorizationCode=" + authorizationCode + ", cvvCode=" + cvvCode + "]";
	}

}