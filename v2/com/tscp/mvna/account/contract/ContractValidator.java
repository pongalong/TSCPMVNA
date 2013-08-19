package com.tscp.mvna.account.contract;

import com.tscp.mvna.Validator;

public class ContractValidator extends Validator<Contract> {

	public ContractValidator(Contract target) {
		super(target);
	}

	@Override
	public void validate() {
		if (target.getServiceInstance() == null || target.getServiceInstance().isEmpty())
			addProblem(target.getServiceInstance(), "ServiceInstance is null or not set");
		else {
			if (target.getServiceInstance().getAccount() == null || target.getServiceInstance().getAccount().getAccountNo() == 0)
				addProblem(target.getServiceInstance().getAccount(), "Account is null or not set");
		}

		if (target.getContractType() == 0)
			addProblem(target.getContractType(), "ContractType is not set");
		if (target.getDuration() < 0)
			addProblem(target.getDuration(), "Duration is not set");
	}

}
