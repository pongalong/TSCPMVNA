package com.tscp.mvna.account.device;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.device.network.NetworkStatus;
import com.tscp.mvna.account.kenan.provision.ServiceComponent;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.provision.ServicePackage;
import com.tscp.mvne.config.PROVISION;

//TODO change problemObject and description to a List to provide multiple errors
@XmlRootElement
public class DeviceIntegrity {
	private boolean sane;
	private Object problemObject;
	private String description;

	/* **************************************************
	 * Sanity Check Methods
	 */

	@XmlTransient
	public void sanityCheck(
			DeviceAndService device) {

		NetworkInfo ni = device.getNetworkInfo();
		ServiceInstance si = device.getService().getActiveServiceInstance();
		ServicePackage sp = device.getService().getActiveServicePackage();
		ServiceComponent sc = device.getService().getActiveServiceComponent();

		// NULL checks
		if (ni == null) {
			description = "NetworkInfo is null";
			return;
		} else if (ni.getStatus() == NetworkStatus.C) {
			if (si != null) {
				description = "NetworkInfo is disconnected but ServiceInstance still exsists";
				return;
			}
			if (sp != null) {
				description = "NetworkInfo is disconnected but ServicePackage still exsists";
				return;
			}
			if (sc != null) {
				description = "NetworkInfo is disconnected but ServiceComponent still exsists";
				return;
			}
		} else {

			if (si == null) {
				description = "ServiceInstance is null";
				return;
			}

			if (sp == null) {
				description = "ServicePackage is null";
				return;
			}

			if (sc == null) {
				description = "ServiceComponent is null";
				return;
			}

			if (sc.getServicePackage().getInstanceId() != sp.getInstanceId()) {
				description = "ServiceComponent is not associated with ServicePackage";
				return;
			}

			// MDN check
			if (!si.getExternalId().equals(ni.getMdn())) {
				description = "ServiceInstance MDN does not match NetworkInfo MDN";
				problemObject = si;
				return;
			}

			// Component empty check
			// if (sp.getComponentList() == null || sp.getComponentList().isEmpty()) {
			// description = "ServicePackage has no ServiceComponents";
			// problemObject = sp;
			// return;
			// }

			// integrity check for active device
			if (ni.getStatus() == NetworkStatus.A) {
				if (si.getInactiveDate() != null) {
					description = "ServiceInstance is disconnected, but NetworkInfo is active";
					problemObject = si;
					return;
				}
				if (sc.getId() != PROVISION.COMPONENT.INSTALL && sc.getId() != PROVISION.COMPONENT.REINSTALL) {
					description = "ServiceComponent is not of active type, but NetworkInfo is active";
					problemObject = sc;
					return;
				}
			}

			// integrity check for suspended device
			if (ni.getStatus() == NetworkStatus.S) {
				if (si.getInactiveDate() != null) {
					description = "ServiceInstance is disconnected, but NetworkInfo is suspended";
					problemObject = si;
					return;
				}
				if (sc.getId() != PROVISION.COMPONENT.SUSPEND) {
					description = "ServiceComponent is not of suspend type, but NetworkInfo is suspended";
					problemObject = sc;
					return;
				}
			}

			// integrity check for disconnected device
			if (ni.getStatus() == NetworkStatus.C) {
				if (si.getInactiveDate() != null) {
					description = "ServiceInstance is not disconnected, but NetworkInfo is";
					problemObject = si;
					return;
				}
			}
		}
		sane = true;
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@XmlAttribute
	public boolean isSane() {
		return sane;
	}

	public void setSane(
			boolean sane) {
		this.sane = sane;
	}

	public Object getProblemObject() {
		return problemObject;
	}

	public void setProblemObject(
			Object problemObject) {
		this.problemObject = problemObject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(
			String description) {
		this.description = description;
	}

}