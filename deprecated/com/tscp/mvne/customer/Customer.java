package com.tscp.mvne.customer;

import java.util.List;
import java.util.Vector;

import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.usage.OldUsageDetail;
import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.mvna.payment.method.manager.CreditCardManager;
import com.tscp.mvne.billing.Account;
import com.tscp.mvne.customer.dao.CustAcctMapDAO;
import com.tscp.mvne.customer.dao.CustAddress;
import com.tscp.mvne.customer.dao.CustInfo;
import com.tscp.mvne.customer.dao.CustTopUp;
import com.tscp.mvne.device.DeviceAssociation;
import com.tscp.mvne.device.OldDevice;
import com.tscp.mvne.hibernate.GeneralSPResponse;
import com.tscp.mvne.payment.OldPaymentInformation;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.PaymentType;
import com.tscp.mvne.payment.dao.CustPmtMap;
import com.tscp.mvne.payment.dao.OldCreditCard;
import com.tscp.mvne.payment.dao.PaymentInvoice;
import com.tscp.mvne.payment.dao.PaymentRecord;
import com.tscp.mvne.payment.dao.PaymentTransaction;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;

@Deprecated
@SuppressWarnings("unchecked")
public class Customer {
	private static Logger logger = LoggerFactory.getLogger("TSCPMVNA");
	private int id;
	private List<CustAcctMapDAO> custaccts;
	private List<CustPmtMap> custpmttypes;
	private List<OldPaymentInformation> paymentinformation;
	private List<OldDevice> deviceList;

	public Customer() {
		paymentinformation = new Vector<OldPaymentInformation>();
	}

	public Customer(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(
			int id) {
		this.id = id;
	}

	public void addCustAccts(
			Account account) throws CustomerException {
		if (id <= 0) {
			throw new CustomerException("addCustAccts", "Please specify a customer to add an account mapping.");
		}
		if (account == null || account.getAccountNo() <= 0) {
			throw new CustomerException("addCustAccts", "Please specify an account to add to customer " + id);
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		CustAcctMapDAO custacctmap = new CustAcctMapDAO();
		custacctmap.setCust_id(id);
		custacctmap.setAccount_no(account.getAccountNo());
		Query q = session.getNamedQuery("ins_cust_acct_map");
		q.setParameter("cust_id", custacctmap.getCust_id());
		q.setParameter("account_no", custacctmap.getAccount_no());
		List<GeneralSPResponse> spresponse = q.list();

		if (spresponse != null) {
			for (GeneralSPResponse response : spresponse) {
				logger.debug("STATUS :: " + response.getStatus() + " :: MVNEMSGCODE :: " + response.getCode() + " :: MVNEMSG :: " + response.getMsg());
				if (!response.getStatus().equals("Y")) {
					throw new CustomerException("addCustAccts", "Error adding Customer Acct Map:: " + response.getCode() + "::" + response.getMsg());
				}
			}
		} else {
			throw new CustomerException("addCustAccts", "No response returned from the db when calling ins_cust_acct_map");
		}

		tx.commit();
	}

	public void deleteCustAccts(
			Account account) throws CustomerException {
		if (id <= 0) {
			throw new CustomerException("addCustAccts", "Please specify a customer to add an account mapping.");
		}
		if (account == null || account.getAccountNo() <= 0) {
			throw new CustomerException("addCustAccts", "Please specify an account to add to customer " + id);
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		CustAcctMapDAO custacctmap = new CustAcctMapDAO();
		custacctmap.setCust_id(id);
		custacctmap.setAccount_no(account.getAccountNo());
		Query q = session.getNamedQuery("del_cust_acct_map");
		q.setParameter("cust_id", custacctmap.getCust_id());
		q.setParameter("account_no", custacctmap.getAccount_no());
		List<GeneralSPResponse> spresponse = q.list();

		if (spresponse != null) {
			for (GeneralSPResponse response : spresponse) {
				logger.debug("STATUS :: " + response.getStatus() + " :: MVNEMSGCODE :: " + response.getCode() + " :: MVNEMSG :: " + response.getMsg());
				if (!response.getStatus().equals("Y")) {
					throw new CustomerException("addCustAccts", "Error deleting Customer Acct Map:: " + response.getCode() + "::" + response.getMsg());
				}
			}
		} else {
			throw new CustomerException("addCustAccts", "No response returned from the db when calling ins_cust_acct_map");
		}

		session.getTransaction().commit();
	}

	public List<OldUsageDetail> getChargeHistory(
			int accountNo, String mdn) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Query q = session.getNamedQuery("sp_fetch_charge_history");
		q.setParameter("in_account_no", accountNo);
		q.setParameter("in_external_id", mdn);

		List<OldUsageDetail> usageDetailList = q.list();

		session.getTransaction().rollback();
		return usageDetailList;
	}

	public List<OldUsageDetail> getChargeHistory(
			int accountNo, String mdn, int dayRange) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Query q = session.getNamedQuery("sp_fetch_charge_history_range");
		q.setParameter("in_account_no", accountNo);
		q.setParameter("in_external_id", mdn);
		q.setParameter("in_day_range", dayRange);

		List<OldUsageDetail> usageDetailList = q.list();

		session.getTransaction().rollback();
		return usageDetailList;
	}

	public CustAcctMapDAO getCustAcctMapDAOfromAccount(
			int accountno) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Query q = session.getNamedQuery("fetch_cust_from_acct");
		q.setParameter("in_account_no", accountno);
		List<CustAcctMapDAO> custAcctMapList = q.list();
		CustAcctMapDAO retValue = new CustAcctMapDAO();
		for (CustAcctMapDAO custAcctMap : custAcctMapList) {
			retValue = custAcctMap;
		}

		session.getTransaction().commit();
		return retValue;
	}

	public List<CustAcctMapDAO> getCustaccts() {
		if (custaccts == null) {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Query q = session.getNamedQuery("fetch_cust_acct_map");
			q.setParameter("in_cust_id", id);
			custaccts = q.list();
			session.getTransaction().commit();
		}
		return custaccts;
	}

	public List<CustAddress> getCustAddressList(
			int addressId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();

		Query q = session.getNamedQuery("fetch_cust_address");
		q.setParameter("in_cust_id", id);
		q.setParameter("in_address_id", addressId);

		List<CustAddress> custAddressList = q.list();

		session.getTransaction().rollback();
		return custAddressList;
	}

	public CustInfo getCustInfo() {
		CustInfo custInfo = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("fetch_cust_info");
		q.setParameter("in_cust_id", id);
		List<CustInfo> custInfoList = q.list();
		if (custInfoList != null && custInfoList.size() > 0) {
			custInfo = custInfoList.get(0);
		}
		session.getTransaction().commit();
		return custInfo;
	}

	public List<CustPmtMap> getCustpmttypes(
			int pmt_id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("fetch_cust_pmt_map");
		q.setParameter("in_cust_id", id);
		q.setParameter("in_pmt_id", pmt_id);
		custpmttypes = q.list();
		session.getTransaction().commit();
		return custpmttypes;
	}

	public List<OldDevice> getDeviceList() {
		return deviceList;
	}

	public List<PaymentRecord> getPaymentHistory() throws CustomerException {
		if (id <= 0) {
			throw new CustomerException("Invalid Customer Id " + id);
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		List<PaymentRecord> paymentRecordList = null;

		try {
			session.setCacheMode(CacheMode.IGNORE);
			session.evict(paymentRecordList);
			Query q = session.getNamedQuery("fetch_cust_pmt_trans");
			q.setReadOnly(true);
			q.setCacheable(false);
			q.setParameter("in_cust_id", id);
			paymentRecordList = q.list();
			tx.commit();
		} catch (HibernateException he) {
			tx.rollback();
			throw new CustomerException("getPaymentHistory", he.getMessage());
		} finally {
			if (session.isOpen())
				session.close();
		}
		return paymentRecordList;
	}

	public List<OldPaymentInformation> getPaymentinformation() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("fetch_cust_pmt_map");
		q.setParameter("in_cust_id", id);
		q.setParameter("in_pmt_id", 0);
		custpmttypes = q.list();
		for (CustPmtMap custpmt : custpmttypes) {
			if (custpmt.getPaymentalias().equals(PaymentType.CreditCard.toString())) {
				q = session.getNamedQuery("fetch_pmt_cc_info");
				q.setParameter("in_pmt_id", custpmt.getPaymentid());
				List<OldCreditCard> creditcard = q.list();
				if (creditcard != null) {
					paymentinformation.add(creditcard.get(0));
				}
			}
		}
		session.getTransaction().rollback();
		return paymentinformation;
	}

	public PaymentInvoice getPaymentInvoice(
			int transId) throws CustomerException {
		if (id <= 0) {
			throw new CustomerException("Invalid Customer...Id cannot be <= 0");
		}
		if (transId == 0) {
			throw new PaymentException("Please specify a transaction to look up an invoice against");
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		PaymentInvoice paymentInvoice = new PaymentInvoice();

		Query q = session.getNamedQuery("fetch_pmt_invoice");
		q.setParameter("in_cust_id", id);
		q.setParameter("in_trans_id", transId);
		List<PaymentInvoice> paymentInvoiceList = q.list();
		if (paymentInvoiceList != null && paymentInvoiceList.size() > 0) {
			for (PaymentInvoice tempPaymentInvoice : paymentInvoiceList) {
				paymentInvoice = tempPaymentInvoice;
			}
		}
		session.getTransaction().commit();
		return paymentInvoice;
	}

	public CustTopUp getTopupAmount(
			Account account) throws CustomerException {
		if (id == 0) {
			throw new CustomerException("getTopupAmount", "Customer.id must be set...");
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		String query = "fetch_cust_topup_amt";

		Query q = session.getNamedQuery(query);
		q.setParameter("in_cust_id", id);
		q.setParameter("in_account_no", account.getAccountNo());
		CustTopUp topupAmount = new CustTopUp();
		List<CustTopUp> topupAmountList = q.list();
		for (CustTopUp custTopUp : topupAmountList) {
			topupAmount = custTopUp;
		}
		session.getTransaction().commit();
		return topupAmount;
	}

	public List<DeviceAssociation> retrieveDeviceAssociationList(
			int inDeviceId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Query q = session.getNamedQuery("fetch_device_assoc_map");
		q.setParameter("in_cust_id", id);
		q.setParameter("in_device_id", inDeviceId);
		List<DeviceAssociation> deviceAssociationList = q.list();

		session.getTransaction().rollback();
		return deviceAssociationList;
	}

	public List<OldDevice> retrieveDeviceList() {
		return retrieveDeviceList(0, 0);
	}

	public List<OldDevice> retrieveDeviceList(
			int accountNo) {
		return retrieveDeviceList(0, accountNo);
	}

	public List<OldDevice> retrieveDeviceList(
			int deviceId, int accountNo) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("fetch_device_info");
		q.setParameter("in_cust_id", id);
		q.setParameter("in_device_id", deviceId);
		q.setParameter("in_account_no", accountNo);
		List<OldDevice> deviceInfoList = q.list();
		setDeviceList(deviceInfoList);
		session.getTransaction().rollback();
		return getDeviceList();
	}

	private void saveCustPmtMap(
			OldPaymentInformation paymentinformation) throws PaymentException {

		CustPmtMap custpmtmap = new CustPmtMap();
		custpmtmap.setCustid(id);
		custpmtmap.setPaymentid(paymentinformation.getPaymentId());
		custpmtmap.setPaymenttype(paymentinformation.getPaymentType().toString());
		custpmtmap.setPaymentalias(paymentinformation.getAlias());
		custpmtmap.setIsDefault(paymentinformation.isDefaultPayment() ? "Y" : null);

		custpmtmap.save();
	}

	public void setCustaccts(
			List<CustAcctMapDAO> custaccts) {
		this.custaccts = custaccts;
	}

	public void setCustpmttypes(
			List<CustPmtMap> custpmttypes) {
		this.custpmttypes = custpmttypes;
	}

	public void setDeviceList(
			List<OldDevice> deviceList) {
		this.deviceList = deviceList;
	}

	public CustTopUp setTopupAmount(
			Account account, String topupAmount) throws CustomerException {
		if (id == 0) {
			throw new CustomerException("setTopupAmount", "Customer.id must be set");
		}
		CustTopUp custTopUp = new CustTopUp();
		custTopUp.setCustid(id);
		custTopUp.setTopupAmount(topupAmount);
		custTopUp.setAccountNo(account.getAccountNo());
		custTopUp.save();
		return getTopupAmount(account);
	}

	public PaymentUnitResponse submitPayment(
			PaymentTransaction transaction, int paymentId) throws PaymentException {
		PaymentUnitResponse retValue = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q = session.getNamedQuery("sbt_pmt_info");
		q.setParameter("in_cust_id", id);
		q.setParameter("in_pmt_id", paymentId);
		q.setParameter("in_pymntamt", transaction.getPaymentAmount());

		List<PaymentUnitResponse> responseList = q.list();
		for (PaymentUnitResponse response : responseList) {
			retValue = response;
		}

		session.getTransaction().commit();
		return retValue;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Customer Object....");
		sb.append("\n");
		sb.append("id               :: " + id);
		return sb.toString();
	}

	public OldCreditCard addCreditCard(
			OldCreditCard creditCard) throws PaymentException {

		if (id <= 0)
			throw new CustomerException("insertCreditCardPaymentInformation", "Please specify a Customer Id");
		if (creditCard == null)
			throw new PaymentException("insertCreditCardPaymentInformation", "Please specify payment information to save.");

		if (creditCard.validate())
			creditCard.save();

		if (creditCard.getPaymentId() <= 0)
			throw new PaymentException("insertCreditCardPaymentInformation", "Error saving payment information.");

		if (creditCard.getAlias() == null || creditCard.getAlias().trim().isEmpty())
			creditCard.setAlias(CreditCardManager.getAlias(creditCard));

		saveCustPmtMap(creditCard);
		return creditCard;
	}

	public OldCreditCard updateCreditCard(
			OldCreditCard creditCard) throws PaymentException {

		if (id <= 0)
			throw new CustomerException("updateCreditCardPaymentInformation", "Please specify a Customer Id");
		if (creditCard == null || creditCard.getPaymentId() <= 0)
			throw new PaymentException("updateCreditCardPaymentInformation", "Please specify payment information to update.");

		if (creditCard.validate())
			creditCard.update();
		if (creditCard.getAlias() == null || creditCard.getAlias().trim().length() == 0)
			creditCard.setAlias(CreditCardManager.getAlias(creditCard));

		return creditCard;
	}

	// public void deletePaymentMethod(
	// int paymentId) throws CustomerException {
	//
	// if (id == 0)
	// throw new CustomerException("deletePayment", "Invalid Customer Object. ID must be set.");
	//
	// List<CustPmtMap> custPmtMapList = getCustpmttypes(0);
	// boolean isValidTransaction = false;
	//
	// for (CustPmtMap cpm : custPmtMapList) {
	// if (cpm.getPaymentid() == paymentId) {
	// isValidTransaction = true;
	// if (cpm.getPaymenttype().equals(PaymentType.CreditCard.toString())) {
	// CreditCardManager.deleteCreditCard(paymentId);
	// }
	// break;
	// }
	// }
	//
	// if (!isValidTransaction)
	// throw new CustomerException("deletePayment", "Invalid Request. Payment ID " + paymentId +
	// " does not belong to cust id " + id);
	// }

}
