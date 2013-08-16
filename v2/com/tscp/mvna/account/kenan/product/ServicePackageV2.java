package com.tscp.mvna.account.kenan.product;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.ServicePackage;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.mvna.account.kenan.provision.service.ProvisionServiceInstanceService;

public class ServicePackageV2 extends ServicePackage {
	private static final long serialVersionUID = -7156147106438456155L;
	protected static final Logger logger = LoggerFactory.getLogger(ServicePackageV2.class);
	protected KenanAccount account;
	protected List<ServiceInstanceV2> serviceInstances;

	/* **************************************************
	 * Constructors
	 */

	public ServicePackageV2(KenanAccount account) {
		this.account = account;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	public void load() {
		super.reset();
		if (serviceInstances == null)
			serviceInstances = new ArrayList<ServiceInstanceV2>();
		serviceInstances.clear();
		serviceInstances.addAll(loadValue());
	}

	@Override
	protected List<ServiceInstanceV2> loadValue() {
		try {
			List<ServiceInstanceV2> result = ProvisionServiceInstanceService.properGetActiveServices(account.getAccountNo());
			// for (ServiceInstanceV2 si : result)
			// si.setAccount(account);
		} catch (ProvisionFetchException e) {
			logger.error("Error loading ServiceInstances for {}", this);
		} finally {
			loaded = true;
		}

		return null;
	}

	/* **************************************************
	 * Getter and Setter Methods
	 */

	public KenanAccount getAccount() {
		return account;
	}

	public void setAccount(
			KenanAccount account) {
		this.account = account;
	}

	public List<ServiceInstanceV2> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(
			List<ServiceInstanceV2> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}

}