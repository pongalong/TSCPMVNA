package com.tscp.mvna.account.kenan.provision;

import com.tscp.mvne.config.PROVISION;

public class ReinstallComponent extends ServiceComponent {
	private static final long serialVersionUID = -3059839014612356221L;

	public ReinstallComponent() {
		super.setId(PROVISION.COMPONENT.REINSTALL);
	}

	public ReinstallComponent(ServiceInstance serviceInstance, ServicePackage servicePackage) {
		super(serviceInstance, servicePackage);
		super.setId(PROVISION.COMPONENT.REINSTALL);
	}

}