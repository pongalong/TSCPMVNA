package com.tscp.mvna.account.kenan.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.BillName;
import com.telscape.billingserviceinterface.BillNameHolder;
import com.telscape.billingserviceinterface.BillingAccount;
import com.telscape.billingserviceinterface.BillingAddressHolder;
import com.telscape.billingserviceinterface.ContactInfoHolder;
import com.telscape.billingserviceinterface.MessageHolder;
import com.telscape.billingserviceinterface.PaymentHolder;
import com.telscape.billingserviceinterface.UsageHolder;
import com.telscape.billingserviceinterface.ValueHolder;
import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.Address;
import com.tscp.mvna.account.Balance;
import com.tscp.mvna.account.Contact;
import com.tscp.mvna.account.device.usage.OldUsageDetail;
import com.tscp.mvna.account.kenan.exception.AccountCreationException;
import com.tscp.mvna.account.kenan.exception.AccountUpdateException;
import com.tscp.mvna.account.kenan.exception.BalanceFetchException;
import com.tscp.mvna.account.kenan.exception.ContactFetchException;
import com.tscp.mvna.account.kenan.exception.KenanException;
import com.tscp.mvna.account.kenan.exception.PaymentFetchException;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultBillingAccount;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultBillingName;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultContactInfo;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultCustAddress;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.mvna.payment.PaymentHistory;
import com.tscp.mvna.payment.PaymentRecord;
import com.tscp.mvna.payment.PaymentRequest;
import com.tscp.mvna.payment.PaymentResponse;
import com.tscp.mvna.payment.service.PaymentService;
import com.tscp.mvne.billing.exception.BillingException;
import com.tscp.mvne.billing.provisioning.Component;
import com.tscp.mvne.billing.provisioning.ProvisionUtil;
import com.tscp.mvne.config.BILLING;
import com.tscp.mvne.config.CONFIG;
import com.tscp.util.DateUtils;

public class AccountService extends KenanGatewayService {
	protected static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	protected static final String USERNAME = AccountService.class.getSimpleName();

	/* **************************************************
	 * Account Creation Methods
	 */

	public static int createAccount(
			Account account) throws AccountCreationException {

		BillingAccount billingAccount = new DefaultBillingAccount();
		billingAccount.setBillName(new DefaultBillingName(account.getContact()));
		billingAccount.setCustAddress(new DefaultCustAddress(account.getContact().getAddress()));
		billingAccount.setBillAddress(billingAccount.getCustAddress());
		billingAccount.setContactInfo(new DefaultContactInfo(account.getContact()));
		billingAccount.setCustEmail(account.getContact().getEmail());
		billingAccount.setCustPhone1(account.getContact().getPhoneNumber());

		ValueHolder response = port.addAccount(USERNAME, billingAccount);

		if (response == null)
			throw new AccountCreationException("No response from server");

		if (response.getValue() == null || response.getValue().trim().isEmpty()) {
			if (response.getStatusMessage() != null)
				throw new AccountCreationException("No account number returned from server; Status: " + response.getStatusMessage().getStatus() + " Message: " + response.getStatusMessage().getMessage());
			throw new AccountCreationException("No account number returned from server");
		}

		try {
			account.setAccountNo(Integer.parseInt(response.getValue()));
		} catch (NumberFormatException e) {
			throw new AccountCreationException("Account number returned not in number format: " + response.getValue());
		}

		// TODO save the account so account_map is updated

		Dao.saveOrUpdate(account);
		return account.getAccountNo();
	}

	/* **************************************************
	 * Account Status Methods
	 */

	public static void updateServiceInstanceStatus(
			ServiceInstance serviceInstance, int newThreshold) throws AccountUpdateException {

		if (serviceInstance == null || serviceInstance.isEmpty())
			throw new AccountUpdateException("ServiceInstance is null or empty");
		if (serviceInstance.getExternalIdType() == 0)
			throw new AccountUpdateException("ServiceInstance has an invalid External ID Type, " + serviceInstance.getExternalId());

		MessageHolder messageHolder = port.updateThreshold(USERNAME, serviceInstance.getExternalId(), serviceInstance.getExternalIdType(), Integer.toString(newThreshold));

		if (messageHolder == null)
			throw new AccountUpdateException("No response from server");
		if (!messageHolder.getStatus().equalsIgnoreCase("SUCCESS"))
			throw new AccountUpdateException("Error updating ServiceInstance " + serviceInstance.getExternalId() + " to new Threshold " + newThreshold + ". " + messageHolder.getMessage());
	}

	/* **************************************************
	 * Balance Methods
	 */

	public static Balance getBalance(
			int accountNo) throws BalanceFetchException {
		try {
			return new Balance(port.getCurrentBalance(USERNAME, Integer.toString(accountNo)));
		} catch (Exception e) {
			logger.error("Unable to get balance for Account {}", accountNo, e);
			throw new BalanceFetchException("Unable to fetch balance for Account " + accountNo + ". " + e.getMessage());
		}
	}

	/* **************************************************
	 * Contact Methods
	 */

	public static Contact getContact(
			int accountNo) throws ContactFetchException {
		Contact contact = new Contact(accountNo);
		BillName billName = getName(accountNo);
		if (billName != null) {
			contact.setFirstName(billName.getFirstName());
			contact.setMiddleName(billName.getMiddleName());
			contact.setLastName(billName.getLastName());
		}
		contact.setPhoneNumber(getPhoneNumber(accountNo));
		contact.setEmail(getEmail(accountNo));

		return contact;
	}

	protected static String getPhoneNumber(
			int accountNo) throws ContactFetchException {
		try {
			ContactInfoHolder contactInfo = port.getContactInfo(USERNAME, Integer.toString(accountNo));
			if (contactInfo != null && contactInfo.getContactInfo() != null)
				return contactInfo.getContactInfo().getContact1Phone();
		} catch (Exception e) {
			logger.error("Unable to get contact phone for Account {}", accountNo, e);
			throw new ContactFetchException("Unable to fetch contact phone for Account " + accountNo + ". " + e.getMessage());
		}
		return "";
	}

	protected static String getEmail(
			int accountNo) throws ContactFetchException {
		try {
			ValueHolder valueHolder = port.getEmail(USERNAME, Integer.toString(accountNo));
			if (valueHolder != null)
				return valueHolder.getValue();
		} catch (Exception e) {
			logger.error("Unable to get contact email for Account {}", accountNo, e);
			throw new ContactFetchException("Unable to fetch contact email for Account " + accountNo + ". " + e.getMessage());
		}
		return "";
	}

	protected static BillName getName(
			int accountNo) throws ContactFetchException {
		try {
			BillNameHolder billNameHolder = port.getCustomerName(USERNAME, Integer.toString(accountNo));
			if (billNameHolder != null)
				return billNameHolder.getBillName();
		} catch (Exception e) {
			logger.error("Unable to get contact name for Account {}", accountNo, e);
			throw new ContactFetchException("Unable to fetch contact name for Account " + accountNo + ". " + e.getMessage());
		}
		return null;
	}

	public static void updateEmail(
			int accountNo, String newEmail) throws AccountUpdateException {
		try {
			checkResponse(port.updateEmail(USERNAME, Integer.toString(accountNo), newEmail));
		} catch (KenanException e) {
			throw new AccountUpdateException("Unable to update email for Account " + accountNo, e);
		}
	}

	/* **************************************************
	 * Address Methods
	 */

	public static Address getAddress(
			int accountNo) throws ContactFetchException {
		try {
			BillingAddressHolder billAddressHolder = port.getBillingAddress(USERNAME, Integer.toString(accountNo));
			if (billAddressHolder != null && billAddressHolder.getBillAddress() != null)
				return new Address(billAddressHolder.getBillAddress());
		} catch (Exception e) {
			logger.error("Unable to get address for Account {}", accountNo, e);
			throw new ContactFetchException("Unable to fetch address for Account " + accountNo + ". " + e.getMessage());
		}
		return null;
	}

	/* **************************************************
	 * UNIMPLEMENTED
	 */

	public static PaymentHistory getPaymentHistory(
			int accountNo) throws PaymentFetchException {

		if (accountNo < 1)
			throw new PaymentFetchException("No account number specified");

		try {
			// List<PaymentHolder> response = checkResponse(port.getCompletePaymentHistory(USERNAME,
			// Integer.toString(accountNo)));
			// PaymentHistory paymentHistory = new PaymentHistory(response);

			// List<PaymentTransaction> asdf = Dao.list("fetch_payment_history", accountNo);
			List<PaymentRequest> requests = Dao.fetch("from PaymentRequest where accountNo = ? order by transactionId desc", accountNo);
			return new PaymentHistory(requests);

		} catch (Exception e) {
			e.printStackTrace();
			throw new PaymentFetchException("Unable to fetch PaymentHistory for Account " + accountNo, e);
		}
	}

	// TODO verify that this stored procedure is actually inserting payments into kenan properly
	public static PaymentRecord addPayment_sp(
			PaymentResponse paymentResponse) throws AccountUpdateException {

		PaymentRecord paymentRecord = new PaymentRecord(paymentResponse);
		paymentRecord.setRecordDate(new DateTime());

		logger.debug("paymentResponse has a request object with account_NO {}", paymentResponse.getPaymentRequest().getAccountNo());

		String amount = PaymentService.stringFormatter.print(paymentResponse.getPaymentRequest().getAmount()).replace(".", "");

		int trackingId = (int) Dao.uniqueResultScalar("save_payment_record", paymentResponse.getPaymentRequest().getAccountNo(), amount, paymentRecord.getRecordDate().toDate());

		// TODO get the new tracking ID from paymentHistory and record the seviceInstance/device the payment was for
		paymentRecord.setTrackingId(trackingId);
		return paymentRecord;
	}

	/**
	 * Deprecated because the insertion does not return a tracking id.
	 * 
	 * @param paymentRequest
	 * @param paymentResponse
	 * @return
	 * @throws AccountUpdateException
	 */
	@Deprecated
	public static PaymentRecord addPayment(
			PaymentRequest paymentRequest, PaymentResponse paymentResponse) throws AccountUpdateException {

		String amount = PaymentService.stringFormatter.print(paymentRequest.getAmount()).replace(".", "");
		String accountNo = Integer.toString(paymentRequest.getAccountNo());
		DateTime recordDate = new DateTime();

		// TODO set clientName as a property in configuration files;
		try {
			MessageHolder message = checkResponse(port.addPayment(USERNAME, accountNo, BILLING.externalIdType, amount, DateUtils.getXMLCalendar(recordDate), BILLING.paymentTransType, CONFIG.clientName));
			logger.debug("Status: {} Message: {}", message.getStatus(), message.getMessage());

			// TODO get the new tracking ID from paymentHistory and record the seviceInstance/device the payment was for
			PaymentRecord paymentRecord = new PaymentRecord(paymentResponse);
			paymentRecord.setRecordDate(recordDate);
			paymentRecord.setTrackingId(0);
			return paymentRecord;
		} catch (KenanException e) {
			throw new AccountUpdateException("Unable to add payment of " + amount + " to Account " + paymentRequest.getAccountNo(), e);
		}
	}

	public Account getUnlinkedAccount(
			int custId) {
		throw new NotImplementedException();
	}

	public int getAccountNoByTN(
			String externalId) {
		throw new NotImplementedException();
	}

	public List<PaymentHolder> getPaymentHistory(
			Account account) {
		throw new NotImplementedException();
	}

	public UsageHolder getUnbilledUsageSummary(
			ServiceInstance serviceInstance) {
		throw new NotImplementedException();
	}

	public static final double getProratedMRC() {
		DateTime dateTime = new DateTime();
		int daysPast = dateTime.getDayOfMonth();
		int totalDays = dateTime.dayOfMonth().withMaximumValue().getDayOfMonth();
		final double MRC = 4.99;
		double rate = (totalDays - daysPast) / (totalDays + 0.0);
		return (MRC * rate);
	}

	public static final boolean checkChargeMRC(
			int accountNo, String externalId) throws BillingException {
		return getLastActiveDate(accountNo, externalId).getTime() <= (new Date()).getTime();
	}

	public static final boolean checkChargeMRCByComponent(
			Component component) throws BillingException {
		return !ProvisionUtil.isCurrentMonth(component);
	}

	public static List<OldUsageDetail> getChargeHistory(
			int accountNo, String externalId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("sp_fetch_charge_history");
		q.setParameter("in_account_no", accountNo);
		q.setParameter("in_external_id", externalId);
		List<OldUsageDetail> usageDetailList = q.list();
		session.getTransaction().rollback();
		return usageDetailList;
	}

	public static final Date getLastActiveDate(
			int accountNo, String externalId) throws BillingException {
		List<OldUsageDetail> usageDetailList = getChargeHistory(accountNo, externalId);
		if (usageDetailList != null && !usageDetailList.isEmpty()) {
			for (OldUsageDetail usageDetail : usageDetailList) {
				if (usageDetail.getUsageType().equals("Access Fee")) {
					return usageDetail.getEndTime();
				}
			}
		} else {
			throw new BillingException("No charge history was found for account " + accountNo + " with MDN " + externalId);
		}
		throw new BillingException("No last access date was found for account " + accountNo + " with MDN " + externalId);
	}

}