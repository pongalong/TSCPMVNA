package com.tscp.mvna.account.kenan.product;

import java.util.ArrayList;
import java.util.List;

import com.telscape.billingserviceinterface.BillingService;
import com.telscape.billingserviceinterface.Service;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.mvna.account.kenan.provision.service.ProvisionServiceComponentService;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultBillingService;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultBillingName;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultCustAddress;

public class ServiceInstanceV2 extends ServiceInstance {
	private static final long serialVersionUID = 6771415347288410529L;
	protected ServicePackageV2 servicePackage;
	protected List<ServiceComponentV2> serviceComponents;

	/* **************************************************
	 * Constructors
	 */

	public ServiceInstanceV2() {
		// do nothing
	}

	public ServiceInstanceV2(ServicePackageV2 servicePackage) {
		this.servicePackage = servicePackage;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	public void load() {
		super.reset();
		if (serviceComponents == null)
			serviceComponents = new ArrayList<ServiceComponentV2>();
		serviceComponents.clear();
		serviceComponents.addAll(loadValue());
	}

	@Override
	protected List<ServiceComponentV2> loadValue() {
		try {
			return ProvisionServiceComponentService.properGetActiveComponents(servicePackage.getAccount().getAccountNo(), this);
		} catch (ProvisionFetchException e) {
			logger.error("Error loading ServiceComponents for {}", this);
		} finally {
			loaded = true;
		}

		return null;
	}

	/* **************************************************
	 * Getter and Setter Methods
	 */

	public ServicePackageV2 getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(
			ServicePackageV2 servicePackage) {
		this.servicePackage = servicePackage;
	}

	public List<ServiceComponentV2> getServiceComponents() {
		return serviceComponents;
	}

	public void setServiceComponents(
			List<ServiceComponentV2> serviceComponents) {
		this.serviceComponents = serviceComponents;
	}

	/* **************************************************
	 * Adaptor Methods
	 */

	public static ServiceInstanceV2 fromService(
			Service service) {
		ServiceInstanceV2 serviceInstance = new ServiceInstanceV2();
		serviceInstance.setSubscriberNo(Integer.parseInt(service.getSubscrNo()));
		serviceInstance.setExternalId(service.getExternalId());
		serviceInstance.setExternalIdType(service.getExternalIdType());
		serviceInstance.setActiveDate(parseServiceDate(service.getActiveDate()));
		serviceInstance.setInactiveDate(parseServiceDate(service.getInactiveDate()));
		return serviceInstance;
	}

	public static Service toService(
			ServiceInstanceV2 serviceInstance, KenanAccount account) {

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
			ServiceInstanceV2 serviceInstance, KenanAccount account) {
		BillingService result = new DefaultBillingService();
		result.setAccountNo(Integer.toString(account.getAccountNo()));
		result.setExternalId(serviceInstance.getExternalId());
		result.setServiceAddr(new DefaultCustAddress(account.getContact().getAddress()));
		result.setServiceName(new DefaultBillingName(account.getContact()));
		return result;
	}

}