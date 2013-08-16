package com.tscp.mvna.account.kenan.provision;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.BillingService;
import com.telscape.billingserviceinterface.Service;
import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultBillingService;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultBillingName;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultCustAddress;
import com.tscp.mvne.config.PROVISION;

@XmlRootElement
public class ServiceInstance extends KenanObject {
	private static final long serialVersionUID = -8723045607424974742L;
	protected static final Logger logger = LoggerFactory.getLogger(ServiceInstance.class);
	public static final DateTimeFormatter serviceDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy kk:mm:ss aa");
	// protected KenanAccount account;
	protected int subscriberNo;
	protected int externalIdType;
	protected String externalId;
	protected DateTime activeDate;
	protected DateTime inactiveDate;

	/* **************************************************
	 * Constructors
	 */

	public ServiceInstance() {
		externalIdType = PROVISION.SERVICE.EXTERNAL_ID_TYPE;
	}

	// public ServiceInstance(KenanAccount account) {
	// this.account = account;
	// }

	public ServiceInstance(NetworkInfo networkInfo) {
		externalId = networkInfo.getMdn();
	}

	// public ServiceInstance(KenanAccount account, NetworkInfo networkInfo) {
	// this.account = account;
	// this.externalId = networkInfo.getMdn();
	// }

	/* **************************************************
	 * Getters and Setters
	 */

	// @XmlTransient
	// public KenanAccount getAccount() {
	// return account;
	// }
	//
	// public void setAccount(
	// KenanAccount account) {
	// this.account = account;
	// }

	@XmlAttribute
	public int getSubscriberNo() {
		return subscriberNo;
	}

	public void setSubscriberNo(
			int subscriberNo) {
		this.subscriberNo = subscriberNo;
	}

	@XmlTransient
	public int getExternalIdType() {
		return externalIdType;
	}

	public void setExternalIdType(
			int externalIdType) {
		this.externalIdType = externalIdType;
	}

	@XmlAttribute
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(
			String externalId) {
		this.externalId = externalId == null ? null : externalId.trim();
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

	public static DateTime parseServiceDate(
			String serviceDate) {
		if (serviceDate != null && !serviceDate.trim().isEmpty())
			return serviceDateFormat.parseDateTime(serviceDate.trim());
		else
			return null;
	}

	@XmlTransient
	public void setActiveDate(
			String activeDate) {
		this.activeDate = parseServiceDate(activeDate);
	}

	@XmlTransient
	public void setInactiveDate(
			String inactiveDate) {
		this.inactiveDate = parseServiceDate(inactiveDate);
	}

	@XmlTransient
	public boolean isEmpty() {
		return externalId == null || externalId.isEmpty();
	}

	/* **************************************************
	 * Adaptor Methods for KenanGateway Objects
	 */

	public static ServiceInstance fromService(
			Service service) {
		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setSubscriberNo(Integer.parseInt(service.getSubscrNo()));
		serviceInstance.setExternalId(service.getExternalId());
		serviceInstance.setExternalIdType(service.getExternalIdType());
		serviceInstance.setActiveDate(parseServiceDate(service.getActiveDate()));
		serviceInstance.setInactiveDate(parseServiceDate(service.getInactiveDate()));
		return serviceInstance;
	}

	public static Service toService(
			ServiceInstance serviceInstance, KenanAccount account) {

		Service result = new Service();

		if (account != null)
			result.setAccountNo(Integer.toString(account.getAccountNo()));

		result.setSubscrNo(Integer.toString(serviceInstance.getSubscriberNo()));
		result.setExternalId(serviceInstance.getExternalId());
		result.setExternalIdType(serviceInstance.getExternalIdType());

		if (serviceInstance.getActiveDate() != null)
			result.setActiveDate(serviceInstance.getActiveDate().toString(serviceDateFormat));
		if (serviceInstance.getInactiveDate() != null)
			result.setInactiveDate(serviceInstance.getInactiveDate().toString(serviceDateFormat));

		return result;
	}

	public static BillingService toBillingService(
			ServiceInstance serviceInstance, KenanAccount account) {
		BillingService result = new DefaultBillingService();
		result.setAccountNo(Integer.toString(account.getAccountNo()));
		result.setExternalId(serviceInstance.getExternalId());
		result.setServiceAddr(new DefaultCustAddress(account.getContact().getAddress()));
		result.setServiceName(new DefaultBillingName(account.getContact()));
		return result;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "ServiceInstance [externalId=" + externalId + ", subscriberNo=" + subscriberNo + "]";
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