package com.tscp.mvna.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {
		"firstName",
		"middleName",
		"lastName",
		"email",
		"phoneNumber",
		"address" })
public class Contact implements Serializable {
	private static final long serialVersionUID = -2825303260404211128L;
	private String firstName;
	private String middleName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private Address address;

	/* **************************************************
	 * Getter/Setter Methods
	 */

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(
			String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(
			String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(
			String lastName) {
		this.lastName = lastName;
	}

	@XmlAttribute
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(
			String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@XmlAttribute
	public String getEmail() {
		return email;
	}

	public void setEmail(
			String email) {
		this.email = email;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(
			Address address) {
		this.address = address;
	}

	/* **************************************************
	 * Helper Methods
	 */

	public boolean hasFirstName() {
		return firstName != null && !firstName.isEmpty();
	}

	public boolean hasMiddleName() {
		return middleName != null && !middleName.isEmpty();
	}

	public boolean hasLastName() {
		return lastName != null && !lastName.isEmpty();
	}
}
