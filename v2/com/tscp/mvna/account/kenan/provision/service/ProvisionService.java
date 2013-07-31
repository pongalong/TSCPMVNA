package com.tscp.mvna.account.kenan.provision.service;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.ArrayOfComponentHolder;
import com.telscape.billingserviceinterface.ArrayOfPackageHolder;
import com.telscape.billingserviceinterface.ArrayOfServiceHolder;
import com.telscape.billingserviceinterface.ComponentHolder;
import com.telscape.billingserviceinterface.PackageHolder;
import com.telscape.billingserviceinterface.ServiceHolder;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.exception.AccountUpdateException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.mvna.account.kenan.service.KenanGatewayService;

/**
 * Base class for provisioning accounts with: ServiceIntances, Packages, Components
 * 
 * @author Jonathan
 * 
 */
public class ProvisionService extends KenanGatewayService {
	protected static final Logger logger = LoggerFactory.getLogger(ProvisionService.class);
	protected static final String USERNAME = ProvisionService.class.getSimpleName();
	protected static final short DISC_REASON = 5;

	/* **************************************************
	 * Server Response Validation Methods
	 */

	public static List<ServiceHolder> checkResponse(
			ArrayOfServiceHolder response) throws ProvisionException {
		if (response == null)
			throw new ProvisionException("No response received from server");
		if (response.getServiceHolder() == null || response.getServiceHolder().isEmpty())
			throw new ProvisionException("Null or empty response received from server");
		if (response.getServiceHolder().size() > 1)
			logger.warn("More than one active service found");

		for (ServiceHolder sh : response.getServiceHolder())
			checkResponse(sh);

		return response.getServiceHolder();
	}

	public static ServiceHolder checkResponse(
			ServiceHolder response) throws ProvisionException {
		if (response.getStatusMessage() == null)
			throw new ProvisionException("Null status message received from server");
		if (response.getStatusMessage().getStatus() == null || response.getStatusMessage().getStatus().isEmpty())
			throw new ProvisionException("Null or empty status message received from server");
		if (!response.getStatusMessage().getStatus().equalsIgnoreCase("SUCCESS"))
			if (response.getStatusMessage().getMessage() != null)
				throw new ProvisionException("Error from server " + getUrl() + ": " + response.getStatusMessage().getMessage());
			else
				throw new ProvisionException("Error from server " + getUrl());

		return response;
	}

	public static List<PackageHolder> checkResponse(
			ArrayOfPackageHolder response) throws ProvisionException {
		if (response == null)
			throw new ProvisionException("No response received from server");
		if (response.getPackageHolder() == null || response.getPackageHolder().isEmpty())
			throw new ProvisionException("Null or empty response received from server");
		if (response.getPackageHolder().size() > 1)
			logger.warn("More than one active package found");

		for (PackageHolder ph : response.getPackageHolder())
			checkResponse(ph);

		return response.getPackageHolder();
	}

	public static PackageHolder checkResponse(
			PackageHolder response) throws ProvisionException {
		if (response.getStatusMessage() == null)
			throw new ProvisionException("Null status message received from server");
		if (response.getStatusMessage().getStatus() == null || response.getStatusMessage().getStatus().isEmpty())
			throw new ProvisionException("Null or empty status message received from server");
		if (!response.getStatusMessage().getStatus().equalsIgnoreCase("SUCCESS"))
			if (response.getStatusMessage().getMessage() != null)
				throw new ProvisionException("Error from server " + getUrl() + ": " + response.getStatusMessage().getMessage());
			else
				throw new ProvisionException("Error from server " + getUrl());

		return response;
	}

	public static List<ComponentHolder> checkResponse(
			ArrayOfComponentHolder response) throws ProvisionFetchException {
		if (response == null)
			throw new ProvisionFetchException("No response received from server");
		// if (response.getComponentHolder() == null || response.getComponentHolder().isEmpty())
		if (response.getComponentHolder() == null)
			throw new ProvisionFetchException("Null or empty response received from server");
		if (response.getComponentHolder().size() > 1)
			logger.warn("More than one active component found");

		for (ComponentHolder ch : response.getComponentHolder())
			checkResponse(ch);

		return response.getComponentHolder();
	}

	public static ComponentHolder checkResponse(
			ComponentHolder response) throws ProvisionFetchException {
		if (response.getStatusMessage() == null)
			throw new ProvisionFetchException("Null status message received from server");
		if (response.getStatusMessage().getStatus() == null || response.getStatusMessage().getStatus().isEmpty())
			throw new ProvisionFetchException("Null or empty status message received from server");
		if (!response.getStatusMessage().getStatus().equalsIgnoreCase("SUCCESS"))
			if (response.getStatusMessage().getMessage() != null)
				throw new ProvisionFetchException("Error from server " + getUrl() + ": " + response.getStatusMessage().getMessage());
			else
				throw new ProvisionFetchException("Error from server " + getUrl());

		return response;
	}

	/* **************************************************
	 * Helper Methods
	 */

	/**
	 * WebService returns 1/1/1 for null dates.
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isNullDate(
			XMLGregorianCalendar date) {
		return date.getDay() == 1 && date.getYear() == 1 && date.getMonth() == 1;
	}

	/* **************************************************
	 * Status Update Methods
	 */

	/**
	 * Accounts with no active services eventually become inactive and must be reactivated before adding new services.
	 * 
	 * @param account
	 * @throws AccountUpdateException
	 */
	public static void reactivateAccount(
			KenanAccount account) throws AccountUpdateException {
		if (account == null || account.getAccountNo() == 0)
			throw new AccountUpdateException("Account is null or not specified");
		try {
			port.reactivateAccount(USERNAME, Integer.toString(account.getAccountNo()));
		} catch (Exception e) {
			logger.error("Unable to reactivate {}", account, e);
			throw new AccountUpdateException("Unable to reactivate Account " + account, e);
		}
	}

}