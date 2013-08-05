package com.tscp.mvna.account.kenan.provision;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.telscape.billingserviceinterface.BillingService;
import com.telscape.billingserviceinterface.Service;
import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultBillingService;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultBillingName;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultCustAddress;
import com.tscp.mvne.config.PROVISION;

@XmlRootElement
public class ServiceInstance {
	public static final DateTimeFormatter serviceDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy kk:mm:ss aa");
	protected KenanAccount account;
	protected int subscriberNo;
	protected int externalIdType;
	protected String externalId;
	protected DateTime activeDate;
	protected DateTime inactiveDate;

	public ServiceInstance() {
		externalIdType = PROVISION.SERVICE.EXTERNAL_ID_TYPE;
	}

	public ServiceInstance(KenanAccount account) {
		this.account = account;
	}

	public ServiceInstance(NetworkInfo networkInfo) {
		externalId = networkInfo.getMdn();
	}

	public ServiceInstance(KenanAccount account, NetworkInfo networkInfo) {
		this.account = account;
		this.externalId = networkInfo.getMdn();
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@XmlTransient
	public KenanAccount getAccount() {
		return account;
	}

	public void setAccount(
			KenanAccount account) {
		this.account = account;
	}

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

	static DateTime parseServiceDate(
			String serviceDate) {
		if (serviceDate != null && !serviceDate.trim().isEmpty())
			return serviceDateFormat.parseDateTime(serviceDate.trim());
		else
			return null;
	}

	@XmlTransient
	public boolean isEmpty() {
		return externalId == null || externalId.isEmpty();
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

	/* **************************************************
	 * Adaptor Methods for KenanGateway Objects
	 */

	public ServiceInstance(Service service) {
		if (account == null)
			account = new KenanAccount();
		account.setAccountNo(Integer.parseInt(service.getAccountNo()));
		subscriberNo = Integer.parseInt(service.getSubscrNo());
		externalId = service.getExternalId();
		externalIdType = service.getExternalIdType();
		activeDate = parseServiceDate(service.getActiveDate());
		inactiveDate = parseServiceDate(service.getInactiveDate());
	}

	public Service toService() {
		Service result = new Service();

		if (account != null)
			result.setAccountNo(Integer.toString(account.getAccountNo()));

		result.setSubscrNo(Integer.toString(subscriberNo));
		result.setExternalId(externalId);
		result.setExternalIdType(externalIdType);

		if (activeDate != null)
			result.setActiveDate(activeDate.toString(serviceDateFormat));
		if (inactiveDate != null)
			result.setInactiveDate(inactiveDate.toString(serviceDateFormat));

		return result;
	}

	public BillingService toBillingService() {
		BillingService result = new DefaultBillingService();

		result.setAccountNo(Integer.toString(account.getAccountNo()));
		result.setExternalId(getExternalId());
		result.setServiceAddr(new DefaultCustAddress(account.getContact().getAddress()));
		result.setServiceName(new DefaultBillingName(account.getContact()));

		return result;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "ServiceInstance [subscriberNo=" + subscriberNo + ", externalId=" + externalId + "]";
	}

}
