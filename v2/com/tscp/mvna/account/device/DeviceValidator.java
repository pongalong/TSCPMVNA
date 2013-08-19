package com.tscp.mvna.account.device;

import javax.xml.bind.annotation.XmlRootElement;

import com.tscp.mvna.Validator;
import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.device.network.NetworkStatus;
import com.tscp.mvna.account.kenan.provision.ServiceComponent;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvne.config.PROVISION;

@XmlRootElement
public class DeviceValidator extends Validator<DeviceAndService> {

	public DeviceValidator(DeviceAndService target) {
		super(target);
	}

	public void validate() {

		if (target == null) {
			addProblem(target, "Target is null");
			return;
		}

		NetworkInfo ni = target.getNetworkInfo();
		ServiceInstance si = target.getService();
		ServiceComponent sc = target.getService().getActiveComponent();

		// NULL checks
		if (ni == null)
			addProblem(ni, "NetworkInfo is null");

		// integrity check for reserved device
		if (ni != null && ni.getStatus() == NetworkStatus.R) {
			if (si == null)
				addProblem(si, "ServiceInstance is null, but NetworkInfo is in RESERVED");
			else if (si.getInactiveDate() != null)
				addProblem(si, "ServiceInstance is disconnected, but NetworkInfo is RESERVED");
			if (sc == null)
				addProblem(sc, "ServiceComponent is null, but NetworkInfo is in RESERVED");
			else {
				if (sc.getInactiveDate() != null)
					addProblem(sc, "ServiceComponent is disconnected, but NetworkInfo is RESERVED");
				if (!sc.isActiveType())
					addProblem(sc, "ServiceComponent is not of ACTIVE type, but NetworkInfo is RESERVED");
			}
		}

		// integrity check for active device
		if (ni != null && ni.getStatus() == NetworkStatus.A) {
			if (si == null)
				addProblem(si, "ServiceInstance is null, but NetworkInfo is in ACTIVE");
			else if (si.getInactiveDate() != null)
				addProblem(si, "ServiceInstance is disconnected, but NetworkInfo is ACTIVE");
			if (sc == null)
				addProblem(sc, "ServiceComponent is null, but NetworkInfo is in ACTIVE");
			else {
				if (sc.getInactiveDate() != null)
					addProblem(sc, "ServiceComponent is disconnected, but NetworkInfo is ACTIVE");
				if (!sc.isActiveType())
					addProblem(sc, "ServiceComponent is not of ACTIVE type, but NetworkInfo is ACTIVE");
			}
		}

		// integrity check for suspended device
		if (ni != null && ni.getStatus() == NetworkStatus.S) {
			if (si == null)
				addProblem(si, "ServiceInstance is null, but NetworkInfo is in SUSPEND");
			else if (si.getInactiveDate() != null)
				addProblem(si, "ServiceInstance is disconnected, but NetworkInfo is in SUSPEND");
			if (sc == null)
				addProblem(sc, "ServiceComponent is null, but NetworkInfo is in SUSPEND");
			else if (sc.getId() != PROVISION.COMPONENT.SUSPEND)
				addProblem(sc, "ServiceComponent is not of SUSPEND type, but NetworkInfo is in SUSPEND");
		}

		// integrity check for disconnected device
		if (ni != null && ni.getStatus() == NetworkStatus.C) {
			if (si != null)
				addProblem(si, "ServiceInstance is active, but NetworkInfo is disconnected");
			if (si != null && si.getInactiveDate() != null)
				addProblem(si, "ServiceInstance is active, but NetworkInfo is disconnected");
			if (sc != null)
				addProblem(sc, "ServiceComponent is active, but NetworkInfo is disconnected");
			if (sc != null && sc.getInactiveDate() != null)
				addProblem(sc, "ServiceComponent is active, but NetworkInfo is disconnected");
		}

	}

}