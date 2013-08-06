package com.tscp.mvna.account.payment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.tscp.mvna.account.Account;
import com.tscp.mvna.payment.method.CreditCard;

@Entity
@Table(name = "PMT_REQUEST")
public class PaymentRequest implements Serializable {
	private static final long serialVersionUID = 2365845683459763290L;
	private int id;
	private DateTime paymentDate = new DateTime();
	private Account account;
	private CreditCard creditCard;
	private String sessionId;
	private Double amount;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PMT_TRANS_ID_GEN")
	@SequenceGenerator(name = "PMT_TRANS_ID_GEN", sequenceName = "PMT_TRANS_TRANS_ID_SEQ")
	public int getId() {
		return id;
	}

	public void setId(
			int id) {
		this.id = id;
	}

	@Column(name = "pmt_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	public DateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(
			DateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_no", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(
			Account account) {
		this.account = account;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pmt_id", nullable = false)
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(
			CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	@Column(name = "session_id")
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(
			String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(
			Double amount) {
		this.amount = amount;
	}

}