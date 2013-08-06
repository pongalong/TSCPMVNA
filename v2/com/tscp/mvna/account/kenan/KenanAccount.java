package com.tscp.mvna.account.kenan;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.Balance;
import com.tscp.mvna.account.Contact;

@MappedSuperclass
public class KenanAccount implements KenanObject {
	protected static final Logger logger = LoggerFactory.getLogger(KenanAccount.class);
	protected int accountNo;
	protected Balance balance;
	protected Contact contact;
	protected Service service;

	@Override
	@Transient
	public boolean isLoaded() {
		boolean nullCheck = contact != null && balance != null;
		boolean loadedCheck = contact.isLoaded() && balance.isLoaded() && service.isLoaded();
		return nullCheck && loadedCheck;
	}

	@Override
	public void refresh() {
		balance = null;
		contact = null;
		service = null;
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@Id
	@Column(name = "ACCOUNT_NO")
	@XmlAttribute
	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(
			int accountNo) {
		this.accountNo = accountNo;
	}

	@Transient
	@XmlElement
	public Balance getBalance() {
		if (balance == null)
			balance = new Balance(accountNo);
		if (!balance.isLoaded())
			balance.refresh();
		else if (balance.isLoaded() && balance.isStale())
			balance.refresh();
		return balance;
	}

	protected void setBalance(
			Balance balance) {
		this.balance = balance;
	}

	@Transient
	@XmlElement
	public Contact getContact() {
		if (contact == null)
			contact = new Contact(accountNo);
		if (!contact.isLoaded())
			contact.refresh();
		return contact;
	}

	public void setContact(
			Contact contact) {
		this.contact = contact;
	}

	@Transient
	@XmlElement
	public Service getService() {
		if (service == null)
			service = new Service(this);
		if (!service.isLoaded())
			service.refresh();
		return service;
	}

	public void setService(
			Service service) {
		this.service = service;
	}

}