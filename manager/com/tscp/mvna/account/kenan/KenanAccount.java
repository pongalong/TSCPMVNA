package com.tscp.mvna.account.kenan;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.Balance;
import com.tscp.mvna.account.Contact;
import com.tscp.mvna.account.kenan.exception.BalanceFetchException;
import com.tscp.mvna.account.kenan.exception.ContactFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;

@MappedSuperclass
public class KenanAccount {
	private static final Logger logger = LoggerFactory.getLogger(KenanAccount.class);
	protected int accountNo;
	protected Balance balance;
	protected Contact contact;
	protected Service service;

	public void refresh() {
		balance = null;
		contact = null;
		service = null;
		loadedContact = false;
		loadedBalance = false;
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
	@XmlTransient
	public void setAccountNo(
			String accountNo) throws NumberFormatException {
		if (accountNo != null && accountNo.matches("\\d+"))
			this.accountNo = Integer.parseInt(accountNo.trim());
	}

	@Transient
	@XmlElement
	public Balance getBalance() {
		if (balance == null || balance.isStale())
			balance = loadBalance();
		return balance;
	}

	protected void setBalance(
			Balance balance) {
		this.balance = balance;
	}

	@Transient
	@XmlElement
	public Contact getContact() {
		if (contact == null && !loadedContact)
			loadContact();
		return contact;
	}

	public void setContact(
			Contact contact) {
		this.contact = contact;
	}

	@Transient
	@XmlElement
	public Service getService() {
		if (service == null && !loadedService) {
			service = new Service();
			service.setAccount(this);
			service.refresh();
			loadedService = true;
		}
		return service;
	}

	public void setService(
			Service service) {
		this.service = service;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	protected boolean loadedContact;
	protected boolean loadedBalance;
	protected boolean loadedService;

	protected Balance loadBalance() {
		try {
			return AccountService.getBalance(accountNo);
		} catch (BalanceFetchException e) {
			logger.error("Unable to fetch Balance for {}", this);
			return null;
		} finally {
			loadedBalance = true;
		}
	}

	protected Contact loadContact() {
		try {
			contact = AccountService.getContact(accountNo);
			if (contact != null)
				contact.setAddress(AccountService.getAddress(accountNo));
			return contact;
		} catch (ContactFetchException e) {
			logger.error("Unable to fetch Contact for {}", this);
			return null;
		} finally {
			loadedContact = true;
		}
	}

}