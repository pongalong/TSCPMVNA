package com.tscp.mvna.account.device;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.network.NetworkStatus;
import com.tscp.mvna.account.device.network.exception.ConnectException;
import com.tscp.mvna.account.device.network.exception.DisconnectException;
import com.tscp.mvna.account.device.network.exception.ReserveException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvna.account.exception.AccountRestoreException;
import com.tscp.mvna.account.exception.AccountSuspendException;
import com.tscp.mvna.account.kenan.Service;
import com.tscp.mvna.account.kenan.exception.AccountDisconnectException;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;

@Entity
@Table(name = "DEVICE")
public class DeviceAndService extends Device implements Serializable {
	protected static final Logger logger = LoggerFactory.getLogger(DeviceAndService.class);
	private static final long serialVersionUID = -901253807496034420L;
	private DeviceIntegrity integrity;

	/* **************************************************
	 * Status Methods
	 */

	@Override
	@Transient
	@XmlTransient
	public void connect() throws ConnectException {

		if (getNetworkInfo() != null && !getNetworkInfo().isConnectable())
			throw new ConnectException("NetworkInfo is not in a connectable state");
		try {
			super.reserve();
		} catch (ReserveException e) {
			throw new ConnectException(e);
		}

		if (getAccount() == null)
			throw new ConnectException("Account is null and cannot be connected");

		try {
			getAccount().connect(new ServiceInstance(getAccount(), getNetworkInfo()));
		} catch (Exception e) {
			try {
				super.disconnect();
			} catch (DisconnectException e1) {
				throw new ConnectException(e);
			}
			throw new ConnectException(e);
		}

		super.connect();
	}

	@Override
	@Transient
	@XmlTransient
	public void disconnect() throws DisconnectException {
		// attempt to disconnect from the network
		if (getNetworkInfo() == null)
			throw new DisconnectException("NetworkInfo is null and cannot be disconnected");
		if (getStatus() == NetworkStatus.C || getStatus() == NetworkStatus.U)
			throw new DisconnectException("NetworkInfo is not in an ACTIVE state and cannot be disconnected");

		super.disconnect();

		// attempt to disconnect the account
		if (getAccount() == null)
			throw new DisconnectException("Account is null and cannot be suspended");
		try {
			getAccount().disconnect();
		} catch (AccountDisconnectException e) {
			throw new DisconnectException(e);
		}

	}

	@Override
	@Transient
	@XmlTransient
	public void suspend() throws SuspendException {
		// attempt to suspend the network
		if (getNetworkInfo() == null)
			throw new SuspendException("NetworkInfo is null and cannot be suspended");
		if (getStatus() == NetworkStatus.A)
			super.suspend();

		// attempt to suspend the account
		if (getAccount() == null)
			throw new SuspendException("Account is null and cannot be suspended");
		try {
			getAccount().suspend();
		} catch (AccountSuspendException e) {
			throw new SuspendException(e);
		}
	}

	@Override
	@Transient
	@XmlTransient
	public void restore() throws RestoreException {
		// attempt to restore the account
		if (getAccount() == null)
			throw new RestoreException("Account is null and device cannot be restored");
		try {
			getAccount().restore();
		} catch (AccountRestoreException e) {
			throw new RestoreException(e);
		}

		// attempt to restore the network
		if (getNetworkInfo() == null)
			throw new RestoreException("NetworkInfo is null and cannot be restored");
		if (getStatus() == NetworkStatus.S)
			super.restore();

	}

	/* **************************************************
	 * Integrity Methods
	 */

	@Transient
	@XmlElement
	public DeviceIntegrity getIntegrity() {
		if (integrity == null) {
			integrity = new DeviceIntegrity();
			integrity.sanityCheck(this);
		}
		return integrity;
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@Transient
	@XmlElement
	public Service getService() {
		return getAccount().getService();
	}

}