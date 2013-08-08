package com.tscp.mvna.payment;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class PaymentTransaction implements Serializable {
	private int transactionId;
	private PaymentRequest request;
	private PaymentResponse response;
	private PaymentRecord record;

}