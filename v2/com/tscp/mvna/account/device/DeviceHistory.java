package com.tscp.mvna.account.device;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;

import com.tscp.mvna.account.device.network.NetworkStatus;

@Entity
@Table(name = "DEVICE_HISTORY")
@XmlRootElement
public class DeviceHistory implements Serializable {
	private static final long serialVersionUID = -5507362010709737768L;
	private DeviceHistoryId id = new DeviceHistoryId();
	private String mdn;
	private NetworkStatus status;
	private String value;

	public DeviceHistory() {
		// do nothing
	}

	public DeviceHistory(Device device) {
		this.id.setDeviceId(device.getId());
		this.mdn = device.getNetworkInfo().getMdn();
		this.status = device.getStatus();
		this.value = device.getValue();
	}

	@Transient
	@XmlElement
	public int getDeviceId() {
		return id.getDeviceId();
	}

	@Transient
	@XmlElement
	public DateTime getDateTime() {
		return id.getDateTime();
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@EmbeddedId
	@XmlTransient
	DeviceHistoryId getId() {
		return id;
	}

	void setId(
			DeviceHistoryId id) {
		this.id = id;
	}

	@Column(name = "MDN")
	public String getMdn() {
		return mdn;
	}

	public void setMdn(
			String mdn) {
		this.mdn = mdn;
	}

	@Column(name = "STATUS_ID")
	@Enumerated(EnumType.ORDINAL)
	public NetworkStatus getStatus() {
		return status;
	}

	public void setStatus(
			NetworkStatus status) {
		this.status = status;
	}

	@Column(name = "VALUE")
	public String getValue() {
		return value;
	}

	public void setValue(
			String value) {
		this.value = value;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "NewDeviceHistory [id=" + getDeviceId() + ", dateTime=" + getDateTime() + ", mdn=" + mdn + ", value=" + value + ", status=" + status + "]";
	}

}