package com.tscp.mvna.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.telscape.billingserviceinterface.CustAddress;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultCustAddress;

@XmlRootElement
@XmlType(propOrder = {
		"address1",
		"address2",
		"address3",
		"city",
		"state",
		"zip" })
public class Address implements Serializable {
	private static final long serialVersionUID = 8693917969138968240L;
	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String state;
	private String zip;

	public Address() {
		// do nothing
	}

	public Address(CustAddress custAddress) {
		address1 = custAddress.getAddress1();
		address2 = custAddress.getAddress2();
		address3 = custAddress.getAddress3();
		city = custAddress.getCity();
		state = custAddress.getState();
		zip = custAddress.getZip();
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(
			String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(
			String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(
			String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(
			String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(
			String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(
			String zip) {
		this.zip = zip;
	}

	public CustAddress toCustAddress() {
		return new DefaultCustAddress(this);
	}

}
