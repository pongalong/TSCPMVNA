package com.tscp.mvna.account.kenan.provision;

import com.tscp.mvne.config.PROVISION;

public class InstallComponent extends ServiceComponent {
	private static final long serialVersionUID = 6845447412803716403L;

	public InstallComponent() {
		super.setId(PROVISION.COMPONENT.INSTALL);
	}

	public InstallComponent(ServiceInstance serviceInstance) {
		super(serviceInstance);
		super.setId(PROVISION.COMPONENT.INSTALL);
	}
}
