package com.tscp.mvna.payment;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.jaxb.xml.adapter.DateTimeAdapter;
import com.tscp.mvna.payment.exception.PaymentGatewayException;
import com.tscp.mvna.payment.service.PaymentGatewayResponse;

@Entity
@Table(name = "PMT_RESPONSE")
@Embeddable
@XmlRootElement
public class PaymentResponse implements Serializable {
	private static final long serialVersionUID = -7797401339183331832L;
	protected static final Logger logger = LoggerFactory.getLogger(PaymentResponse.class);

	protected int transactionId;
	protected DateTime responseDate = new DateTime();
	protected PaymentRequest paymentRequest;
	protected PaymentRecord paymentRecord;
	protected boolean success;

	/* **************************************************
	 * Constructors
	 */

	public PaymentResponse() {
		// do nothing
	}

	public PaymentResponse(PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "paymentRequest"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "TRANS_ID", unique = true, nullable = false)
	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(
			int transactionId) {
		this.transactionId = transactionId;
	}

	@Column(name = "RESPONSE_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(
			DateTime responseDate) {
		this.responseDate = responseDate;
	}

	@Column(name = "SUCCESS")
	@Type(type = "yes_no")
	public boolean isSuccess() {
		return success || (confirmationCode != null && confirmationCode.equalsIgnoreCase(PaymentGatewayResponse.SUCCESSFUL_TRANSACTION));
	}

	protected void setSuccess(
			boolean success) {
		this.success = success;
	}

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@PrimaryKeyJoinColumn
	@XmlTransient
	protected PaymentRequest getPaymentRequest() {
		return paymentRequest;
	}

	protected void setPaymentRequest(
			PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

	@OneToOne(mappedBy = "paymentResponse")
	@XmlTransient
	protected PaymentRecord getPaymentRecord() {
		return paymentRecord;
	}

	protected void setPaymentRecord(
			PaymentRecord paymentRecord) {
		this.paymentRecord = paymentRecord;
	}

	protected PaymentTransaction paymentTransaction;

	@OneToOne(mappedBy = "paymentRequest")
	@XmlTransient
	public PaymentTransaction getPaymentTransaction() {
		return paymentTransaction;
	}

	public void setPaymentTransaction(
			PaymentTransaction paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
	}

	/* **************************************************
	 * Adaptor Methods for PaymentGateway Objects
	 */

	protected int gatewayTransactionId;
	protected String confirmationCode;
	protected String confirmationMsg;
	protected String authorizationCode;
	protected String cvvCode;

	@Column(name = "GATEWAY_TRANS_ID")
	public int getGatewayTransactionId() {
		return gatewayTransactionId;
	}

	public void setGatewayTransactionId(
			int gatewayTransactionId) {
		this.gatewayTransactionId = gatewayTransactionId;
	}

	@Column(name = "CONF_CODE")
	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(
			String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	@Column(name = "CONF_MSG")
	public String getConfirmationMsg() {
		return confirmationMsg;
	}

	public void setConfirmationMsg(
			String confirmationMsg) {
		this.confirmationMsg = confirmationMsg;
	}

	@Column(name = "AUTH_CODE")
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(
			String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	@Column(name = "CVV_CODE")
	public String getCvvCode() {
		return cvvCode;
	}

	public void setCvvCode(
			String cvvCode) {
		this.cvvCode = cvvCode;
	}

	public void parseGatewayResponse(
			PaymentGatewayResponse response) {

		if (paymentRequest == null)
			throw new PaymentGatewayException(this.getClass().getSimpleName() + " does not contain a PaymentRequest and is not a valid response");
		if (response == null)
			throw new PaymentGatewayException("Cannot parse null response");

		authorizationCode = response.getAuthorizationCode();
		confirmationCode = response.getConfirmationCode();
		confirmationMsg = response.getConfirmationMsg();
		cvvCode = response.getCvvCode();
		gatewayTransactionId = response.getGatewayTransactionId();
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "PaymentResponse [transactionId=" + transactionId + ", gatewayTransactionId=" + gatewayTransactionId + ", authCode=" + authorizationCode + ", success=" + isSuccess() + "]";
	}

}