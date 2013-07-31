package com.tscp.mvna.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.webservices.api.databinding.DatabindingMode;
import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.device.network.exception.ConnectException;
import com.tscp.mvna.account.device.network.exception.DisconnectException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvna.account.kenan.exception.AccountCreationException;
import com.tscp.mvna.account.kenan.service.AccountService;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.method.CreditCard;
import com.tscp.mvna.user.customer.Customer;

@WebService
@DatabindingMode(value = "eclipselink.jaxb")
public class TSCPMVNA2 {
	protected static final Logger logger = LoggerFactory.getLogger(TSCPMVNA2.class);

	/* **************************************************
	 * Fetch Methods
	 */

	@WebMethod
	public Customer getCustomer(
			int id) {
		return (Customer) Dao.get(Customer.class, id);
	}

	@WebMethod
	public CreditCard getCreditCard(
			int id) {
		return (CreditCard) Dao.get(CreditCard.class, id);
	}

	@WebMethod
	public DeviceAndService getDevice(
			int id) {
		return (DeviceAndService) Dao.get(DeviceAndService.class, id);
	}

	@WebMethod
	public Account getAccount(
			int accountNo) {
		return (Account) Dao.get(Account.class, accountNo);
	}

	/* **************************************************
	 * Device Methods
	 */

	@WebMethod
	public DeviceAndService connectDevice(
			DeviceAndService device) throws ConnectException {
		device = getDevice(device.getId());
		device.connect();
		return device;
	}

	@WebMethod
	public DeviceAndService disconnectDevice(
			DeviceAndService device) throws DisconnectException {
		device = getDevice(device.getId());
		device.disconnect();
		return device;
	}

	@WebMethod
	public DeviceAndService suspendDevice(
			DeviceAndService device) throws SuspendException {
		device = getDevice(device.getId());
		device.suspend();
		return device;
	}

	@WebMethod
	public DeviceAndService restoreDevice(
			DeviceAndService device) throws RestoreException {
		device = getDevice(device.getId());
		device.restore();
		return device;
	}

	/* **************************************************
	 * Account Methods
	 */

	@WebMethod
	public int createAccount(
			Account account) throws AccountCreationException {
		return AccountService.createAccount(account);
	}

	@WebMethod
	public void updateEmail(
			int accountNo, String newEmail) {
		AccountService.updateEmail(accountNo, newEmail);
	}

}