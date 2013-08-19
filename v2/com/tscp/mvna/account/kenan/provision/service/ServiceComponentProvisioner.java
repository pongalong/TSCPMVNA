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
import com.tscp.mvna.account.kenan.service.KenanAdapter;
import com.tscp.util.DateUtils;

public class ServiceComponentProvisioner extends Provisioner {
	protected static final Logger logger = LoggerFactory.getLogger(ServiceComponentProvisioner.class);
	protected static final String USERNAME = ServiceComponentProvisioner.class.getSimpleName();

	/* **************************************************
	 * Fetch Methods
	 */

	public static List<ServiceComponent> getActiveComponents(
			ServiceInstance serviceInstance) throws ProvisionFetchException {

		if (serviceInstance.getAccount() == null || serviceInstance.getAccount().getAccountNo() == 0)
			throw new ProvisionFetchException("Account must be provided");

		List<ServiceComponent> results = new ArrayList<ServiceComponent>();

		try {
			ServiceComponent temp;
			for (ComponentHolder componentHolder : checkResponse(port.getListActiveComponent(USERNAME, Integer.toString(serviceInstance.getAccount().getAccountNo()), serviceInstance.getExternalId()))) {
				temp = KenanAdapter.fromPkgComponent(componentHolder.getComponent());
				if (!temp.getServiceInstance().getExternalId().equals(serviceInstance.getExternalId()))
					logger.warn("{} does not match externalId on {}", temp, serviceInstance);
				else
					temp.setServiceInstance(serviceInstance);
				results.add(temp);
			}
		} catch (Exception e) {
			throw e;
		}

		return results;
	}

	/* **************************************************
	 * Add Methods
	 */

	public static ServiceComponent addInitialComponent(
			ServiceComponent serviceComponent) throws ProvisionException {
		return addComponent(serviceComponent, false, false);
	}

	public static ServiceComponent addFutureComponent(
			ServiceComponent serviceComponent) throws ProvisionException {
		return addComponent(serviceComponent, true, true);
	}

	public static ServiceComponent addComponent(
			ServiceComponent serviceComponent) throws ProvisionException {
		return addComponent(serviceComponent, false, true);
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
			ServiceComponent serviceComponent, boolean nextDay, boolean single) throws ProvisionException {

		if (nextDay)
			serviceComponent.setActiveDate(new DateTime().plusDays(1));

		logger.debug("Adding {} with Date [{}]", serviceComponent, serviceComponent.getActiveDate().toLocalDate());

		ArrayOfPkgComponent componentList = buildPkgComponentList(serviceComponent);

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
			ServiceComponent serviceComponent) throws ProvisionException {

		DateTime inactiveDate = new DateTime();

		if (serviceComponent.isNextDay())
			serviceComponent.setInactiveDate(new DateTime().plusDays(1));
		if (serviceComponent.getInactiveDate() == null || serviceComponent.getInactiveDate().isBeforeNow())
			serviceComponent.setInactiveDate(inactiveDate);

		logger.debug("Removing {} with Date [{}]", serviceComponent, serviceComponent.getInactiveDate().toLocalDate());

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

	/* **************************************************
	 * Adapter Methods
	 */

	static ArrayOfPkgComponent buildPkgComponentList(
			ServiceComponent serviceComponent) {
		ArrayOfPkgComponent result = new ArrayOfPkgComponent();
		result.getPkgComponent().add(KenanAdapter.toPkgComponent(serviceComponent));
		return result;
	}

}