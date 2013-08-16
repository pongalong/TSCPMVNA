package com.tscp.mvna.account.kenan.provision;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvna.account.kenan.provision.service.ProvisionService;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultBillingPackage;
import com.tscp.mvne.config.PROVISION;
import com.tscp.util.DateUtils;

@XmlRootElement
public class ServicePackage extends KenanObject {
	private static final long serialVersionUID = 6237387974191464611L;
	protected static final Logger logger = LoggerFactory.getLogger(ServicePackage.class);
	protected int instanceId;
	protected int id = PROVISION.PACKAGE.ID;
	protected int accountNo;
	protected String name;
	protected DateTime activeDate = new DateTime();
	protected DateTime inactiveDate;

	// protected List<ServiceComponent> componentList;

	/* **************************************************
	 * Getters and Setters
	 */

	public ServicePackage() {

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

	// @XmlElement
	// public List<ServiceComponent> getComponentList() {
	// return componentList;
	// }
	//
	// public void setComponentList(
	// List<ServiceComponent> componentList) {
	// this.componentList = componentList;
	// }

	/* **************************************************
	 * Helper Methods
	 */

	// @XmlTransient
	// public void addComponent(
	// ServiceComponent serviceComponent) {
	// if (componentList == null)
	// componentList = new ArrayList<ServiceComponent>();
	// componentList.add(serviceComponent);
	// }

	@XmlTransient
	public boolean isEmpty() {
		return instanceId == 0 || accountNo == 0 || id == 0;
	}

	/* **************************************************
	 * Adaptor Methods for KenanGateway Objects
	 */

	public static ServicePackage fromBillingPackage(
			com.telscape.billingserviceinterface.Package billingPackage) {

		ServicePackage servicePackage = new ServicePackage();

		servicePackage.setInstanceId(billingPackage.getPackageInstanceId());
		servicePackage.setName(billingPackage.getPackageName());
		servicePackage.setId(billingPackage.getPackageId());

		// set account number
		if (billingPackage.getAccountNo() != null && !billingPackage.getAccountNo().isEmpty())
			servicePackage.setAccountNo(Integer.parseInt(billingPackage.getAccountNo()));

		// set dates
		if (billingPackage.getActiveDate() != null && !ProvisionService.isNullDate(billingPackage.getActiveDate()))
			servicePackage.setActiveDate(new DateTime(billingPackage.getActiveDate().toGregorianCalendar()));
		if (billingPackage.getDiscDate() != null && !ProvisionService.isNullDate(billingPackage.getDiscDate()))
			servicePackage.setInactiveDate(new DateTime(billingPackage.getDiscDate().toGregorianCalendar()));

		return servicePackage;
	}

	public static com.telscape.billingserviceinterface.Package toBillingPackage(
			ServicePackage servicePackage) {
		com.telscape.billingserviceinterface.Package result = new DefaultBillingPackage();
		result.setAccountNo(Integer.toString(servicePackage.getAccountNo()));

		if (servicePackage.isEmpty())
			return result;

		result.setPackageInstanceId(servicePackage.getInstanceId());
		result.setPackageId(servicePackage.getId());
		result.setPackageName(servicePackage.getName());
		result.setActiveDate(DateUtils.getXMLCalendar(servicePackage.getActiveDate()));
		result.setDiscDate(DateUtils.getXMLCalendar(servicePackage.getInactiveDate()));

		return result;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "ServicePackage [id=" + id + ", instanceId=" + instanceId + ", accountNo=" + accountNo + ", name=" + name + "]";
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