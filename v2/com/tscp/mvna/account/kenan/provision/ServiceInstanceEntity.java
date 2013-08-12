package com.tscp.mvna.account.kenan.provision;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tscp.mvna.account.kenan.KenanAccount;

@Entity
@Table(name = "EXTERNAL_ID_EQUIP_MAP")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ServiceInstanceEntity implements Serializable {
	private static final long serialVersionUID = -3718681482899990537L;
	public static final DateTimeFormatter serviceDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy kk:mm:ss aa");
	protected String externalId;
	protected int subscriberNo;
	protected int subscriberResets;
	protected int externalIdType;
	protected KenanAccount account;
	protected int serverId;
	protected DateTime activeDate;
	protected DateTime inactiveDate;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(
			String externalId) {
		this.externalId = externalId;
	}

	public int getSubscriberNo() {
		return subscriberNo;
	}

	public void setSubscriberNo(
			int subscriberNo) {
		this.subscriberNo = subscriberNo;
	}

	public int getSubscriberResets() {
		return subscriberResets;
	}

	public void setSubscriberResets(
			int subscriberResets) {
		this.subscriberResets = subscriberResets;
	}

	public int getExternalIdType() {
		return externalIdType;
	}

	public void setExternalIdType(
			int externalIdType) {
		this.externalIdType = externalIdType;
	}

	public KenanAccount getAccount() {
		return account;
	}

	public void setAccount(
			KenanAccount account) {
		this.account = account;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(
			int serverId) {
		this.serverId = serverId;
	}

	public DateTime getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(
			DateTime activeDate) {
		this.activeDate = activeDate;
	}

	public DateTime getInactiveDate() {
		return inactiveDate;
	}

	public void setInactiveDate(
			DateTime inactiveDate) {
		this.inactiveDate = inactiveDate;
	}

}
