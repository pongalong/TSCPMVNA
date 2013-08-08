package com.tscp.mvna.payment.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class PaymentGatewayResponseEntity implements PaymentGatewayObject, Serializable {
	private static final long serialVersionUID = -7428346015795926109L;
	public static final String SUCCESSFUL_TRANSACTION = "0";
	protected int gatewayTransactionId;
	protected String confirmationCode;
	protected String confirmationMsg;
	protected String authorizationCode;
	protected String cvvCode;

	@Override
	@Column(name = "GATEWAY_TRANS_ID")
	public int getGatewayTransactionId() {
		return gatewayTransactionId;
	}

	public void setGatewayTransactionId(
			int gatewayTransactionId) {
		this.gatewayTransactionId = gatewayTransactionId;
	}

	@Column(name = "CONF_CODE")
	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(
			String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	@Column(name = "CONF_MSG")
	public String getConfirmationMsg() {
		return confirmationMsg;
	}

	public void setConfirmationMsg(
			String confirmationMsg) {
		this.confirmationMsg = confirmationMsg;
	}

	@Column(name = "AUTH_CODE")
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(
			String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	@Column(name = "CVV_CODE")
	public String getCvvCode() {
		return cvvCode;
	}

	public void setCvvCode(
			String cvvCode) {
		this.cvvCode = cvvCode;
	}

}