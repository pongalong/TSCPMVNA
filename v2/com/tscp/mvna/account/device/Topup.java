package com.tscp.mvna.account.device;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.money.Money;

@Entity
@Table(name = "DEVICE_TOPUP")
public class Topup implements Serializable {
	private static final long serialVersionUID = 2662262678054930566L;
	private DeviceAndService device;
	private int deviceId;
	private Money value;

	/* **************************************************
	 * Getters and Setters
	 */

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	@XmlTransient
	protected DeviceAndService getDevice() {
		return device;
	}

	protected void setDevice(
			DeviceAndService device) {
		this.device = device;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "device"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "device_id", unique = true, nullable = false)
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(
			int deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "amount")
	@Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount")
	public Money getValue() {
		return value;
	}

	public void setValue(
			Money value) {
		this.value = value;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Topup [deviceId=" + deviceId + ", value=" + value + "]";
	}

}