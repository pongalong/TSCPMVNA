package com.tscp.mvna.account.kenan.service;

import org.joda.time.DateTime;

import com.telscape.billingserviceinterface.BillingService;
import com.telscape.billingserviceinterface.Package;
import com.telscape.billingserviceinterface.PkgComponent;
import com.telscape.billingserviceinterface.Service;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.ServiceComponent;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.ServicePackage;
import com.tscp.mvna.account.kenan.provision.service.Provisioner;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultBillingPackage;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultBillingService;
import com.tscp.mvna.account.kenan.provision.service.defaults.DefaultPkgComponent;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultBillingName;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultCustAddress;
import com.tscp.util.DateUtils;

public final class KenanAdapter {

	private KenanAdapter() {
		// prevent instantiation
	}

	/* **************************************************
	 * Adaptor Methods for ServiceComponent
	 */

	public static final ServiceComponent fromPkgComponent(
			PkgComponent pkgComponent) {

		ServiceComponent serviceComponent = new ServiceComponent();

		// set ServiceComponent properties
		serviceComponent.setInstanceId(pkgComponent.getComponentInstanceId());
		serviceComponent.setId(pkgComponent.getComponentId());
		serviceComponent.setServerId(pkgComponent.getComponentInstanceIdServ());
		serviceComponent.setName(pkgComponent.getComponentName());
		if (pkgComponent.getComponentActiveDate() != null && !Provisioner.isNullDate(pkgComponent.getComponentActiveDate()))
			serviceComponent.setActiveDate(new DateTime(pkgComponent.getComponentActiveDate().toGregorianCalendar()));
		if (pkgComponent.getDiscDate() != null && !Provisioner.isNullDate(pkgComponent.getDiscDate()))
			serviceComponent.setInactiveDate(new DateTime(pkgComponent.getDiscDate().toGregorianCalendar()));

		// set ServiceInstance properties
		serviceComponent.setServiceInstance(new ServiceInstance(pkgComponent.getExternalId()));
		serviceComponent.getServiceInstance().setExternalIdType(pkgComponent.getExternalIdType());

		// set ServicePackage properties
		serviceComponent.setServicePackage(new ServicePackage());
		serviceComponent.getServicePackage().setId(pkgComponent.getPackageId());
		serviceComponent.getServicePackage().setInstanceId(pkgComponent.getPackageInstanceId());
		serviceComponent.getServicePackage().setServerId(pkgComponent.getPackageInstanceIdServ());
		serviceComponent.getServicePackage().setName(pkgComponent.getPackageName());
		serviceComponent.getServicePackage().setActiveDate(new DateTime(pkgComponent.getPackageActiveDate().toGregorianCalendar()));

		// this is an unused property since only active components are important
		// the disconnect reason should always be the same and is only used when disconnecting
		// pkgComponent.getDiscReason();

		return serviceComponent;
	}

	public static final PkgComponent toPkgComponent(
			ServiceComponent serviceComponent) {

		PkgComponent result = new DefaultPkgComponent();

		// set ServiceComponent properties
		result.setComponentId(serviceComponent.getId());
		result.setComponentInstanceId(serviceComponent.getInstanceId());
		result.setComponentInstanceIdServ(serviceComponent.getServerId().shortValue());
		result.setComponentName(serviceComponent.getName());
		result.setComponentActiveDate(DateUtils.getXMLCalendar(serviceComponent.getActiveDate()));
		result.setDiscDate(DateUtils.getXMLCalendar(serviceComponent.getInactiveDate()));

		// set ServiceInstance properties
		if (serviceComponent.getServiceInstance() != null) {
			result.setExternalId(serviceComponent.getServiceInstance().getExternalId());
			result.setExternalIdType(serviceComponent.getServiceInstance().getExternalIdType().shortValue());
		}

		// set ServicePackage properties
		if (serviceComponent.getServicePackage() != null) {
			result.setPackageInstanceId(serviceComponent.getServicePackage().getInstanceId());
			result.setPackageId(serviceComponent.getServicePackage().getId());
			result.setPackageInstanceIdServ(serviceComponent.getServicePackage().getServerId().shortValue());
			result.setPackageName(serviceComponent.getServicePackage().getName());
			result.setPackageActiveDate(DateUtils.getXMLCalendar(serviceComponent.getServicePackage().getActiveDate()));
		}

		// this is an unused property since only active components are important
		// the disconnect reason should always be the same and is only used when disconnecting
		// result.setDiscReason(Provisioner.DISC_REASON);

		return result;
	}

	/* **************************************************
	 * Adaptor Methods for ServicePackage
	 */

	public static final ServicePackage fromBillingPackage(
			Package kenanPackage) {

		ServicePackage servicePackage = new ServicePackage();

		servicePackage.setInstanceId(kenanPackage.getPackageInstanceId());
		servicePackage.setName(kenanPackage.getPackageName());
		servicePackage.setId(kenanPackage.getPackageId());
		servicePackage.setServerId(kenanPackage.getPackageInstanceIdServ());

		// set account
		if (kenanPackage.getAccountNo() != null && !kenanPackage.getAccountNo().isEmpty())
			servicePackage.setAccount(new KenanAccount(Integer.parseInt(kenanPackage.getAccountNo())));

		// set dates
		if (kenanPackage.getActiveDate() != null && !Provisioner.isNullDate(kenanPackage.getActiveDate()))
			servicePackage.setActiveDate(new DateTime(kenanPackage.getActiveDate().toGregorianCalendar()));
		if (kenanPackage.getDiscDate() != null && !Provisioner.isNullDate(kenanPackage.getDiscDate()))
			servicePackage.setInactiveDate(new DateTime(kenanPackage.getDiscDate().toGregorianCalendar()));

		// this is an unused property since only active components are important
		// the disconnect reason should always be the same and is only used when disconnecting
		// and ServicePackages can be on an account without an externalId (ServiceInstance)
		// billingPackage.getDiscReason();
		// billingPackage.getExternalIdType();

		return servicePackage;
	}

	public static final Package toBillingPackage(
			ServicePackage servicePackage) {

		Package result = new DefaultBillingPackage();

		if (servicePackage.getAccount() != null && servicePackage.getAccount().getAccountNo() > 0)
			result.setAccountNo(Integer.toString(servicePackage.getAccount().getAccountNo()));

		if (servicePackage.isEmpty())
			return result;

		result.setPackageInstanceId(servicePackage.getInstanceId());
		result.setPackageId(servicePackage.getId());
		result.setPackageName(servicePackage.getName());
		result.setActiveDate(DateUtils.getXMLCalendar(servicePackage.getActiveDate()));
		result.setDiscDate(DateUtils.getXMLCalendar(servicePackage.getInactiveDate()));

		// result.setDiscReason(Provisioner.DISC_REASON);
		// result.setExternalIdType(PROVISION.SERVICE.EXTERNAL_ID_TYPE.shortValue());

		return result;
	}

	/* **************************************************
	 * Adaptor Methods for ServiceInstance
	 */

	public static final ServiceInstance fromService(
			Service service) {
		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setSubscriberNo(Integer.parseInt(service.getSubscrNo()));
		serviceInstance.setExternalId(service.getExternalId());
		serviceInstance.setExternalIdType(service.getExternalIdType());
		serviceInstance.setActiveDate(ServiceInstance.parseServiceDate(service.getActiveDate()));
		serviceInstance.setInactiveDate(ServiceInstance.parseServiceDate(service.getInactiveDate()));

		if (service.getAccountNo() != null && !service.getAccountNo().isEmpty())
			serviceInstance.setAccount(new KenanAccount(Integer.parseInt(service.getAccountNo())));

		return serviceInstance;
	}

	public static final Service toService(
			ServiceInstance serviceInstance) {
		Service result = new Service();
		if (serviceInstance.getAccount() != null)
			result.setAccountNo(Integer.toString(serviceInstance.getAccount().getAccountNo()));
		result.setSubscrNo(Integer.toString(serviceInstance.getSubscriberNo()));
		result.setExternalId(serviceInstance.getExternalId());
		result.setExternalIdType(serviceInstance.getExternalIdType());
		if (serviceInstance.getActiveDate() != null)
			result.setActiveDate(serviceInstance.getActiveDate().toString(ServiceInstance.serviceDateFormat));
		if (serviceInstance.getInactiveDate() != null)
			result.setInactiveDate(serviceInstance.getInactiveDate().toString(ServiceInstance.serviceDateFormat));
		return result;
	}

	public static final BillingService toBillingService(
			ServiceInstance serviceInstance) {
		BillingService result = new DefaultBillingService();
		result.setAccountNo(Integer.toString(serviceInstance.getAccount().getAccountNo()));
		result.setExternalId(serviceInstance.getExternalId());
		result.setServiceAddr(new DefaultCustAddress(serviceInstance.getAccount().getContact().getAddress()));
		result.setServiceName(new DefaultBillingName(serviceInstance.getAccount().getContact()));
		return result;
	}

}
