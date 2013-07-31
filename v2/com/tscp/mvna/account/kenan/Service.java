package com.tscp.mvna.account.kenan;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.provision.InstallComponent;
import com.tscp.mvna.account.kenan.provision.ReinstallComponent;
import com.tscp.mvna.account.kenan.provision.ServiceComponent;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.ServicePackage;
import com.tscp.mvna.account.kenan.provision.SuspendComponent;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceConnectException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceDisconnectException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceIntegrityException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceRestoreException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceSuspendException;
import com.tscp.mvna.account.kenan.provision.service.ProvisionServiceComponentService;
import com.tscp.mvna.account.kenan.provision.service.ProvisionServiceInstanceService;
import com.tscp.mvna.account.kenan.provision.service.ProvisionServicePackageService;
import com.tscp.mvne.config.PROVISION;

@XmlRootElement
public class Service {
	protected static final Logger logger = LoggerFactory.getLogger(Service.class);
	protected KenanAccount account;
	protected List<ServiceInstance> serviceInstances;
	protected List<ServicePackage> servicePackages;
	protected List<ServiceComponent> serviceComponents;
	protected boolean loadedServiceInstances;
	protected boolean loadedServicePackages;
	protected boolean loadedServiceComponents;

	protected Service() {
		// do nothing
	}

	public Service(ServiceInstance newServiceInstance) throws ServiceConnectException {
		if (newServiceInstance.getAccount() == null)
			throw new ServiceConnectException("Service cannot be created: Account is null");

		account = newServiceInstance.getAccount();
		serviceInstances = new ArrayList<ServiceInstance>();
		serviceInstances.add(newServiceInstance);
	}

	/* **************************************************
	 * Provision Methods
	 */

	@XmlTransient
	public void connect(
			ServiceInstance newServiceInstance) throws ServiceConnectException, ProvisionException {

		if (newServiceInstance == null)
			throw new ServiceConnectException("Account cannot be connected: ServiceInstance is null");

		// force loading of services for integrity
		refresh();

		if (hasServiceInstances()) {
			for (ServiceInstance si : getServiceInstances())
				if (si.getExternalId().equals(newServiceInstance.getExternalId()))
					throw new ServiceConnectException(this + " already contains ServiceInstance " + newServiceInstance.getExternalId());
			throw new ServiceConnectException(this + " already has an active ServiceInstance " + getActiveServiceInstance());
		}

		if (getActiveServiceComponent() != null && getActiveServiceComponent().getInactiveDate() != null)
			throw new ServiceConnectException("Account cannot be connected: Existing " + getActiveServiceComponent() + " is not disconnected");

		ServicePackage newServicePackage;
		ServiceComponent newServiceComponent;

		// TODO still need to check last active date as there is no way of getting the previous component if it is
		// inactive
		if (getActiveServiceComponent() == null || !getActiveServiceComponent().isCurrentMonth())
			newServiceComponent = new InstallComponent();
		else
			newServiceComponent = new ReinstallComponent();

		newServiceInstance = ProvisionServiceInstanceService.addServiceInstance(account, newServiceInstance);
		newServicePackage = ProvisionServicePackageService.addPackage(account.getAccountNo(), new ServicePackage());
		newServiceComponent.setExternalId(newServiceInstance.getExternalId());
		newServiceComponent.setServicePackage(newServicePackage);
		newServiceComponent = ProvisionServiceComponentService.addInitialComponent(newServiceComponent);
	}

	@XmlTransient
	public void disconnect() throws ServiceIntegrityException, ServiceDisconnectException, ProvisionException {
		sanityCheck();
		ProvisionServiceInstanceService.removeServiceInstance(account, getActiveServiceInstance());
	}

	/**
	 * Removes the current ServiceComponent and adds a RestoreComponent. If the current ServiceComponent has an active
	 * date of the current month, no MRC should be charged and a ReinstallComponent will be provisioned.
	 * 
	 * @throws ServiceIntegrityException
	 * @throws ServiceRestoreException
	 * @throws ProvisionException
	 */
	@XmlTransient
	public void restore() throws ServiceIntegrityException, ServiceRestoreException, ProvisionException {

		sanityCheck();

		if (getActiveServiceComponent().getId() != PROVISION.COMPONENT.SUSPEND)
			throw new ServiceRestoreException("Service cannot be restored: Current ServiceComponent is not SUSPEND");

		ServiceComponent restoreComponent = getActiveServiceComponent().isCurrentMonth() ? new ReinstallComponent(getActiveServiceComponent()) : new InstallComponent(getActiveServiceComponent());

		ProvisionServiceComponentService.removeComponent(getActiveServiceComponent());
		ProvisionServiceComponentService.addComponent(restoreComponent);
	}

	/**
	 * Removes the current ServiceComponent and adds a SuspendComponent with tomorrow as an active date. This allows us to
	 * capture and rate any incoming usage.
	 * 
	 * @throws ServiceIntegrityException
	 * @throws ServiceSuspendException
	 * @throws ProvisionException
	 */
	@XmlTransient
	public void suspend() throws ServiceIntegrityException, ServiceSuspendException, ProvisionException {

		sanityCheck();

		if (getActiveServiceComponent().getId() != PROVISION.COMPONENT.INSTALL && getActiveServiceComponent().getId() != PROVISION.COMPONENT.REINSTALL)
			throw new ServiceSuspendException("Service cannot be suspended: Current ServiceComponent is not ACTIVE");

		ProvisionServiceComponentService.removeComponent(getActiveServiceComponent());
		ProvisionServiceComponentService.addFutureComponent(new SuspendComponent(getActiveServiceComponent()));
	}

	/* **************************************************
	 * Integrity Methods
	 */

	@XmlTransient
	public void sanityCheck() throws ServiceIntegrityException {
		if (getActiveServiceInstance() == null)
			throw new ServiceIntegrityException("Sevice has no active ServiceInstance");
		if (getActiveServicePackage() == null)
			throw new ServiceIntegrityException("Service has no active ServicePackage");
		if (getActiveServiceComponent() == null)
			throw new ServiceIntegrityException("Service has no active ServiceComponent");
		if (getServiceComponents().size() > 1)
			throw new ServiceIntegrityException("Service has more than one active component");
	}

	/* **************************************************
	 * Fetch Methods
	 */

	public void refresh() {
		loadedServiceInstances = false;
		loadedServicePackages = false;
		loadedServiceComponents = false;
		serviceInstances = loadServiceInstances();
		servicePackages = loadServicePackages();
		serviceComponents = loadServiceComponents();
	}

	@XmlTransient
	protected KenanAccount getAccount() {
		return account;
	}

	protected void setAccount(
			KenanAccount account) {
		this.account = account;
	}

	protected List<ServiceComponent> loadServiceComponents() {
		try {
			if (getServiceInstances() == null)
				return null;

			List<ServiceComponent> result = new ArrayList<ServiceComponent>();
			List<ServiceComponent> temp;

			for (ServiceInstance si : getServiceInstances()) {
				temp = ProvisionServiceComponentService.getActiveComponents(account.getAccountNo(), si);
				if (temp != null)
					result.addAll(temp);
			}
			if (result.isEmpty())
				logger.warn("{} has no active ServiceComponent", this);
			return result;
		} catch (ProvisionFetchException e) {
			return null;
		} finally {
			loadedServiceComponents = true;
		}
	}

	protected List<ServicePackage> loadServicePackages() {
		try {
			if (getServiceInstances() == null)
				return null;

			List<ServicePackage> result = ProvisionServicePackageService.getActivePackages(account.getAccountNo());
			if (result != null)
				if (result == null || result.isEmpty())
					logger.warn("{} has no active ServicePackage", this);
			return result;
		} catch (ProvisionFetchException e) {
			return null;
		} finally {
			loadedServicePackages = true;
		}
	}

	protected List<ServiceInstance> loadServiceInstances() {
		try {
			List<ServiceInstance> result = ProvisionServiceInstanceService.getActiveServices(account.getAccountNo());
			if (result != null)
				for (ServiceInstance si : result) {
					logger.debug("loaded {} and setting account as {}", si, account);
					si.setAccount(account);
				}
			return result;
		} catch (ProvisionFetchException e) {
			logger.error("Unable to fetch ServiceInstances for {}", this);
			return null;
		} finally {
			loadedServiceInstances = true;
		}
	}

	/* **************************************************
	 * Getter and Setters: These are protected and viewable by KenanAccount, which can have any number of ServiceInstance,
	 * ServicePackage and ServiceComponent
	 */

	@XmlTransient
	protected List<ServicePackage> getServicePackages() {
		if (servicePackages == null && !loadedServicePackages)
			servicePackages = loadServicePackages();
		return servicePackages;
	}

	protected void setServicePackages(
			List<ServicePackage> servicePackages) {
		this.servicePackages = servicePackages;
	}

	@XmlTransient
	protected List<ServiceInstance> getServiceInstances() {
		if (serviceInstances == null && !loadedServiceInstances)
			serviceInstances = loadServiceInstances();
		return serviceInstances;
	}

	protected void setServiceInstances(
			List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}

	@XmlTransient
	protected List<ServiceComponent> getServiceComponents() {
		if (serviceComponents == null && !loadedServiceComponents)
			serviceComponents = loadServiceComponents();
		return serviceComponents;
	}

	protected void setServiceComponents(
			List<ServiceComponent> serviceComponents) {
		this.serviceComponents = serviceComponents;
	}

	/* **************************************************
	 * Public Methods: These are viewable by Account, which should only have 1 ServiceInstance, 1 ServicePackage and 1
	 * ServiceComponent
	 */

	@XmlElement
	public ServiceComponent getActiveServiceComponent() {
		getServiceComponents();
		if (hasServiceComponents())
			return getServiceComponents().get(0);
		return null;
	}

	@XmlElement
	public ServicePackage getActiveServicePackage() {
		getServicePackages();
		if (hasServicePackages())
			return getServicePackages().get(0);
		return null;
	}

	@XmlElement
	public ServiceInstance getActiveServiceInstance() {
		getServiceInstances();
		if (hasServiceInstances()) {
			return getServiceInstances().get(0);
		}
		return null;
	}

	@XmlTransient
	public boolean hasServiceComponents() {
		return serviceComponents != null && !serviceComponents.isEmpty();
	}

	@XmlTransient
	public boolean hasServicePackages() {
		return servicePackages != null && !servicePackages.isEmpty();
	}

	@XmlTransient
	public boolean hasServiceInstances() {
		return serviceInstances != null && !serviceInstances.isEmpty();
	}

}