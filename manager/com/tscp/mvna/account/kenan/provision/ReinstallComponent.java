package com.tscp.mvna.account.kenan.provision;

import com.tscp.mvne.config.PROVISION;

public class ReinstallComponent extends ServiceComponent {

	public ReinstallComponent() {
		super();
		super.setId(PROVISION.COMPONENT.REINSTALL);
	}

	public ReinstallComponent(ServiceComponent component) {
		super(component);
		super.setId(PROVISION.COMPONENT.REINSTALL);
	}

}