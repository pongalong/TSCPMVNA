package com.tscp.mvna.account.kenan.provision;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionFetchException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceConnectException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceDisconnectException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceIntegrityException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceRestoreException;
import com.tscp.mvna.account.kenan.provision.exception.ServiceSuspendException;
import com.tscp.mvna.account.kenan.provision.service.ServiceComponentProvisioner;
import com.tscp.mvna.account.kenan.provision.service.ServiceInstanceProvisioner;
import com.tscp.mvna.account.kenan.provision.service.ServicePackageProvisioner;
import com.tscp.mvne.config.PROVISION;

public class Service extends ServiceInstance {
	private static final long serialVersionUID = -5194651460015202188L;
	private static final Logger logger = LoggerFactory.getLogger(Service.class);

	public Service(KenanAccount account) {
		super(account);
	}

	public Service(NetworkInfo networkInfo) {
		super(networkInfo);
	}

	protected void refresh() {
		loaded = false;
		serviceComponents = null;
	}

	@Override
	public void load() {
		try {
			List<ServiceInstance> result = ServiceInstanceProvisioner.getActiveServices(account);
			if (result == null || result.isEmpty())
				logger.warn("{} has no active ServiceInstance", this);
			if (result.size() > 1)
				logger.warn("{} has more than one active ServicePackage", this);

			if (account == null || account.getAccountNo() != result.get(0).getAccount().getAccountNo())
				logger.warn("{} does not match returned Account {}", this, result.get(0).getAccount());

			account = result.get(0).getAccount();
			subscriberNo = result.get(0).getSubscriberNo();
			externalIdType = result.get(0).getExternalIdType();
			externalId = result.get(0).getExternalId();
			activeDate = result.get(0).getActiveDate();
			inactiveDate = result.get(0).getInactiveDate();
			serviceComponents = result.get(0).getServiceComponents();

			super.load();
		} catch (ProvisionFetchException e) {
			logger.error("Error loading ServiceInstances for {}", this, e);
		} finally {
			loaded = true;
		}
	}

	/* **************************************************
	 * Provisioning Methods
	 */

	public void integrityCheck() throws ServiceIntegrityException {
		if (account == null)
			throw new ServiceIntegrityException("Service has no account");
		if (serviceComponents == null)
			throw new ServiceIntegrityException("Sevice has no active ServiceComponent");
		if (serviceComponents.size() > 1)
			throw new ServiceIntegrityException("Sevice has more than one active ServiceComponent");
	}

	public void connect() throws ServiceIntegrityException, ServiceConnectException, ProvisionException {

		if (account == null || account.getAccountNo() == 0)
			throw new ServiceConnectException("Account must be provided");

		refresh();

		if (getActiveComponent() != null && getActiveComponent().getInactiveDate() != null)
			throw new ServiceConnectException("Account cannot be connected: Existing " + getActiveComponent() + " is not disconnected");

		ServiceInstance newServiceInstnace = ServiceInstanceProvisioner.addServiceInstance(this);
		ServicePackage newServicePackage = ServicePackageProvisioner.addPackage(new ServicePackage(account));
		ServiceComponent newServiceComponent;

		// TODO check last active date as there is no way of getting the previous component if it is inactive
		if (getActiveComponent() == null || !getActiveComponent().isCurrentMonth())
			newServiceComponent = new InstallComponent();
		else
			newServiceComponent = new ReinstallComponent();

		newServiceComponent.setServiceInstance(newServiceInstnace);
		newServiceComponent.setServicePackage(newServicePackage);
		newServiceComponent = ServiceComponentProvisioner.addInitialComponent(newServiceComponent);

		refresh();
	}

	public void disconnect() throws ServiceIntegrityException, ServiceDisconnectException, ProvisionException {
		integrityCheck();
		ServiceInstanceProvisioner.removeServiceInstance(this);
		refresh();
	}

	public void restore() throws ServiceIntegrityException, ServiceRestoreException, ProvisionException {
		integrityCheck();
		if (getActiveComponent().getId() != PROVISION.COMPONENT.SUSPEND)
			throw new ServiceRestoreException("Service cannot be restored: ServiceComponent is not SUSPEND");
		ServiceComponentProvisioner.removeComponent(getActiveComponent());
		ServiceComponentProvisioner.addComponent(getActiveComponent().isCurrentMonth() ? new ReinstallComponent(getActiveComponent().getServiceInstance()) : new InstallComponent(getActiveComponent().getServiceInstance()));
		refresh();
	}

	@XmlTransient
	public void suspend() throws ServiceIntegrityException, ServiceSuspendException, ProvisionException {
		integrityCheck();
		if (!getActiveComponent().isActiveType())
			throw new ServiceSuspendException("Service cannot be suspended: ServiceComponent is not ACTIVE");
		ServiceComponentProvisioner.removeComponent(getActiveComponent());
		ServiceComponentProvisioner.addFutureComponent(new SuspendComponent(getActiveComponent().getServiceInstance()));
		refresh();
	}

}