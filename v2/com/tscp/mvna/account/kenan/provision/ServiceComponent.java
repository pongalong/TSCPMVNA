package com.tscp.mvna.account.kenan.provision;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvne.config.PROVISION;
import com.tscp.util.DateUtils;

@XmlRootElement
public class ServiceComponent extends KenanObject {
	private static final long serialVersionUID = -7131375586432560540L;
	protected static final Logger logger = LoggerFactory.getLogger(ServiceComponent.class);
	protected int id;
	protected int instanceId;
	protected int serverId;
	protected String name;
	protected DateTime activeDate = new DateTime();
	protected DateTime inactiveDate;

	protected ServicePackage servicePackage;
	protected ServiceInstance serviceInstance;

	/* **************************************************
	 * Constructors
	 */

	public ServiceComponent() {
		// do nothing
	}

	public ServiceComponent(String externalId) {
		serviceInstance = new ServiceInstance();
		serviceInstance.setExternalId(externalId);
	}

	public ServiceComponent(ServiceInstance serviceInstance, ServicePackage servicePackage) {
		this.serviceInstance = serviceInstance;
		this.servicePackage = servicePackage;
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

	public ServicePackage getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(
			ServicePackage servicePackage) {
		this.servicePackage = servicePackage;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(
			ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	/* **************************************************
	 * Helper Methods
	 */

	@XmlTransient
	public boolean isNextDay() {
		return DateUtils.sameDay(getActiveDate(), new DateTime().plusDays(1));
	}

	@XmlTransient
	public boolean isCurrentMonth() {
		return DateUtils.sameMonth(getActiveDate(), new DateTime());
	}

	@XmlTransient
	public boolean isEmpty() {
		return instanceId == 0 || serviceInstance == null || serviceInstance.isEmpty();
	}

	@XmlTransient
	public boolean isActiveType() {
		return id == PROVISION.COMPONENT.INSTALL || id == PROVISION.COMPONENT.REINSTALL;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		String toString = "ServiceComponent [id=" + id;
		toString += serviceInstance != null ? ", externalId=" + serviceInstance.getExternalId() : null;
		toString += ", name=" + name + ", instanceId=" + instanceId + "]";
		return toString;
	}

}