package com.tscp.mvna.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.tscp.jaxb.xml.adapter.DateTimeAdapter;

@Entity
@XmlRootElement
public class UnlinkedAccount implements Serializable {
	private static final long serialVersionUID = 8546335168318675912L;
	private int accountNo;
	private int custId;
	private String email;
	private int paymentTransId;
	private DateTime paymentTransDate;

	@Id
	@Column(name = "account_no")
	@XmlAttribute
	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(
			int accountNo) {
		this.accountNo = accountNo;
	}

	@Column(name = "cust_id")
	@XmlAttribute
	public int getCustId() {
		return custId;
	}

	public void setCustId(
			int custId) {
		this.custId = custId;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(
			String email) {
		this.email = email;
	}

	@Column(name = "trans_id")
	public int getPaymentTransId() {
		return paymentTransId;
	}

	public void setPaymentTransId(
			int paymentTransId) {
		this.paymentTransId = paymentTransId;
	}

	@Column(name = "pmt_trans_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.DATE)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getPaymentTransDate() {
		return paymentTransDate;
	}

	public void setPaymentTransDate(
			DateTime paymentTransDate) {
		this.paymentTransDate = paymentTransDate;
	}

	@Override
	public String toString() {
		return "UnlinkedAccount [accountNo=" + accountNo + ", custId=" + custId + ", email=" + email + ", paymentTransId=" + paymentTransId + ", paymentTransDate=" + paymentTransDate + "]";
	}

}