package com.tscp.mvna.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.webservices.api.databinding.DatabindingMode;
import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.UnlinkedAccount;
import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.device.network.exception.ConnectException;
import com.tscp.mvna.account.device.network.exception.DisconnectException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvna.account.device.usage.UsageHistory;
import com.tscp.mvna.account.kenan.exception.AccountCreationException;
import com.tscp.mvna.account.kenan.exception.PaymentFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.PaymentHistory;
import com.tscp.mvna.payment.PaymentRecord;
import com.tscp.mvna.payment.PaymentRequest;
import com.tscp.mvna.payment.PaymentResponse;
import com.tscp.mvna.payment.PaymentTransaction;
import com.tscp.mvna.payment.exception.PaymentServiceException;
import com.tscp.mvna.payment.method.CreditCard;
import com.tscp.mvna.payment.service.PaymentService;
import com.tscp.mvna.user.Customer;
import com.tscp.mvna.user.User;

@WebService
@DatabindingMode(value = "eclipselink.jaxb")
public class TSCPMVNA2 {
	protected static final Logger logger = LoggerFactory.getLogger(TSCPMVNA2.class);

	// TODO MAKE OBJECTS CACHEABLE WITH A CACHEABLE INTERFACE TO RETURN ID AND STALE
	// TODO IMPLEMENT CONTRACTS
	// TODO IMPLEMENT NOTIFICATIONS
	// TODO IMPLEMENT REFUNDS

	/* **************************************************
	 * Fetch Object Methods
	 */

	@WebMethod
	public User getUser(
			int id) {
		return (User) Dao.get(User.class, id);
	}

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
	 * Fetch Usage Methods
	 */

	@WebMethod
	public UsageHistory getUsageHistoryByDevice(
			DeviceAndService device) {
		return getUsageHistoryById(device.getAccountNo(), device.getService().getExternalId());
	}

	@WebMethod
	public UsageHistory getUsageHistoryById(
			int accountNo, String externalId) {
		return new UsageHistory(Dao.list("fetch_usage_history", accountNo, externalId));
	}

	@WebMethod
	public List<UnlinkedAccount> getUnlinkedAccounts(
			int custId) {
		return Dao.list("fetch_unlinked_account", custId);
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

	@WebMethod
	public PaymentTransaction submitTopup(
			DeviceAndService device) throws PaymentServiceException {
		device = getDevice(device.getId());

		PaymentTransaction tx = PaymentService.beginTransaction(device);
		PaymentRequest request = PaymentService.submitRequest(tx);
		PaymentResponse response = PaymentService.submitPayment(tx);
		PaymentRecord record = PaymentService.submitRecord(tx);

		return null;
	}

	/* **************************************************
	 * Account Methods
	 */

	@WebMethod
	public PaymentHistory fetchPaymentHistory(
			int accountNo) throws PaymentFetchException {
		return new PaymentHistory(AccountService.getPaymentRequests(accountNo));
	}

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