package com.tscp.mvna.account.kenan.provision.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.ArrayOfPkgComponent;
import com.telscape.billingserviceinterface.ComponentHolder;
import com.telscape.billingserviceinterface.PkgComponent;
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

	public static List<ServiceComponent> getActiveComponents(
			int accountNo, ServiceInstance serviceInstance) throws ProvisionFetchException {
		if (accountNo == 0)
			throw new ProvisionFetchException("Account number must be provided");

		List<ServiceComponent> results = new ArrayList<ServiceComponent>();

		try {
			for (ComponentHolder componentHolder : checkResponse(port.getListActiveComponent(USERNAME, Integer.toString(accountNo), serviceInstance.getExternalId())))
				results.add(new ServiceComponent(componentHolder.getComponent()));
			return results;
		} catch (ProvisionException e) {
			return null;
		}
	}

	/* **************************************************
	 * Add Methods
	 */

	/**
	 * Adds a {@link ServiceComponent} to the given {@link ServicePackage} activating the ServicePackage and all other
	 * components within the package.
	 * 
	 * @param serviceComponent
	 * @return
	 * @throws ProvisionException
	 */
	public static ServiceComponent addComponent(
			ServiceComponent serviceComponent) throws ProvisionException {

		logger.debug("Adding {} with Date [{}]", serviceComponent, serviceComponent.getActiveDate().toLocalDate());

		ArrayOfPkgComponent componentList = buildPkgComponentList(serviceComponent);

		try {
			checkResponse(port.addComponent(USERNAME, componentList));
			return serviceComponent;
		} catch (Exception e) {
			logger.error("BillServer error, adding {}", serviceComponent, e);
			throw new ProvisionException("Error adding component " + serviceComponent + ": " + e.getMessage());
		}

	}

	/**
	 * Adds a {@link ServiceComponent} to the given {@link ServicePackage} WITHOUT re-activating all other components
	 * within the package.
	 * 
	 * @param serviceComponent
	 * @param servicePackage
	 * @throws ProvisionException
	 */
	public static ServiceComponent addSingleComponent(
			ServiceComponent serviceComponent) throws ProvisionException {

		logger.debug("Adding {} with Date [{}]", serviceComponent, serviceComponent.getActiveDate().toLocalDate());

		ArrayOfPkgComponent componentList = buildPkgComponentList(serviceComponent);

		try {
			checkResponse(port.insertSingleComponent(USERNAME, componentList));
			return serviceComponent;
		} catch (Exception e) {
			logger.error("BillServer error, adding {}", serviceComponent, e);
			throw new ProvisionException("Error adding component " + serviceComponent + ": " + e.getMessage());
		}

	}

	/**
	 * Adds a {@link ServiceComponent} to the given {@link ServicePackage} with a future date. This allows any remaining
	 * usage for the component to be caught and rated.
	 * 
	 * @param serviceComponent
	 * @param servicePackage
	 * @throws ProvisionException
	 */
	public static ServiceComponent addSingleComponentNextDay(
			ServiceComponent serviceComponent) throws ProvisionException {
		serviceComponent.setActiveDate(new DateTime().plusDays(1));
		return addSingleComponent(serviceComponent);
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
			ServiceComponent serviceComponent) throws ProvisionException {

		logger.debug("Removing {} with Date [{}]", serviceComponent, serviceComponent.getInactiveDate().toLocalDate());

		if (serviceComponent.getInactiveDate() == null || serviceComponent.getInactiveDate().isBeforeNow())
			serviceComponent.setInactiveDate(new DateTime());

		ArrayOfPkgComponent componentList = buildPkgComponentList(serviceComponent);
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

	/**
	 * Removes a {@link ServiceComponent} from the given {@link ServicePackage} with a future date. This allows any
	 * remaining usage for the component to be caught and rated.
	 * 
	 * @param component
	 * @param servicePackage
	 * @throws ProvisionException
	 */
	public static void removeComponentNextDay(
			ServiceComponent component) throws ProvisionException {
		component.setInactiveDate(new DateTime().plusDays(1));
		removeComponent(component);
	}

	/* **************************************************
	 * Adapter Methods
	 */

	static ArrayOfPkgComponent buildPkgComponentList(
			ServiceComponent serviceComponent) {
		ArrayOfPkgComponent result = new ArrayOfPkgComponent();
		result.getPkgComponent().add(serviceComponent.toPkgComponent());
		return result;
	}

}