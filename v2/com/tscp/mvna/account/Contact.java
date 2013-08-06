package com.tscp.mvna.account;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.KenanObject;
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
public class Contact implements KenanObject {
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

	protected boolean loaded;

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void refresh() {
		Contact tmp;
		try {
			tmp = AccountService.getContact(accountNo);
			if (tmp != null) {
				firstName = tmp.getFirstName();
				middleName = tmp.getMiddleName();
				lastName = tmp.getLastName();
				phoneNumber = tmp.getPhoneNumber();
				email = tmp.getEmail();
				address = AccountService.getAddress(accountNo);
			}
		} catch (ContactFetchException e) {
			logger.error("Unable to fetch Contact for Account {}", accountNo);
		} finally {
			loaded = true;
			tmp = null;
		}
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
