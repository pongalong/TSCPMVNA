package com.tscp.mvna.account.kenan.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.BillName;
import com.telscape.billingserviceinterface.BillNameHolder;
import com.telscape.billingserviceinterface.BillingAccount;
import com.telscape.billingserviceinterface.BillingAddressHolder;
import com.telscape.billingserviceinterface.ContactInfoHolder;
import com.telscape.billingserviceinterface.CustBalanceHolder;
import com.telscape.billingserviceinterface.MessageHolder;
import com.telscape.billingserviceinterface.ValueHolder;
import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.device.usage.OldUsageDetail;
import com.tscp.mvna.account.kenan.Address;
import com.tscp.mvna.account.kenan.Balance;
import com.tscp.mvna.account.kenan.Contact;
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

	public static BigMoney getBalanceValue(
			int accountNo) throws BalanceFetchException {
		try {
			CustBalanceHolder balanceHolder = port.getCurrentBalance(USERNAME, Integer.toString(accountNo));
			if (balanceHolder != null && balanceHolder.getCustBalance() != null)
				return BigMoney.of(CurrencyUnit.USD, balanceHolder.getCustBalance().getRealBalance() * -1);
		} catch (Exception e) {
			logger.error("Unable to get balance for Account {}", accountNo, e);
			throw new BalanceFetchException("Unable to fetch balance for Account " + accountNo + ". " + e.getMessage());
		}
		return null;
	}

	public static Balance getBalance(
			int accountNo) throws BalanceFetchException {
		return new Balance(getBalanceValue(accountNo));
	}

	public static PaymentHistory getPaymentHistory(
			int accountNo) throws PaymentFetchException {
		return new PaymentHistory(getPaymentRequests(accountNo));
	}

	public static List<PaymentRequest> getPaymentRequests(
			int accountNo) throws PaymentFetchException {

		if (accountNo < 1)
			throw new PaymentFetchException("No account number specified");

		try {
			@SuppressWarnings("unchecked")
			List<PaymentRequest> requests = Dao.fetch("from PaymentRequest where accountNo = ? order by transactionId desc", accountNo);
			return requests;
		} catch (HibernateException e) {
			throw new PaymentFetchException("Unable to fetch PaymentHistory for Account " + accountNo, e);
		}
	}

	public static PaymentRecord addPayment(
			PaymentResponse paymentResponse) throws AccountUpdateException {

		PaymentRecord paymentRecord = new PaymentRecord(paymentResponse);
		paymentRecord.setRecordDate(new DateTime());
		String amount = PaymentService.stringFormatter.print(paymentResponse.getPaymentRequest().getAmount()).replace(".", "");
		int trackingId = (int) Dao.uniqueResult("save_payment_record", paymentResponse.getPaymentRequest().getAccountNo(), amount, paymentRecord.getRecordDate().toDate());
		paymentRecord.setTrackingId(trackingId);
		return paymentRecord;
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
		contact.setAddress(getAddress(accountNo));
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

	// TODO IMPLEMENT THE BELOW

	public Account getUnlinkedAccount(
			int custId) {
		throw new NotImplementedException();
	}

	/**
	 * Pro-rated amonts should be stored in table TC_MOB_MINS on K11MVNO@TSCPMVNA
	 * 
	 * @return
	 */
	@Deprecated
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

	public static List<OldUsageDetail> getChargeHistory(
			int accountNo, String externalId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("sp_fetch_charge_history");
		q.setParameter("in_account_no", accountNo);
		q.setParameter("in_external_id", externalId);
		@SuppressWarnings("unchecked")
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