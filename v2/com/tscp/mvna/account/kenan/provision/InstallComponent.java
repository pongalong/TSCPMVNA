package com.tscp.mvna.account.kenan.provision;

import com.tscp.mvne.config.PROVISION;

public class InstallComponent extends ServiceComponent {

	public InstallComponent() {
		super();
		super.setId(PROVISION.COMPONENT.INSTALL);
	}

	public InstallComponent(ServiceComponent component) {
		super(component);
		super.setId(PROVISION.COMPONENT.INSTALL);
	}

}
