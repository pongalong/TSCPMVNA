package com.tscp.mvna.account;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.telscape.billingserviceinterface.CustBalanceHolder;

@XmlRootElement
public class Balance {
	protected static final DecimalFormat df = new DecimalFormat("0.00000");
	private Double value;
	private DateTime dateTime = new DateTime();

	public Balance() {
		// do nothing;
	}

	public Balance(CustBalanceHolder valueHolder) {
		if (valueHolder != null)
			value = valueHolder.getCustBalance().getRealBalance() * -1;
	}

	@XmlAttribute
	public boolean isStale() {
		Period elapsed = new Period(dateTime, new DateTime());
		return elapsed.getMinutes() > 15;
	}

	@XmlElement
	public String getCurrencyValue() {
		return NumberFormat.getCurrencyInstance().format(value);
	}

	@XmlElement
	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Balance [value=" + value + ", stale=" + isStale() + "]";
	}

}