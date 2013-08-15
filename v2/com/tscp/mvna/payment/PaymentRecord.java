package com.tscp.mvna.payment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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

@Entity
@Table(name = "PMT_RECORD")
@Embeddable
@XmlRootElement
public class PaymentRecord implements Serializable {
	private static final long serialVersionUID = -6631843556939094878L;
	protected static final Logger logger = LoggerFactory.getLogger(PaymentRecord.class);

	protected int transactionId;
	protected int trackingId;
	protected DateTime recordDate = new DateTime();

	/* **************************************************
	 * Constructors
	 */

	public PaymentRecord() {
		// do nothing
	}

	// public PaymentRecord(PaymentResponse paymentResponse) {
	// this.paymentResponse = paymentResponse;
	// }

	/* **************************************************
	 * Getters and Setters
	 */

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "paymentResponse"))
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

	@Column(name = "record_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(
			DateTime recordDate) {
		this.recordDate = recordDate;
	}

	@Column(name = "tracking_id")
	public int getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(
			int trackingId) {
		this.trackingId = trackingId;
	}

	// protected PaymentResponse paymentResponse;
	//
	// @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	// @PrimaryKeyJoinColumn
	// @XmlTransient
	// protected PaymentResponse getPaymentResponse() {
	// return paymentResponse;
	// }
	//
	// protected void setPaymentResponse(
	// PaymentResponse paymentResponse) {
	// this.paymentResponse = paymentResponse;
	// }

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
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "PaymentRecord [transactionId=" + transactionId + ", trackingId=" + trackingId + "]";
	}

}