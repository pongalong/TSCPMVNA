package com.tscp.mvna.payment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.tscp.jaxb.xml.adapter.DateTimeAdapter;

@Entity
@Table(name = "PMT_RECORD")
public class PaymentRecord implements Serializable {
	private static final long serialVersionUID = -6631843556939094878L;
	private int transactionId;
	private DateTime recordDate;
	private int trackingId;

	@Id
	@Column(name = "trans_id")
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

}