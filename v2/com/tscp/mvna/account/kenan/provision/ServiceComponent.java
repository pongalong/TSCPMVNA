package com.tscp.mvna.account.kenan.provision;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.PkgComponent;
import com.tscp.mvna.account.kenan.provision.service.ProvisionService;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultPkgComponent;
import com.tscp.util.DateUtils;

@XmlRootElement
public class ServiceComponent {
	protected static final Logger logger = LoggerFactory.getLogger(ServiceComponent.class);
	protected ServicePackage servicePackage;
	protected int instanceId;
	protected int id;
	protected String name;
	protected String externalId;
	protected DateTime activeDate;
	protected DateTime inactiveDate;

	public ServiceComponent() {
		activeDate = new DateTime();
	}

	public ServiceComponent(ServiceComponent component) {
		this();
		this.setExternalId(component.getExternalId());
		this.setServicePackage(component.getServicePackage());
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

	@XmlTransient
	public ServicePackage getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(
			ServicePackage servicePackage) {
		this.servicePackage = servicePackage;
	}

	@XmlAttribute
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(
			String externalId) {
		this.externalId = externalId;
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
	public boolean isTommorrow() {
		return DateUtils.sameDay(getActiveDate(), new DateTime().plusDays(1));
	}

	@XmlTransient
	public boolean isCurrentMonth() {
		return DateUtils.sameMonth(getActiveDate(), new DateTime());
	}

	@XmlTransient
	public boolean isEmpty() {
		return instanceId == 0 || externalId == null || externalId.isEmpty();
	}

	/* **************************************************
	 * Adaptor Methods for KenanGateway Objects
	 */

	public ServiceComponent(PkgComponent billingComponent) {
		instanceId = billingComponent.getComponentInstanceId();
		id = billingComponent.getComponentId();
		name = billingComponent.getComponentName();
		externalId = billingComponent.getExternalId();

		// set dates
		if (billingComponent.getComponentActiveDate() != null && !ProvisionService.isNullDate(billingComponent.getComponentActiveDate()))
			activeDate = new DateTime(billingComponent.getComponentActiveDate().toGregorianCalendar());
		if (billingComponent.getDiscDate() != null && !ProvisionService.isNullDate(billingComponent.getDiscDate()))
			inactiveDate = new DateTime(billingComponent.getDiscDate().toGregorianCalendar());

		// set package
		if (servicePackage == null && billingComponent.getPackageInstanceId() > 0) {
			servicePackage = new ServicePackage();
			servicePackage.setInstanceId(billingComponent.getPackageInstanceId());
		}
	}

	@XmlTransient
	public PkgComponent toPkgComponent() {
		PkgComponent result = new DefaultPkgComponent();
		result.setExternalId(getExternalId());
		result.setComponentId(getId());
		result.setComponentInstanceId(getInstanceId());
		result.setComponentName(getName());
		result.setComponentActiveDate(DateUtils.getXMLCalendar(getActiveDate()));
		result.setDiscDate(DateUtils.getXMLCalendar(getInactiveDate()));

		if (servicePackage != null) {
			result.setPackageInstanceId(servicePackage.getInstanceId());
			result.setPackageId(servicePackage.getId());
			result.setPackageName(servicePackage.getName());
			result.setPackageActiveDate(DateUtils.getXMLCalendar(servicePackage.getActiveDate()));
		}

		return result;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "ServiceComponent [id=" + id + ", externalId=" + externalId + ", instanceId=" + instanceId + "]";
	}
}
