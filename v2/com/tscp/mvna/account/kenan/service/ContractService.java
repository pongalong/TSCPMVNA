package com.tscp.mvna.account.kenan.service;

import java.util.List;

import com.tscp.mvna.account.kenan.Contract;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.dao.Dao;

//TODO FINISHI IMPLEMENTING CONTRACTS
public class ContractService {

	@SuppressWarnings("unchecked")
	public static List<Contract> getContracts(
			KenanAccount account, ServiceInstance serviceInstance) {
		return (List<Contract>) Dao.list("get_customer_coupons", account.getAccountNo(), serviceInstance.getExternalId());
	}
}
