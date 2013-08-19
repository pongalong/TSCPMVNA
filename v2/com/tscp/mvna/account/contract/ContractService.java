package com.tscp.mvna.account.contract;

import java.util.List;

import org.joda.money.Money;
import org.joda.time.DateTime;

import com.tscp.mvna.account.contract.exception.ContractException;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.service.PaymentService;

public class ContractService {

	public int applyContract(
			Contract contract) throws ContractException {

		contract.getValidator().validate();
		if (!contract.getValidator().isSane())
			throw new ContractException("Cannot create contract with the following errors: " + contract.getValidator().getProblems());

		Object[] args = new Object[4];
		args[0] = contract.getServiceInstance().getAccount().getAccountNo();
		args[1] = contract.getServiceInstance().getExternalId();
		args[2] = contract.getContractType();
		args[3] = contract.getDuration();

		return (Integer) Dao.uniqueResult("add_contract", args);
	}

	public void updateContract(
			Contract contract) throws ContractException {

		contract.getValidator().validate();
		if (!contract.getValidator().isSane())
			throw new ContractException("Cannot create contract with the following errors: " + contract.getValidator().getProblems());

		Object[] args = new Object[5];
		args[0] = contract.getServiceInstance().getAccount().getAccountNo();
		args[1] = contract.getServiceInstance().getExternalId();
		args[2] = contract.getContractType();
		args[3] = contract.getContractId();
		args[4] = contract.getDuration();

		int result = Dao.executeUpdate("update_contract", args);
		if (result < 0)
			throw new ContractException("Contract was not updated");
		if (result == 0)
			throw new ContractException("No contracts were updated");
		if (result > 1)
			throw new ContractException("More than one contract was updated");
	}

	public List<Contract> getContracts(
			ServiceInstance serviceInstance) throws ContractException {
		@SuppressWarnings("unchecked")
		List<Contract> result = Dao.list("get_contracts", serviceInstance.getAccount().getAccountNo(), serviceInstance.getExternalId());
		return result;
	}

	public int applyPayment(
			KenanAccount account, Money amount, DateTime date) throws ContractException {
		int trackingId = (int) Dao.uniqueResult("add_coupon_payment", account.getAccountNo(), PaymentService.stringFormatter.print(amount), date.toDate());
		return trackingId;
	}

}
