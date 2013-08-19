package com.tscp.mvna.payment.refund;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.tscp.mvna.payment.PaymentTransaction;
import com.tscp.mvna.user.UserEntity;

public class RefundV2 implements Serializable {
	private static final long serialVersionUID = -7181877219517648132L;
	private PaymentTransaction paymentTransaction;
	private DateTime refundDate;
	private UserEntity refundBy;
	private RefundReason refundReason;
	private String notes;

	public PaymentTransaction getPaymentTransaction() {
		return paymentTransaction;
	}

	public void setPaymentTransaction(
			PaymentTransaction paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
	}

	public DateTime getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(
			DateTime refundDate) {
		this.refundDate = refundDate;
	}

	public UserEntity getRefundBy() {
		return refundBy;
	}

	public void setRefundBy(
			UserEntity refundBy) {
		this.refundBy = refundBy;
	}

	public RefundReason getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(
			RefundReason refundReason) {
		this.refundReason = refundReason;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(
			String notes) {
		this.notes = notes;
	}

}