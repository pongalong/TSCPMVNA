package com.tscp.mvna.account.kenan;

import com.tscp.mvna.account.contract.exception.ContractException;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;

public class Contract {
	private KenanAccount account;
	private ServiceInstance serviceInstance;
	private int contractType;
	private int contractId;
	private int duration;
	private String description;

	/* **************************************************
	 * Validation Methods
	 */

	public void validate() throws ContractException {
		if (account == null || account.getAccountNo() == 0)
			throw new ContractException("Account is not set");
		if (serviceInstance == null || serviceInstance.isEmpty())
			throw new ContractException("ServiceInstance is not set");
		if (contractType == 0)
			throw new ContractException("ContractType is not set");
		if (duration < 0)
			throw new ContractException("Duration is not set");
	}

	/* **************************************************
	 * Getters and Setters
	 */

	public KenanAccount getAccount() {
		return account;
	}

	public void setAccount(
			KenanAccount account) {
		this.account = account;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(
			ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public int getContractType() {
		return contractType;
	}

	public void setContractType(
			int contractType) {
		this.contractType = contractType;
	}

	public int getContractId() {
		return contractId;
	}

	public void setContractId(
			int contractId) {
		this.contractId = contractId;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(
			int duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(
			String description) {
		this.description = description;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Contract [account=" + account + ", serviceInstance=" + serviceInstance + ", contractId=" + contractId + ", description=" + description + "]";
	}
}
