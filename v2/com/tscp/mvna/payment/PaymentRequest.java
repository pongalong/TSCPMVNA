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
import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.payment.method.CreditCard;
import com.tscp.mvna.user.User;
import com.tscp.mvna.user.UserEntity;

@Entity
@Table(name = "PMT_REQUEST")
public class PaymentRequest implements Serializable {
	private static final long serialVersionUID = 2365845683459763290L;
	private int transactionId;
	private DateTime dateTime = new DateTime();
	private DeviceAndService device;
	private int accountNo;
	private CreditCard creditCard;
	private Money amount;
	private UserEntity requestBy;
	private PaymentResponse paymentResponse;

	public PaymentRequest() {
		// do nothing
	}

	public PaymentRequest(DeviceAndService device) {
		this.device = device;
		accountNo = device.getAccount().getAccountNo();
		creditCard = device.getPaymentMethod();
		amount = device.getTopup().getValue();
		requestBy = device.getOwner();
	}

	@Id
	@Column(name = "TRANS_ID")
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

	// TODO device already contains an account object. this object needs to be rethought
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id")
	public DeviceAndService getDevice() {
		return device;
	}

	public void setDevice(
			DeviceAndService device) {
		this.device = device;
	}

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "account_no", nullable = false)
	@Column(name = "account_no")
	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(
			int accountNo) {
		this.accountNo = accountNo;
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
		return "PaymentRequest [transactionId=" + transactionId + ", dateTime=" + dateTime + ", account=" + accountNo + ", creditCard=" + creditCard + ", amount=" + amount + "]";
	}

}