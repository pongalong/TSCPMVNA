package com.tscp.mvna.account.kenan.provision;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.mvna.account.kenan.provision.service.ServiceComponentProvisioner;
import com.tscp.mvne.config.PROVISION;

/**
 * Represents a ServiceInstance on a Kenan Account. This can be associated on an account without a ServicePackage or a
 * ServiceComponent.
 * 
 * @author Jonathan
 * 
 */
@XmlRootElement
public class ServiceInstance extends KenanObject {
	private static final long serialVersionUID = -8723045607424974742L;
	protected static final Logger logger = LoggerFactory.getLogger(ServiceInstance.class);
	public static final DateTimeFormatter serviceDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy kk:mm:ss aa");
	protected KenanAccount account;
	protected int subscriberNo;
	protected int externalIdType = PROVISION.SERVICE.EXTERNAL_ID_TYPE;
	protected String externalId;
	protected DateTime activeDate;
	protected DateTime inactiveDate;

	protected List<ServiceComponent> serviceComponents;

	/* **************************************************
	 * Constructors
	 */

	public ServiceInstance() {
		// prevent instantiation
	}

	public ServiceInstance(KenanAccount account) {
		this.account = account;
	}

	public ServiceInstance(String externalId) {
		this.externalId = externalId;
	}

	public ServiceInstance(NetworkInfo networkInfo) {
		externalId = networkInfo.getMdn();
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	public void load() {
		super.reset();
		if (serviceComponents == null)
			serviceComponents = new ArrayList<ServiceComponent>();
		serviceComponents.clear();
		serviceComponents.addAll(loadValue());
	}

	@Override
	protected List<ServiceComponent> loadValue() {
		try {
			return ServiceComponentProvisioner.getActiveComponents(this);
		} catch (ProvisionFetchException e) {
			logger.error("Error loading ServiceComponents for {}", this);
		} finally {
			loaded = true;
		}

		return null;
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
	public Integer getExternalIdType() {
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

	public List<ServiceComponent> getServiceComponents() {
		if (serviceComponents == null && !loaded)
			serviceComponents = loadValue();
		return serviceComponents;
	}

	public void setServiceComponents(
			List<ServiceComponent> serviceComponents) {
		this.serviceComponents = serviceComponents;
	}

	public ServiceComponent getActiveComponent() {
		if (getServiceComponents() != null && !getServiceComponents().isEmpty())
			return getServiceComponents().get(0);
		return null;
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
	public boolean isEmpty() {
		return externalId == null || externalId.isEmpty();
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "ServiceInstance [externalId=" + externalId + ", subscriberNo=" + subscriberNo + "]";
	}

}