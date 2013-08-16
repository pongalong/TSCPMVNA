package com.tscp.mvna.account.kenan.provision.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.ArrayOfPkgComponent;
import com.telscape.billingserviceinterface.ComponentHolder;
import com.telscape.billingserviceinterface.PkgComponent;
import com.tscp.mvna.account.kenan.product.ServiceComponentV2;
import com.tscp.mvna.account.kenan.product.ServiceInstanceV2;
import com.tscp.mvna.account.kenan.provision.ServiceComponent;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.ServicePackage;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.util.DateUtils;

public class ProvisionServiceComponentService extends ProvisionService {
	protected static final Logger logger = LoggerFactory.getLogger(ProvisionServiceComponentService.class);
	protected static final String USERNAME = ProvisionServiceComponentService.class.getSimpleName();

	/* **************************************************
	 * Fetch Methods
	 */

	public static List<ServiceComponentV2> properGetActiveComponents(
			int accountNo, ServiceInstanceV2 serviceInstance) throws ProvisionFetchException {

		if (accountNo == 0)
			throw new ProvisionFetchException("Account number must be provided");

		List<ServiceComponentV2> results = new ArrayList<ServiceComponentV2>();

		try {
			for (ComponentHolder componentHolder : checkResponse(port.getListActiveComponent(USERNAME, Integer.toString(accountNo), serviceInstance.getExternalId())))
				results.add(new ServiceComponentV2(ServiceComponent.fromPkgComponent(componentHolder.getComponent()), serviceInstance));
		} catch (Exception e) {
			throw e;
		}

		return results;
	}

	public static List<ServiceComponent> getActiveComponents(
			int accountNo, ServiceInstance serviceInstance) throws ProvisionFetchException {

		if (accountNo == 0)
			throw new ProvisionFetchException("Account number must be provided");

		List<ServiceComponent> results = new ArrayList<ServiceComponent>();

		try {
			for (ComponentHolder componentHolder : checkResponse(port.getListActiveComponent(USERNAME, Integer.toString(accountNo), serviceInstance.getExternalId())))
				results.add(ServiceComponent.fromPkgComponent(componentHolder.getComponent()));
		} catch (Exception e) {
			throw e;
		}

		return results;
	}

	/* **************************************************
	 * Add Methods
	 */

	public static ServiceComponent addInitialComponent(
			ServiceComponent serviceComponent, ServicePackage servicePackage) throws ProvisionException {
		return addComponent(serviceComponent, servicePackage, false, false);
	}

	public static ServiceComponent addFutureComponent(
			ServiceComponent serviceComponent, ServicePackage servicePackage) throws ProvisionException {
		return addComponent(serviceComponent, servicePackage, true, true);
	}

	public static ServiceComponent addComponent(
			ServiceComponent serviceComponent, ServicePackage servicePackage) throws ProvisionException {
		return addComponent(serviceComponent, servicePackage, false, true);
	}

	/**
	 * Adds a {@link ServiceComponent} to the given {@link ServicePackage}. If flag 'single' is true then no other
	 * components will be activated; if false then it will activate the ServicePackage and all other components within the
	 * package.
	 * 
	 * @param serviceComponent
	 * @return
	 * @throws ProvisionException
	 */
	protected static ServiceComponent addComponent(
			ServiceComponent serviceComponent, ServicePackage servicePackage, boolean nextDay, boolean single) throws ProvisionException {

		if (nextDay)
			serviceComponent.setActiveDate(new DateTime().plusDays(1));

		logger.debug("Adding {} with Date [{}]", serviceComponent, serviceComponent.getActiveDate().toLocalDate());

		ArrayOfPkgComponent componentList = buildPkgComponentList(serviceComponent, servicePackage);

		try {
			if (single)
				checkResponse(port.insertSingleComponent(USERNAME, componentList));
			else
				checkResponse(port.addComponent(USERNAME, componentList));
			return serviceComponent;
		} catch (Exception e) {
			logger.error("BillServer error, adding {}", serviceComponent, e);
			throw new ProvisionException("Error adding component " + serviceComponent + ": " + e.getMessage());
		}
	}

	/* **************************************************
	 * Remove Methods
	 */

	/**
	 * {@link ServiceComponent} from the given {@link ServicePackage}
	 * 
	 * @param serviceComponent
	 * @param servicePackage
	 * @throws ProvisionException
	 */
	public static void removeComponent(
			ServiceComponent serviceComponent, ServicePackage servicePackage) throws ProvisionException {

		DateTime inactiveDate = new DateTime();

		if (serviceComponent.isTommorrow())
			serviceComponent.setInactiveDate(new DateTime().plusDays(1));
		if (serviceComponent.getInactiveDate() == null || serviceComponent.getInactiveDate().isBeforeNow())
			serviceComponent.setInactiveDate(inactiveDate);

		logger.debug("Removing {} with Date [{}]", serviceComponent, serviceComponent.getInactiveDate().toLocalDate());

		ArrayOfPkgComponent componentList = buildPkgComponentList(serviceComponent, servicePackage);
		for (PkgComponent pkgComponent : componentList.getPkgComponent()) {
			pkgComponent.setDiscReason(DISC_REASON);
			pkgComponent.setDiscDate(DateUtils.getXMLCalendar(serviceComponent.getInactiveDate()));
		}

		try {
			checkResponse(port.disconnectComponent(USERNAME, componentList));
		} catch (Exception e) {
			logger.error("Exception removing {}", serviceComponent, e);
			throw new ProvisionException("Error removing component " + serviceComponent + ": " + e.getMessage());
		}

	}

	/* **************************************************
	 * Adapter Methods
	 */

	static ArrayOfPkgComponent buildPkgComponentList(
			ServiceComponent serviceComponent, ServicePackage servicePackage) {
		ArrayOfPkgComponent result = new ArrayOfPkgComponent();
		result.getPkgComponent().add(ServiceComponent.toPkgComponent(serviceComponent, servicePackage));
		return result;
	}

}