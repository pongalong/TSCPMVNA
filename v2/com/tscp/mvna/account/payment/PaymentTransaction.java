package com.tscp.mvna.account.payment;

import java.io.Serializable;

import javax.persistence.Entity;

import com.tscp.mvne.payment.dao.PaymentRecord;

@Entity
public class PaymentTransaction implements Serializable {
	private int transactionId;
	private PaymentRequest request;
	private PaymentResponse response;
	private PaymentRecord record;

}
