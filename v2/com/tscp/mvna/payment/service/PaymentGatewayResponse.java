package com.tscp.mvna.payment.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class PaymentGatewayResponse implements PaymentGatewayObject, Serializable {
	private static final long serialVersionUID = -7428346015795926109L;
	public static final String SUCCESSFUL_TRANSACTION = "0";
	private int gatewayTransactionId;
	protected String confirmationCode;
	protected String confirmationMsg;
	protected String authorizationCode;
	protected String cvvCode;

	@Id
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

	@Transient
	public boolean isSuccess() {
		return confirmationCode != null && confirmationCode.equalsIgnoreCase(SUCCESSFUL_TRANSACTION);
	}

	@Override
	public String toString() {
		return "PaymentGatewayResponse [gatewayTransactionId=" + gatewayTransactionId + ", confirmationCode=" + confirmationCode + ", confirmationMsg=" + confirmationMsg + ", authorizationCode=" + authorizationCode + ", cvvCode=" + cvvCode + "]";
	}

}