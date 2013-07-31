package com.tscp.mvna.payment.method;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "pmt_info")
@XmlRootElement
public class PaymentInfo implements Serializable {
	private static final long serialVersionUID = 4779565016596058607L;
	private int id;
	private String name;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private CreditCard creditCard;

	/* **************************************************
	 * Getters and Setters
	 */

	@Id
	@Column(name = "pmt_id")
	@GeneratedValue(generator = "gen")
	@GenericGenerator(name = "gen", strategy = "foreign", parameters = @Parameter(name = "property", value = "creditCard"))
	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(
			int id) {
		this.id = id;
	}

	@Column(name = "cust_name")
	public String getName() {
		return name;
	}

	public void setName(
			String name) {
		this.name = name;
	}

	@Column(name = "bill_addr1")
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(
			String address1) {
		this.address1 = address1;
	}

	@Column(name = "bill_addr2")
	public String getAddress2() {
		return address2;
	}

	public void setAddress2(
			String address2) {
		this.address2 = address2;
	}

	@Column(name = "bill_city")
	public String getCity() {
		return city;
	}

	public void setCity(
			String city) {
		this.city = city;
	}

	@Column(name = "bill_state")
	public String getState() {
		return state;
	}

	public void setState(
			String state) {
		this.state = state;
	}

	@Column(name = "bill_zip")
	public String getZip() {
		return zip;
	}

	public void setZip(
			String zip) {
		this.zip = zip;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	@XmlElement
	@XmlInverseReference(mappedBy = "paymentInfo")
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(
			CreditCard creditCard) {
		if (creditCard.getPaymentInfo() == null)
			creditCard.setPaymentInfo(this);
		this.creditCard = creditCard;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "NewPaymentInfo [name=" + name + ", address1=" + address1 + ", zip=" + zip + "]";
	}

}