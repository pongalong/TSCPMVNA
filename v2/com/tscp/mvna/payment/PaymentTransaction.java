package com.tscp.mvna.payment;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.telscape.billingserviceinterface.Payment;
import com.tscp.util.ClassUtils;

@XmlRootElement
public class PaymentTransaction implements Serializable {
	private static final long serialVersionUID = 23273165232351086L;
	private int transactionId;
	private PaymentRequest request;
	private PaymentResponse response;
	private PaymentRecord record;

	public PaymentTransaction() {
		// do nothing
	}

	public PaymentTransaction(Payment payment) {
		payment.getPaymentTypeDesc();
		payment.getTrackingId();
		payment.getTransAmount();
		payment.getTransDate();
		payment.getTransType();
		ClassUtils.toString(payment);
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(
			int transactionId) {
		this.transactionId = transactionId;
	}

	public PaymentRequest getRequest() {
		return request;
	}

	public void setRequest(
			PaymentRequest request) {
		this.request = request;
	}

	public PaymentResponse getResponse() {
		return response;
	}

	public void setResponse(
			PaymentResponse response) {
		this.response = response;
	}

	public PaymentRecord getRecord() {
		return record;
	}

	public void setRecord(
			PaymentRecord record) {
		this.record = record;
	}

}