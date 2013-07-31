package com.tscp.mvna.account.device.network;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;

import com.tscp.mvna.account.device.network.exception.InvalidEsnException;

@XmlRootElement
public class NetworkInfo {
	private NetworkEsn esn;
	private String mdn;
	private String msid;
	private DateTime effectiveDate;
	private DateTime expirationDate;
	private NetworkStatus status;
	private boolean stale;

	public NetworkInfo() {
		// do nothing
	}

	public NetworkInfo(String esn) throws InvalidEsnException {
		this.esn = new NetworkEsn(esn);
	}

	/* **************************************************
	 * Getters and Setters
	 */

	public NetworkEsn getEsn() {
		return esn;
	}

	public void setEsn(
			NetworkEsn esn) {
		this.esn = esn;
	}

	@XmlAttribute
	public String getMdn() {
		return mdn;
	}

	public void setMdn(
			String mdn) {
		this.mdn = mdn;
	}

	public String getMsid() {
		return msid;
	}

	public void setMsid(
			String msid) {
		this.msid = msid;
	}

	public DateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(
			DateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public DateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(
			DateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	@XmlAttribute
	public NetworkStatus getStatus() {
		return status;
	}

	public void setStatus(
			NetworkStatus status) {
		this.status = status;
	}

	@XmlAttribute
	public boolean isStale() {
		return stale;
	}

	public void setStale(
			boolean stale) {
		this.stale = stale;
	}

	/* **************************************************
	 * Helper Methods
	 */

	public boolean isConnectable() {
		return hasEsn() && getStatus() != null && getStatus().isConnectable();
	}

	@XmlTransient
	public boolean hasEsn() {
		return esn != null && !esn.isEmpty();
	}

	@XmlTransient
	public boolean hasMdn() {
		return mdn != null && !mdn.isEmpty();
	}

	@XmlTransient
	public boolean hasMsid() {
		return msid != null && !msid.isEmpty();
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		String esnString = esn == null ? null : esn.getValue();
		return "NetworkInfo [esn=" + esnString + ", mdn=" + mdn + ", status=" + status + "]";
	}

}