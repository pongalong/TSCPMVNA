package com.tscp.mvna.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.HibernateException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.device.Device;
import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.method.CreditCard;

@Entity
@Table(name = "CUSTOMER")
@XmlRootElement
public class Customer extends UserEntity {
	private static final long serialVersionUID = 8912663095612683977L;
	protected static final Logger logger = LoggerFactory.getLogger(Customer.class);
	protected Set<CreditCard> creditCards;
	protected Set<Account> accounts;
	protected Set<DeviceAndService> devices;

	/* **************************************************
	 * Helper Methods
	 */

	@Transient
	public CreditCard getCreditCard(
			int id) {
		for (CreditCard cc : creditCards)
			if (cc.getId() == id)
				return cc;
		return null;
	}

	@Transient
	public Device getDevice(
			int id) {
		for (Device dev : getDevices())
			if (dev.getId() == id) {
				return dev;
			}
		return null;
	}

	@Transient
	public Account getAccount(
			int accountNo) {
		for (Account acc : accounts)
			if (acc.getAccountNo() == accountNo)
				return acc;
		return null;
	}

	@Transient
	public void addCreditCard(
			CreditCard creditCard) {

		try {
			creditCard.setCustomer(this);
			creditCard.setEnabled(true);
			creditCards.add(creditCard);
			Dao.saveOrUpdate(creditCard);
		} catch (HibernateException e) {
			creditCard.setEnabled(false);
		}
	}

	@Transient
	public void removeCreditCard(
			CreditCard creditCard) {

		boolean originalValue = creditCard.isEnabled();

		try {
			creditCard.setEnabled(false);
			Dao.update(creditCard);
		} catch (HibernateException e) {
			creditCard.setEnabled(originalValue);
		}
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "PMT_MAP", joinColumns = @JoinColumn(name = "CUST_ID"), inverseJoinColumns = @JoinColumn(name = "PMT_ID"))
	@LazyCollection(LazyCollectionOption.FALSE)
	@XmlElement
	public Set<CreditCard> getCreditCards() {
		return creditCards;
	}

	public void setCreditCards(
			Set<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "ACCOUNT_MAP", joinColumns = @JoinColumn(name = "CUST_ID"), inverseJoinColumns = @JoinColumn(name = "ACCOUNT_NO"))
	@LazyCollection(LazyCollectionOption.FALSE)
	@XmlElement
	@XmlInverseReference(mappedBy = "customer")
	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(
			Set<Account> accounts) {
		this.accounts = accounts;
	}

	@Transient
	@XmlElement
	public Set<DeviceAndService> getDevices() {
		if (devices != null)
			return devices;
		if (accounts == null)
			return null;
		devices = new HashSet<DeviceAndService>();
		for (Account acc : getAccounts())
			for (DeviceAndService dev : acc.getDevices())
				devices.add(dev);
		return devices;
	}

	public void setDevices(
			Set<DeviceAndService> devices) {
		this.devices = devices;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Customer [id=" + getId() + "]";
	}

}