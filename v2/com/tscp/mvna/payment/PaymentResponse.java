package com.tscp.mvna.payment;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import com.tscp.mvna.payment.service.PaymentGatewayResponseEntity;

@Entity
@Table(name = "PMT_RESPONSE")
@XmlRootElement
public class PaymentResponse extends PaymentGatewayResponseEntity {
	protected static final Logger logger = LoggerFactory.getLogger(PaymentResponse.class);
	private static final long serialVersionUID = -7797401339183331832L;
	protected PaymentRequest paymentRequest;
	protected int transactionId;
	protected DateTime responseDate = new DateTime();

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
		return confirmationCode != null && confirmationCode.equalsIgnoreCase(SUCCESSFUL_TRANSACTION);
	}

	protected void setSuccess(
			boolean success) {
		// do nothing. Success is based on the confirmation code.
	}

	/* **************************************************
	 * Adaptor Methods for PaymentGateway Objects
	 */

	public void parseGatewayResponse(
			PaymentGatewayResponseEntity response) {

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
		return "PaymentResponse [transactionId=" + transactionId + ", responseDate=" + responseDate + ", gatewayTransactionId=" + gatewayTransactionId + ", success=" + isSuccess() + "]";
	}

}