package com.tscp.mvna.account.kenan.provision;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvne.config.PROVISION;

/**
 * Represents a ServicePackage in Kenan that is associated at the account level. This object does not need to be used
 * directly as references to the instance id are contained in ServiceComponents
 * 
 * @author Jonathan
 * 
 */
@XmlRootElement
public class ServicePackage extends KenanObject {
	private static final long serialVersionUID = 6237387974191464611L;
	protected static final Logger logger = LoggerFactory.getLogger(ServicePackage.class);
	protected KenanAccount account;
	protected int instanceId;
	protected int id = PROVISION.PACKAGE.ID;
	protected int serverId;
	protected String name;
	protected DateTime activeDate = new DateTime();
	protected DateTime inactiveDate;

	/* **************************************************
	 * Getters and Setters
	 */

	public ServicePackage() {

	}

	public ServicePackage(KenanAccount account) {
		this.account = account;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	public void load() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not have any values to load");
	}

	@Override
	protected Object loadValue() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not have any values to load");
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(
			int id) {
		this.id = id;
	}

	@XmlAttribute
	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(
			int instanceId) {
		this.instanceId = instanceId;
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(
			int serverId) {
		this.serverId = serverId;
	}

	@XmlTransient
	public KenanAccount getAccount() {
		return account;
	}

	public void setAccount(
			KenanAccount account) {
		this.account = account;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(
			String name) {
		this.name = name;
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

	/* **************************************************
	 * Helper Methods
	 */

	@XmlTransient
	public boolean isEmpty() {
		return (account != null && account.getAccountNo() != 0) || instanceId == 0 || id == 0;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "ServicePackage [id=" + id + ", instanceId=" + instanceId + ", name=" + name + "]";
	}

}