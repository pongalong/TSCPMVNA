package com.tscp.mvna.account.kenan;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.provision.Service;

@MappedSuperclass
public class KenanAccount extends KenanObject {
	private static final long serialVersionUID = -4599362937702458223L;
	protected static final Logger logger = LoggerFactory.getLogger(KenanAccount.class);
	protected int accountNo;
	protected Balance balance;
	protected Contact contact;
	protected Service service;

	/* **************************************************
	 * Constructors
	 */

	protected KenanAccount() {
		// prevent instantiation
	}

	public KenanAccount(int accountNo) {
		this.accountNo = accountNo;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	public void reset() {
		super.reset();
		balance = null;
		contact = null;
		service = null;
	}

	@Override
	@Transient
	public boolean isLoaded() {
		boolean nullCheck = contact != null && balance != null && service != null;
		boolean loadedCheck = contact.isLoaded() && balance.isLoaded() && service.isLoaded();
		return nullCheck && loadedCheck;
	}

	@Override
	public void load() {
		if (balance != null)
			balance.load();
		if (contact != null)
			contact.load();
		if (service != null)
			service.load();
	}

	@Override
	protected Object loadValue() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not have any values to load");
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
			balance.load();
		else if (balance.isStale())
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
			contact.load();
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
			service.load();
		return service;
	}

	public void setService(
			Service service) {
		this.service = service;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		String contactEmail = contact != null ? contact.getEmail() : null;
		String toString = "KenanAccount [accountNo=" + accountNo;
		if (contactEmail != null)
			toString += ", contact=" + contactEmail;
		toString += "]";
		return toString;
	}
}