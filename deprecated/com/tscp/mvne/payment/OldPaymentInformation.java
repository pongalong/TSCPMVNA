package com.tscp.mvne.payment;

import com.tscp.mvne.payment.dao.PaymentTransaction;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;

@Deprecated
public abstract class OldPaymentInformation {
	protected int paymentId;
	protected String alias;
	protected String address1;
	protected String address2;
	protected String city;
	protected String state;
	protected String zip;
	protected boolean defaultPayment;

	public String getAlias() {
		return alias;
	}

	public void setAlias(
			String alias) {
		this.alias = alias;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(
			String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(
			String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(
			String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(
			String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(
			String zip) {
		this.zip = zip;
	}

	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(
			int paymentid) {
		this.paymentId = paymentid;
	}

	public boolean isDefaultPayment() {
		return defaultPayment;
	}

	public void setDefaultPayment(
			boolean defaultPayment) {
		this.defaultPayment = defaultPayment;
	}

	public abstract PaymentType getPaymentType();

	public abstract PaymentUnitResponse submitPayment(
			PaymentTransaction transaction) throws PaymentException;

	public abstract void save() throws PaymentException;

	public abstract void update() throws PaymentException;

	public abstract void delete() throws PaymentException;

	public abstract boolean validate() throws PaymentException;
}
