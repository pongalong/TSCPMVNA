package com.tscp.mvna.account.kenan.provision.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.ServiceHolder;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.product.ServiceInstanceV2;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.util.DateUtils;

public class ProvisionServiceInstanceService extends ProvisionService {
	protected static final Logger logger = LoggerFactory.getLogger(ProvisionServiceInstanceService.class);
	protected static final String USERNAME = ProvisionServiceInstanceService.class.getSimpleName();

	/* **************************************************
	 * Fetch Methods
	 */

	public static List<ServiceInstanceV2> properGetActiveServices(
			int accountNo) throws ProvisionFetchException {

		List<ServiceInstanceV2> results = new ArrayList<ServiceInstanceV2>();

		try {
			for (ServiceHolder serviceHolder : checkResponse(port.getActiveService(USERNAME, Integer.toString(accountNo))))
				results.add(ServiceInstanceV2.fromService(serviceHolder.getService()));
			return results;
		} catch (ProvisionException e) {
			return null;
		}
	}

	public static List<ServiceInstance> getActiveServices(
			int accountNo) throws ProvisionFetchException {

		List<ServiceInstance> results = new ArrayList<ServiceInstance>();

		try {
			for (ServiceHolder serviceHolder : checkResponse(port.getActiveService(USERNAME, Integer.toString(accountNo))))
				results.add(ServiceInstance.fromService(serviceHolder.getService()));
			return results;
		} catch (ProvisionException e) {
			return null;
		}
	}

	/* **************************************************
	 * Add Methods
	 */

	public static ServiceInstance addServiceInstance(
			KenanAccount account, ServiceInstance serviceInstance) throws ProvisionException {

		logger.debug("Connecting {} to {}", serviceInstance, account);

		if (account == null || account.getAccountNo() == 0)
			throw new ProvisionException("Account is null or not specified");
		if (serviceInstance == null || serviceInstance.isEmpty())
			throw new ProvisionException("ServiceInstnace is null or not specified");

		reactivateAccount(account);

		try {
			checkResponse(port.addService(USERNAME, ServiceInstance.toBillingService(serviceInstance, account)));
			return serviceInstance;
		} catch (Exception e) {
			logger.error("Unable to connect {} with {}", account, serviceInstance, e);
			throw new ProvisionException(e);
		}

	}

	/* **************************************************
	 * Remove Methods
	 */

	public static void removeServiceInstance(
			KenanAccount account, ServiceInstance serviceInstance) throws ProvisionException {

		logger.debug("Disconnecting {} from {}", serviceInstance, account);

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