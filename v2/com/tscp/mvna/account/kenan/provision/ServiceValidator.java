package com.tscp.mvna.account.kenan.provision;

import com.tscp.mvna.Validator;

public class ServiceValidator extends Validator<Service> {

	public ServiceValidator(Service target) {
		super(target);
	}

	@Override
	public void validate() {
		if (target.getAccount() == null)
			addProblem(target.getAccount(), "Service has no account");
		if (target.getServiceComponents() == null)
			addProblem(target.getServiceComponents(), "Sevice has no active ServiceComponent");
		if (target.getServiceComponents().size() > 1)
			addProblem(target.getServiceComponents(), "Sevice has more than one active ServiceComponent");
	}

}
