package com.tscp.mvna.account.kenan.provision.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.ArrayOfPackage;
import com.telscape.billingserviceinterface.PackageHolder;
import com.telscape.billingserviceinterface.ValueHolder;
import com.tscp.mvna.account.kenan.exception.KenanException;
import com.tscp.mvna.account.kenan.provision.ServicePackage;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.util.DateUtils;

public class ProvisionServicePackageService extends ProvisionService {
	protected static final Logger logger = LoggerFactory.getLogger(ProvisionServicePackageService.class);
	protected static final String USERNAME = ProvisionServicePackageService.class.getSimpleName();

	/* **************************************************
	 * Fetch Methods
	 */

	public static List<ServicePackage> getActivePackages(
			int accountNo) throws ProvisionFetchException {
		if (accountNo == 0)
			throw new ProvisionFetchException("Account number must be provided");

		try {
			List<ServicePackage> results = new ArrayList<ServicePackage>();
			for (PackageHolder packageHolder : checkResponse(port.getListActivePackages(USERNAME, Integer.toString(accountNo))))
				results.add(new ServicePackage(packageHolder.getPackage()));
			return results;
		} catch (ProvisionException e) {
			return null;
		}
	}

	/* **************************************************
	 * Add Methods
	 */

	/**
	 * Adds the {@link ServicePackage} to the given account.
	 * 
	 * @param accountNo
	 * @param servicePackage
	 * @throws ProvisionException
	 */
	public static ServicePackage addPackage(
			int accountNo, ServicePackage servicePackage) throws ProvisionException {
		if (accountNo == 0)
			throw new ProvisionException("Account number must be provided");

		if (servicePackage == null)
			servicePackage = new ServicePackage();
		if (servicePackage.getAccountNo() == 0)
			servicePackage.setAccountNo(accountNo);

		try {
			ValueHolder valueHolder = checkResponse(port.addPackage(USERNAME, toBillingPackageList(servicePackage)));
			servicePackage.setInstanceId(Integer.parseInt(valueHolder.getValue()));
			// value2 holds the instance_id_serv which determines wireline or wireless
			// servicePackage.setInstanceIdServ(Integer.parseInt(valueHolder.getValue2()));
			return servicePackage;
		} catch (Exception e) {
			throw new ProvisionException(e);
		}

	}

	/* **************************************************
	 * Remove Methods
	 */

	/**
	 * Removes the {@link ServicePackage} from the account. This method is protected as it is not needed in the current
	 * business rules.
	 * 
	 * @param accountNo
	 * @param servicePackage
	 * @throws ProvisionException
	 */
	protected static void removePackage(
			int accountNo, ServicePackage servicePackage) throws ProvisionException {
		if (accountNo == 0)
			throw new ProvisionException("Account number must be provided");
		if (servicePackage == null || servicePackage.getInstanceId() == 0)
			throw new ProvisionException("ServicePackage is null or not specified");

		ArrayOfPackage arrayOfPackages = toBillingPackageList(servicePackage);
		for (com.telscape.billingserviceinterface.Package pkg : arrayOfPackages.getPackage()) {
			pkg.setDiscReason(DISC_REASON);
			pkg.setDiscDate(DateUtils.getXMLCalendar());
		}

		try {
			checkResponse(port.disconnectPackage(USERNAME, arrayOfPackages));
		} catch (KenanException e) {
			logger.error("Exception removing {}", servicePackage, e);
			throw new ProvisionException("Error removing " + servicePackage + ": " + e.getMessage());
		}
	}

	/* **************************************************
	 * Adapter Methods
	 */

	public static ArrayOfPackage toBillingPackageList(
			ServicePackage servicePackage) {
		ArrayOfPackage result = new ArrayOfPackage();
		result.getPackage().add(servicePackage.toBillingPackage());
		return result;
	}

}
