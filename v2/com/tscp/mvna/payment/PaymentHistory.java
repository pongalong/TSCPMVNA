package com.tscp.mvna.payment;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.PaymentHolder;
import com.tscp.mvna.TimeSensitive;
import com.tscp.mvna.account.kenan.KenanObject;

public class PaymentHistory implements KenanObject, TimeSensitive {
	protected static final Logger logger = LoggerFactory.getLogger(PaymentHistory.class);
	private List<PaymentTransaction> payments;

	private DateTime instantiationTime = new DateTime();
	private boolean loaded;

	public PaymentHistory() {

	}

	public PaymentHistory(List<PaymentHolder> paymentHolderList) {
		payments = new ArrayList<PaymentTransaction>();

		for (PaymentHolder ph : paymentHolderList)
			payments.add(new PaymentTransaction(ph.getPayment()));
	}

	public List<PaymentTransaction> getPayments() {
		return payments;
	}

	public void setPayments(
			List<PaymentTransaction> payments) {
		this.payments = payments;
	}

	@Override
	public boolean isStale() {
		Period elapsed = new Period(instantiationTime, new DateTime());
		return elapsed.getMinutes() > 15;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

}