package com.tscp.mvna.account.kenan.provision;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.provision.service.ProvisionService;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultBillingPackage;
import com.tscp.mvne.config.PROVISION;
import com.tscp.util.DateUtils;

@XmlRootElement
public class ServicePackage {
	protected static final Logger logger = LoggerFactory.getLogger(ServicePackage.class);
	protected int instanceId;
	protected int id;
	protected int accountNo;
	protected String name;
	protected DateTime activeDate;
	protected DateTime inactiveDate;
	protected List<ServiceComponent> componentList;

	public ServicePackage() {
		id = PROVISION.PACKAGE.ID;
		activeDate = new DateTime();
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

	@XmlTransient
	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(
			int instanceId) {
		this.instanceId = instanceId;
	}

	@XmlAttribute
	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(
			int accountNo) {
		this.accountNo = accountNo;
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

	@XmlElement
	public List<ServiceComponent> getComponentList() {
		return componentList;
	}

	public void setComponentList(
			List<ServiceComponent> componentList) {
		this.componentList = componentList;
	}

	/* **************************************************
	 * Helper Methods
	 */

	@XmlTransient
	public void addComponent(
			ServiceComponent serviceComponent) {
		if (componentList == null)
			componentList = new ArrayList<ServiceComponent>();
		componentList.add(serviceComponent);
	}

	@XmlTransient
	public boolean isEmpty() {
		return instanceId == 0 || accountNo == 0 || id == 0;
	}

	/* **************************************************
	 * Adaptor Methods for KenanGateway Objects
	 */

	public ServicePackage(com.telscape.billingserviceinterface.Package billingPackage) {
		instanceId = billingPackage.getPackageInstanceId();
		name = billingPackage.getPackageName();
		id = billingPackage.getPackageId();

		// set account number
		if (billingPackage.getAccountNo() != null && !billingPackage.getAccountNo().isEmpty())
			accountNo = Integer.parseInt(billingPackage.getAccountNo());

		// set dates
		if (billingPackage.getActiveDate() != null && !ProvisionService.isNullDate(billingPackage.getActiveDate()))
			activeDate = new DateTime(billingPackage.getActiveDate().toGregorianCalendar());
		if (billingPackage.getDiscDate() != null && !ProvisionService.isNullDate(billingPackage.getDiscDate()))
			inactiveDate = new DateTime(billingPackage.getDiscDate().toGregorianCalendar());
	}

	public com.telscape.billingserviceinterface.Package toBillingPackage() {
		com.telscape.billingserviceinterface.Package result = new DefaultBillingPackage();
		result.setAccountNo(Integer.toString(accountNo));

		if (isEmpty())
			return result;

		result.setPackageInstanceId(getInstanceId());
		result.setPackageId(getId());
		result.setPackageName(getName());
		result.setActiveDate(DateUtils.getXMLCalendar(getActiveDate()));
		result.setDiscDate(DateUtils.getXMLCalendar(getInactiveDate()));

		return result;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "ServicePackage [id=" + id + ", instanceId=" + instanceId + ", accountNo=" + accountNo + ", name=" + name + "]";
	}

}