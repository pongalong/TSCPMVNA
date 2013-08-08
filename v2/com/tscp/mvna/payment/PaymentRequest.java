package com.tscp.mvna.payment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;
import org.joda.money.Money;
import org.joda.time.DateTime;

import com.tscp.jaxb.xml.adapter.DateTimeAdapter;
import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.device.Device;
import com.tscp.mvna.payment.method.CreditCard;
import com.tscp.mvna.user.User;
import com.tscp.mvna.user.UserEntity;

@Entity
@Table(name = "PMT_REQUEST")
public class PaymentRequest implements Serializable {
	private static final long serialVersionUID = 2365845683459763290L;
	private int transactionId;
	private DateTime dateTime = new DateTime();
	private Account account;
	private CreditCard creditCard;
	private Money amount;
	private UserEntity requestBy;
	private PaymentResponse paymentResponse;

	public PaymentRequest() {
		// do nothing
	}

	public PaymentRequest(Device device) {
		account = device.getAccount();
		creditCard = device.getPaymentMethod();
		amount = device.getTopup().getValue();
		requestBy = device.getOwner();
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PMT_TRANS_ID_GEN")
	@SequenceGenerator(name = "PMT_TRANS_ID_GEN", sequenceName = "PMT_TRANS_TRANS_ID_SEQ")
	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(
			int transactionId) {
		this.transactionId = transactionId;
	}

	@Column(name = "request_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(
			DateTime dateTime) {
		this.dateTime = dateTime;
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

	@Column(name = "amount")
	@Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount")
	public Money getAmount() {
		return amount;
	}

	public void setAmount(
			Money amount) {
		this.amount = amount;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
	@JoinColumn(name = "REQUESTER_ID", nullable = false)
	public UserEntity getRequestBy() {
		return requestBy;
	}

	public void setRequestBy(
			UserEntity requestBy) {
		this.requestBy = requestBy;
	}

	@OneToOne(mappedBy = "paymentRequest")
	protected PaymentResponse getPaymentResponse() {
		return paymentResponse;
	}

	protected void setPaymentResponse(
			PaymentResponse paymentResponse) {
		this.paymentResponse = paymentResponse;
	}

	@Override
	public String toString() {
		return "PaymentRequest [transactionId=" + transactionId + ", dateTime=" + dateTime + ", account=" + account + ", creditCard=" + creditCard + ", amount=" + amount + "]";
	}

}