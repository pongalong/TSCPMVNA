package com.tscp.mvna.account.device;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Embeddable
public class DeviceHistoryId implements Serializable {
	private static final long serialVersionUID = 8495195441076259180L;
	private int deviceId;
	private DateTime dateTime = new DateTime();

	@Column(name = "DEVICE_ID")
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(
			int deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "DATE_TIME")
	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(
			DateTime dateTime) {
		this.dateTime = dateTime;
	}

}