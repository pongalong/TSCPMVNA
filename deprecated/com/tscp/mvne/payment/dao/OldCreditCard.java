package com.tscp.mvne.payment.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.Query;
import org.hibernate.Session;

import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.mvne.payment.OldPaymentInformation;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.PaymentType;

@Deprecated
@SuppressWarnings("unchecked")
public class OldCreditCard extends OldPaymentInformation implements Serializable {
	public static final String CREDITCARD_AMEX = "3";
	public static final String CREDITCARD_VISA = "4";
	public static final String CREDITCARD_MASTERCARD = "5";
	public static final String CREDITCARD_DISCOVER = "6";

	private static final long serialVersionUID = 1L;

	private PaymentType paymentType = PaymentType.CreditCard;

	private String nameOnCreditCard;
	private String creditCardNumber;
	private String expirationDate;
	private String verificationcode;

	public OldCreditCard() {
		paymentType = PaymentType.CreditCard;
	}

	@Override
	public PaymentUnitResponse submitPayment(
			PaymentTransaction transaction) throws PaymentException {
		PaymentUnitResponse retValue = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("sbt_pmt_cc_info");
		q.setParameter("in_cardno", getCreditCardNumber());
		q.setParameter("in_cardexpdt", getExpirationDate());
		q.setParameter("in_seccode", getVerificationcode());
		q.setParameter("in_pymntamt", transaction.getPaymentAmount());
		q.setParameter("in_zip", getZip());
		q.setParameter("in_cardholder", getNameOnCreditCard());
		q.setParameter("in_street", getAddress1());

		List<PaymentUnitResponse> responseList = q.list();
		for (PaymentUnitResponse response : responseList) {
			retValue = response;
		}

		session.getTransaction().commit();
		return retValue;
	}

	@Override
	public void save() throws PaymentException {
		// CreditCardManager.updateCreditCard(this);
	}

	@Override
	public void update() throws PaymentException {
		// CreditCardManager.updateCreditCard(this);
	}

	@Override
	public void delete() throws PaymentException {
		// CreditCardManager.deleteCreditCard(this);
	}

	@Override
	public boolean validate() throws PaymentException {
		if (nameOnCreditCard == null || nameOnCreditCard.trim().length() == 0) {
			throw new PaymentException("validate", "Name cannot be null on credit card payment information");
		}
		if (creditCardNumber == null || creditCardNumber.trim().length() == 0) {
			throw new PaymentException("validate", "Credit Card Number invalid");
		} else if (creditCardNumber.trim().length() > 16 || creditCardNumber.trim().length() < 15) {
			throw new PaymentException("validate", "Credit Card Number invalid");
		}
		if (expirationDate == null || expirationDate.trim().length() == 0) {
			throw new PaymentException("validate", "Expiration date must be provided");
		}
		if (verificationcode == null || verificationcode.trim().length() == 0) {
			throw new PaymentException("validate", "Verification code must be populated.");
		}
		if (getZip() == null || getZip().trim().length() <= 0) {
			throw new PaymentException("validate", "Billing Zip code must be populated");
		}
		return true;
	}

	/* **********************************************************
	 * Getters and Setters
	 */

	public String getNameOnCreditCard() {
		return nameOnCreditCard;
	}

	public void setNameOnCreditCard(
			String nameOnCreditCard) {
		this.nameOnCreditCard = nameOnCreditCard;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(
			String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(
			String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getVerificationcode() {
		return verificationcode;
	}

	public void setVerificationcode(
			String verificationcode) {
		this.verificationcode = verificationcode;
	}

	public void setPaymentType(
			PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	@Override
	@Enumerated(EnumType.STRING)
	public PaymentType getPaymentType() {
		return paymentType;
	}

	/* **********************************************************
	 * Helper Methods
	 */

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CreditCard Object...");
		sb.append(" \n");
		sb.append("Alias            :: " + getAlias());
		sb.append(" \n");
		sb.append("PaymentId        :: " + getPaymentId());
		sb.append(" \n");
		sb.append("PaymentType      :: " + getPaymentType());
		sb.append(" \n");
		sb.append("Address1         :: " + getAddress1());
		sb.append(" \n");
		sb.append("Address2         :: " + getAddress2());
		sb.append(" \n");
		sb.append("City             :: " + getCity());
		sb.append(" \n");
		sb.append("State            :: " + getState());
		sb.append(" \n");
		sb.append("Zip              :: " + getZip());
		sb.append(" \n");
		sb.append("IsDefault        :: " + isDefaultPayment());
		sb.append(" \n");
		sb.append("NameOnCreditCard :: " + getNameOnCreditCard());
		sb.append(" \n");
		if (getCreditCardNumber() != null && getCreditCardNumber().length() >= 4) {
			sb.append("CreditCardNumber :: " + getCreditCardNumber().substring(getCreditCardNumber().length() - 4, getCreditCardNumber().length()));
			sb.append(" \n");
		}
		sb.append("ExpirationDate   :: " + getExpirationDate());
		sb.append(" \n");
		sb.append("VerificationCode :: " + getVerificationcode());
		return sb.toString();
	}

	@Override
	public boolean equals(
			Object obj) {
		if (obj instanceof OldCreditCard) {
			OldCreditCard tempCreditCard = (OldCreditCard) obj;
			if (tempCreditCard.getCreditCardNumber().equals(getCreditCardNumber()) && tempCreditCard.getExpirationDate().equals(getExpirationDate()) && tempCreditCard.getAddress1().equals(getAddress1()) && tempCreditCard.getAddress2().equals(getAddress2()) && tempCreditCard.getAlias().equals(getAlias()) && tempCreditCard.getCity().equals(getCity()) && tempCreditCard.getNameOnCreditCard().equals(getNameOnCreditCard()) && tempCreditCard.getPaymentId() == getPaymentId()
					&& tempCreditCard.getPaymentType().equals(getPaymentType()) && tempCreditCard.getState().equals(getState()) && tempCreditCard.getVerificationcode().equals(getVerificationcode()) && tempCreditCard.getZip().equals(getZip())) {
				return true;
			}
		}
		return super.equals(obj);
	}

}
