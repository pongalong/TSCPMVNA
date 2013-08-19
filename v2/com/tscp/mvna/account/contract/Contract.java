package com.tscp.mvna.account.contract;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.tscp.mvna.account.kenan.provision.ServiceInstance;

@Entity
public class Contract implements Serializable {
	private static final long serialVersionUID = 4982673690989698367L;
	private ContractValidator validator;
	private ServiceInstance serviceInstance;
	private int contractType;
	private int contractId;
	private int duration;
	private String description;

	@Transient
	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(
			ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	@Column(name = "contract_type")
	public int getContractType() {
		return contractType;
	}

	public void setContractType(
			int contractType) {
		this.contractType = contractType;
	}

	@Id
	@Column(name = "contract_id")
	public int getContractId() {
		return contractId;
	}

	public void setContractId(
			int contractId) {
		this.contractId = contractId;
	}

	@Column(name = "duration")
	public int getDuration() {
		return duration;
	}

	public void setDuration(
			int duration) {
		this.duration = duration;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(
			String description) {
		this.description = description;
	}

	@Transient
	public ContractValidator getValidator() {
		if (validator == null) {
			validator = new ContractValidator(this);
			validator.validate();
		}
		return validator;
	}

}
