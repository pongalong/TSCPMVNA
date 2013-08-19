package com.tscp.mvna.account.kenan.provision.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.ServiceHolder;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.mvna.account.kenan.service.KenanAdapter;
import com.tscp.util.DateUtils;

public class ServiceInstanceProvisioner extends Provisioner {
	protected static final Logger logger = LoggerFactory.getLogger(ServiceInstanceProvisioner.class);
	protected static final String USERNAME = ServiceInstanceProvisioner.class.getSimpleName();

	/* **************************************************
	 * Fetch Methods
	 */

	public static List<ServiceInstance> getActiveServices(
			KenanAccount account) throws ProvisionFetchException {

		if (account == null || account.getAccountNo() == 0)
			throw new ProvisionFetchException("Account must be provided");

		List<ServiceInstance> results = new ArrayList<ServiceInstance>();
		ServiceInstance temp;

		try {
			for (ServiceHolder serviceHolder : checkResponse(port.getActiveService(USERNAME, Integer.toString(account.getAccountNo())))) {
				temp = KenanAdapter.fromService(serviceHolder.getService());
				if (temp.getAccount().getAccountNo() != account.getAccountNo())
					logger.warn("{} does not have matching account {}", temp, account);
				temp.setAccount(account);
				results.add(temp);
			}
			return results;
		} catch (ProvisionException e) {
			return null;
		}
	}

	/* **************************************************
	 * Add Methods
	 */

	public static ServiceInstance addServiceInstance(
			ServiceInstance serviceInstance) throws ProvisionException {

		logger.debug("Connecting {}", serviceInstance);

		if (serviceInstance.getAccount() == null || serviceInstance.getAccount().getAccountNo() == 0)
			throw new ProvisionException("Account is null or not specified");
		if (serviceInstance == null || serviceInstance.isEmpty())
			throw new ProvisionException("ServiceInstnace is null or not specified");

		reactivateAccount(serviceInstance.getAccount());

		try {
			checkResponse(port.addService(USERNAME, KenanAdapter.toBillingService(serviceInstance)));
			return serviceInstance;
		} catch (Exception e) {
			logger.error("Unable to connect {}", serviceInstance, e);
			throw new ProvisionException(e);
		}

	}

	/* **************************************************
	 * Remove Methods
	 */

	public static void removeServiceInstance(
			ServiceInstance serviceInstance) throws ProvisionException {

		logger.debug("Disconnecting {}", serviceInstance);

		KenanAccount account = serviceInstance.getAccount();

		if (account == null || account.getAccountNo() == 0)
			throw new ProvisionException("Account is null or not specified");
		if (serviceInstance == null || serviceInstance.isEmpty())
			throw new ProvisionException("ServiceInstnace is null or not specified");

		DateTime discDate = new DateTime().plusDays(1);

		try {
			checkResponse(port.disconnectServicePackages(USERNAME, Integer.toString(account.getAccountNo()), serviceInstance.getExternalId(), serviceInstance.getExternalIdType(), DateUtils.getXMLCalendar(discDate), DISC_REASON));
		} catch (Exception e) {
			logger.error("Error disconnecting {} from {}", serviceInstance, account, e);
			throw new ProvisionException(e);
		}
	}

}