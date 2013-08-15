package com.tscp.mvna.payment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.money.Money;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.Payment;
import com.tscp.mvna.payment.service.PaymentService;

@Entity
@Table(name = "PMT_TRANS_MAP")
@XmlRootElement
public class PaymentTransaction implements Serializable {
	protected static final Logger logger = LoggerFactory.getLogger(PaymentTransaction.class);
	private static final long serialVersionUID = 23273165232351086L;
	private int transactionId;
	private PaymentRequest paymentRequest;
	private PaymentResponse paymentResponse;
	private PaymentRecord paymentRecord;

	public PaymentTransaction() {
		// do nothing
	}

	public PaymentTransaction(Payment payment) {
		paymentRequest = new PaymentRequest();
		paymentRequest.setAmount(Money.of(PaymentService.CURRENCY, payment.getTransAmount()));

		paymentRecord = new PaymentRecord();
		paymentRecord.setRecordDate(new DateTime(payment.getTransDate().toGregorianCalendar()));
		paymentRecord.setTrackingId(payment.getTrackingId());

		logger.debug("typeDesc {}", payment.getPaymentTypeDesc());
		logger.debug("transType {}", payment.getTransType());
	}

	@Id
	@Column(name = "trans_id")
	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(
			int transactionId) {
		this.transactionId = transactionId;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	public PaymentRequest getPaymentRequest() {
		return paymentRequest;
	}

	public void setPaymentRequest(
			PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	public PaymentResponse getPaymentResponse() {
		return paymentResponse;
	}

	public void setPaymentResponse(
			PaymentResponse paymentResponse) {
		this.paymentResponse = paymentResponse;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	public PaymentRecord getPaymentRecord() {
		return paymentRecord;
	}

	public void setPaymentRecord(
			PaymentRecord paymentRecord) {
		this.paymentRecord = paymentRecord;
	}

	@Override
	public String toString() {
		return "PaymentTransaction [transactionId=" + transactionId + ", request=" + paymentRequest + ", response=" + paymentResponse + ", record=" + paymentRecord + "]";
	}

}