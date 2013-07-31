package com.tscp.mvna.account.device.network;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.tscp.mvna.account.device.Device;

@Deprecated
@XmlRootElement
public class OldNetworkInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String esnmeiddec;
	private String esnmeidhex;
	private String mdn;
	private String msid;
	private String effectivedate;
	private String effectivetime;
	private String expirationdate;
	private String expirationtime;
	private NetworkStatus status;
	private boolean stale;

	public static boolean isAlphaNumeric(
			String value) {
		return !isNumeric(value) && !isAlpha(value);
	}

	public static boolean isNumeric(
			String value) {
		return value.matches("\\d+");
	}

	public static boolean isAlpha(
			String value) {
		return value.matches("\\D+");
	}

	private boolean isDec(
			String esn) {
		return esn != null && isNumeric(esn) && (esn.length() == 11 || esn.length() == 18);
	}

	private boolean isHex(
			String esn) {
		return esn != null && isAlphaNumeric(esn) && (esn.length() == 8 || esn.length() == 14);
	}

	public OldNetworkInfo() {
		// do nothing
	}

	public OldNetworkInfo(Device device) {
		setEsn(device.getValue());
	}

	public boolean hasEsn() {
		return (esnmeiddec != null && !esnmeiddec.isEmpty()) || (esnmeidhex != null && !esnmeidhex.isEmpty());
	}

	public boolean hasMdn() {
		return mdn != null && !mdn.isEmpty();
	}

	public boolean hsMsid() {
		return msid != null && !msid.isEmpty();
	}

	public void setEsn(
			String esn) {
		if (isDec(esn))
			esnmeiddec = esn;
		else if (isHex(esn))
			esnmeidhex = esn;
		else
			throw new ConnectException("ESN provided is not decimal or hex format");
	}

	/* **************************************************
	 * Getters and Setters
	 */

	public String getEsnmeiddec() {
		return esnmeiddec;
	}

	public void setEsnmeiddec(
			String esnmeiddec) {
		this.esnmeiddec = esnmeiddec == null ? null : esnmeiddec.trim();
	}

	public String getEsnmeidhex() {
		return esnmeidhex;
	}

	public void setEsnmeidhex(
			String esnmeidhex) {
		this.esnmeidhex = esnmeidhex == null ? null : esnmeidhex.trim();
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(
			String mdn) {
		this.mdn = mdn == null ? null : mdn.trim();
	}

	public String getMsid() {
		return msid;
	}

	public void setMsid(
			String msid) {
		this.msid = msid == null ? null : msid.trim();
	}

	public String getEffectivedate() {
		return effectivedate;
	}

	public void setEffectivedate(
			String effectivedate) {
		this.effectivedate = effectivedate;
	}

	public String getEffectivetime() {
		return effectivetime;
	}

	public void setEffectivetime(
			String effectivetime) {
		this.effectivetime = effectivetime;
	}

	public String getExpirationdate() {
		return expirationdate;
	}

	public void setExpirationdate(
			String expirationdate) {
		this.expirationdate = expirationdate;
	}

	public String getExpirationtime() {
		return expirationtime;
	}

	public void setExpirationtime(
			String expirationtime) {
		this.expirationtime = expirationtime;
	}

	public NetworkStatus getStatus() {
		return status;
	}

	public void setStatus(
			NetworkStatus status) {
		this.status = status;
	}

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

	@Override
	public String toString() {
		return "NetworkInfo [esnmeiddec=" + esnmeiddec + ", mdn=" + mdn + ", status=" + status + ", stale=" + stale + "]";
	}

}