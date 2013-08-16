package com.tscp.mvna.account.kenan.provision;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.PkgComponent;
import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvna.account.kenan.provision.service.ProvisionService;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultPkgComponent;
import com.tscp.util.DateUtils;

@XmlRootElement
public class ServiceComponent extends KenanObject {
	private static final long serialVersionUID = -7131375586432560540L;
	protected static final Logger logger = LoggerFactory.getLogger(ServiceComponent.class);
	protected int instanceId;
	protected int id;
	protected String name;
	protected String externalId;
	protected DateTime activeDate = new DateTime();
	protected DateTime inactiveDate;

	/* **************************************************
	 * Constructors
	 */

	protected ServiceComponent() {
		// do nothing
	}

	public ServiceComponent(ServiceComponent component) {
		this.externalId = component.getExternalId();
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

	public static final ServiceComponent fromPkgComponent(
			PkgComponent billingComponent) {

		ServiceComponent serviceComponent = new ServiceComponent();
		serviceComponent.setInstanceId(billingComponent.getComponentInstanceId());
		serviceComponent.setId(billingComponent.getComponentId());
		serviceComponent.setName(billingComponent.getComponentName());
		serviceComponent.setExternalId(billingComponent.getExternalId());

		if (billingComponent.getComponentActiveDate() != null && !ProvisionService.isNullDate(billingComponent.getComponentActiveDate()))
			serviceComponent.setActiveDate(new DateTime(billingComponent.getComponentActiveDate().toGregorianCalendar()));
		if (billingComponent.getDiscDate() != null && !ProvisionService.isNullDate(billingComponent.getDiscDate()))
			serviceComponent.setInactiveDate(new DateTime(billingComponent.getDiscDate().toGregorianCalendar()));

		return serviceComponent;
	}

	public static final PkgComponent toPkgComponent(
			ServiceComponent serviceComponent, ServicePackage servicePackage) {

		PkgComponent result = new DefaultPkgComponent();
		result.setExternalId(serviceComponent.getExternalId());
		result.setComponentId(serviceComponent.getId());
		result.setComponentInstanceId(serviceComponent.getInstanceId());
		result.setComponentName(serviceComponent.getName());
		result.setComponentActiveDate(DateUtils.getXMLCalendar(serviceComponent.getActiveDate()));
		result.setDiscDate(DateUtils.getXMLCalendar(serviceComponent.getInactiveDate()));

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
		return "ServiceComponent [id=" + id + ", externalId=" + externalId + ", name=" + name + ", instanceId=" + instanceId + "]";
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not have any values to load");
	}

	@Override
	protected Object loadValue() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not have any values to load");
	}

}
