package com.tscp.mvna.account.kenan.provision;

import com.tscp.mvne.config.PROVISION;

public class SuspendComponent extends ServiceComponent {
	private static final long serialVersionUID = -84993254134237837L;

	public SuspendComponent() {
		super.setId(PROVISION.COMPONENT.SUSPEND);
	}

	public SuspendComponent(ServiceInstance serviceInstance) {
		super(serviceInstance);
		super.setId(PROVISION.COMPONENT.SUSPEND);
	}

}
