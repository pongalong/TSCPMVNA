package com.tscp.mvna.account.device.network;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.Device;
import com.tscp.mvna.account.device.network.exception.ConnectException;
import com.tscp.mvna.account.device.network.exception.DisconnectException;
import com.tscp.mvna.account.device.network.exception.NetworkQueryException;
import com.tscp.mvna.account.device.network.exception.ReserveException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvna.account.device.network.service.NetworkService;

@XmlSeeAlso({ Device.class })
public abstract class NetworkDevice {
	private static final Logger logger = LoggerFactory.getLogger(NetworkDevice.class);
	protected static final NetworkService networkService = new NetworkService();
	protected NetworkInfo networkInfo;
	protected boolean loadedNetworkInfo;

	/* **************************************************
	 * Network Query Methods
	 */

	@XmlElement
	public abstract NetworkInfo getNetworkInfo();

	public abstract void refreshNetwork();

	protected void refreshByESN(
			String esn) {
		try {
			networkInfo = networkService.getNetworkInfoByEsn(esn);
		} catch (NetworkQueryException e) {
			logger.error("Unable to fetch NetworkInfo for {}", this);
		} finally {
			loadedNetworkInfo = true;
		}
	}

	protected void refreshByMDN(
			String mdn) {
		try {
			networkInfo = networkService.getNetworkInfoByMdn(mdn);
		} catch (NetworkQueryException e) {
			logger.error("Unable to fetch NetworkInfo for {}", this);
		} finally {
			loadedNetworkInfo = true;
		}
	}

	/* **************************************************
	 * Network Update Methods
	 */

	protected void reserve() throws ReserveException {
		if (networkInfo != null && networkInfo.getStatus() != NetworkStatus.C)
			throw new ReserveException("Device is already active and cannot reserve a new MDN");
		networkInfo = networkService.reserveMdn();
	}

	protected void connect() throws ConnectException {
		if (networkInfo == null)
			throw new ConnectException("NetworkInfo is null and cannot be connected");
		networkInfo = networkService.connect(networkInfo);
	}

	protected void disconnect() throws DisconnectException {
		if (networkInfo == null)
			throw new DisconnectException("NetworkInfo is null and cannot be disconnected");
		networkInfo = networkService.disconnect(networkInfo);
	}

	protected void suspend() throws SuspendException {
		if (networkInfo == null)
			throw new SuspendException("NetworkInfo is null and cannot be suspended");
		networkInfo = networkService.suspendService(networkInfo);
	}

	protected void restore() throws RestoreException {
		if (networkInfo == null)
			throw new RestoreException("NetworkInfo is null and cannot be restored");
		networkInfo = networkService.restoreService(networkInfo);
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "NetworkDevice [networkInfo=" + networkInfo + "]";
	}

}