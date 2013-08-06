package com.tscp.mvna.account.device;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "DEVICE_TOPUP")
public class Topup implements Serializable {
	private static final long serialVersionUID = 2662262678054930566L;
	protected static final DecimalFormat df = new DecimalFormat("0.00000");
	private int deviceId;
	private DeviceAndService device;
	private Double amount;

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

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(
			Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Topup [deviceId=" + deviceId + ", amount=" + amount + "]";
	}

	@Transient
	@XmlElement
	public String getCurrencyValue() {
		return amount == null ? null : NumberFormat.getCurrencyInstance().format(amount);
	}

}