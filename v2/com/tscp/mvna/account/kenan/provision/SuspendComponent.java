package com.tscp.mvna.account.kenan.provision;

import com.tscp.mvne.config.PROVISION;

public class SuspendComponent extends ServiceComponent {

	public SuspendComponent() {
		super.setId(PROVISION.COMPONENT.SUSPEND);
	}

	public SuspendComponent(ServiceComponent component) {
		super(component);
		super.setId(PROVISION.COMPONENT.SUSPEND);
	}

}
