package com.tscp.mvna.payment.method;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.Type;

import com.tscp.mvna.user.Customer;

@Entity
@Table(name = "PMT_CREDITCARD")
@XmlRootElement
public class CreditCard implements Serializable {
	private static final long serialVersionUID = 6895450785564810295L;
	private int id;
	private String cardNumber;
	private String csc;
	private String expirationDate;
	private PaymentInfo paymentInfo;
	private Customer customer;

	private boolean enabled;

	@Column(name = "enabled")
	@Type(type = "yes_no")
	@XmlAttribute
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(
			boolean enabled) {
		this.enabled = enabled;
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@Id
	@Column(name = "pmt_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PMT_ID_GEN")
	@SequenceGenerator(name = "PMT_ID_GEN", sequenceName = "PMT_INFO_PMT_ID_SEQ")
	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(
			int id) {
		this.id = id;
	}

	@Column(name = "creditcard_no")
	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(
			String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@Column(name = "sec_code")
	public String getCsc() {
		return csc;
	}

	public void setCsc(
			String csc) {
		this.csc = csc;
	}

	@Column(name = "exp_dt")
	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(
			String expirationDate) {
		this.expirationDate = expirationDate;
	}

	@OneToOne(mappedBy = "creditCard", cascade = CascadeType.ALL)
	@XmlElement
	@XmlInverseReference(mappedBy = "creditCard")
	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(
			PaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
		paymentInfo.setCreditCard(this);
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "PMT_MAP", joinColumns = @JoinColumn(name = "PMT_ID"), inverseJoinColumns = @JoinColumn(name = "CUST_ID"))
	@XmlTransient
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(
			Customer customer) {
		this.customer = customer;
	}

	@Transient
	@XmlAttribute
	public int getCustomerId() {
		return customer.getId();
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "CreditCard [id=" + id + ", cardNumber=" + cardNumber + ", expirationDate=" + expirationDate + "]";
	}

}