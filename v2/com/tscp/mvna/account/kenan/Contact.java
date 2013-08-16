package com.tscp.mvna.account.kenan;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.exception.ContactFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;

@XmlRootElement
@XmlType(propOrder = {
		"firstName",
		"middleName",
		"lastName",
		"email",
		"phoneNumber",
		"address" })
public class Contact extends KenanObject {
	private static final long serialVersionUID = 5338029567070428920L;
	protected static final Logger logger = LoggerFactory.getLogger(Contact.class);
	protected int accountNo;
	protected String firstName;
	protected String middleName;
	protected String lastName;
	protected String phoneNumber;
	protected String email;
	protected Address address;

	/* **************************************************
	 * Constructors
	 */

	protected Contact() {
		// do nothing
	}

	public Contact(int accountNo) {
		this.accountNo = accountNo;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	public void reset() {
		super.reset();
		firstName = null;
		middleName = null;
		lastName = null;
		phoneNumber = null;
		email = null;
		address = null;
	}

	@Override
	public void load() {
		Contact temp = loadValue();
		firstName = temp.getFirstName();
		middleName = temp.getMiddleName();
		lastName = temp.getLastName();
		phoneNumber = temp.getPhoneNumber();
		email = temp.getEmail();
		address = temp.getAddress();
	}

	@Override
	protected Contact loadValue() {
		try {
			return AccountService.getContact(accountNo);
		} catch (ContactFetchException e) {
			logger.error("Error loading Contact for Account {}", accountNo);
		} finally {
			loaded = true;
		}
		return null;
	}

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
