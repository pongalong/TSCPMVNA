package com.tscp.mvna.account;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.exception.AccountRestoreException;
import com.tscp.mvna.account.exception.AccountSuspendException;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.exception.AccountConnectException;
import com.tscp.mvna.account.kenan.exception.AccountDisconnectException;
import com.tscp.mvna.account.kenan.exception.AccountUpdateException;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.service.AccountService;
import com.tscp.mvna.user.Customer;
import com.tscp.mvne.config.PROVISION;

@Entity
@Table(name = "ACCOUNT")
@XmlRootElement
public class Account extends KenanAccount implements Serializable {
	private static final long serialVersionUID = 39652240104589366L;
	protected static final Logger logger = LoggerFactory.getLogger(Account.class);
	protected Customer owner;
	protected List<DeviceAndService> devices;

	/* **************************************************
	 * Status Update Methods
	 */

	@Transient
	@XmlTransient
	public void connect(
			ServiceInstance serviceInstance) throws AccountConnectException {

		if (serviceInstance.getAccount() != this)
			serviceInstance.setAccount(this);

		try {
			getService().connect(serviceInstance);
		} catch (ProvisionException e) {
			throw new AccountConnectException(e);
		}
	}

	@Transient
	@XmlTransient
	public void disconnect() throws AccountDisconnectException {
		// TODO disconnect all contracts on the account
		// List<KenanContract> contracts = contractService.getContracts(account, serviceInstance);
		// if (contracts != null) {
		// for (KenanContract contract : contracts) {
		// contract.setAccount(account);
		// contract.setServiceInstance(serviceInstance);
		// contract.setDuration(0);
		// contractService.updateContract(contract);
		// }
		// }

		try {
			getService().disconnect();
		} catch (ProvisionException e) {
			throw new AccountDisconnectException(e);
		}
	}

	@Transient
	@XmlTransient
	public void restore() throws AccountRestoreException {
		try {
			getService().restore();
		} catch (ProvisionException e) {
			throw new AccountRestoreException(e);
		}

		try {
			AccountService.updateServiceInstanceStatus(getService().getActiveServiceInstance(), PROVISION.SERVICE.RESTORE);
		} catch (AccountUpdateException e) {
			logger.error("Unable to restore account: Could not update Threshold");
			logger.warn("{} does not have correct threshold {}", this, PROVISION.SERVICE.RESTORE);
			throw new AccountRestoreException(e);
		}

		logger.info("Restored {}", this);
	}

	@Transient
	@XmlTransient
	public void suspend() throws AccountSuspendException {
		try {
			getService().suspend();
		} catch (ProvisionException e) {
			throw new AccountSuspendException(e);
		}

		try {
			AccountService.updateServiceInstanceStatus(getService().getActiveServiceInstance(), PROVISION.SERVICE.HOTLINE);
		} catch (AccountUpdateException e) {
			logger.error("Unable to suspend account: Could not update Threshold");
			logger.warn("{} does not have correct threshold {}", this, PROVISION.SERVICE.HOTLINE);
			throw new AccountSuspendException(e);
		}

		logger.info("Suspended {}", this);
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "DEVICE_MAP", joinColumns = @JoinColumn(name = "ACCOUNT_NO"), inverseJoinColumns = @JoinColumn(name = "DEVICE_ID"))
	@LazyCollection(LazyCollectionOption.FALSE)
	@XmlElement
	@XmlInverseReference(mappedBy = "account")
	public List<DeviceAndService> getDevices() {
		return devices;
	}

	public void setDevices(
			List<DeviceAndService> devices) {
		this.devices = devices;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "ACCOUNT_MAP", joinColumns = @JoinColumn(name = "ACCOUNT_NO"), inverseJoinColumns = @JoinColumn(name = "CUST_ID"))
	@XmlTransient
	public Customer getOwner() {
		return owner;
	}

	public void setOwner(
			Customer owner) {
		this.owner = owner;
	}

	/* **************************************************
	 * XML Helper Methods
	 */

	@Transient
	@XmlAttribute
	public int getCustomerId() {
		if (getOwner() != null)
			return owner.getId();
		return 0;
	}

	/* **************************************************
	 * Helper Methods
	 */

	@Transient
	@XmlAttribute
	public boolean isEmpty() {
		return devices == null || devices.isEmpty();
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Account [accountNo=" + accountNo + ", customer=" + owner.getId() + "]";
	}

}