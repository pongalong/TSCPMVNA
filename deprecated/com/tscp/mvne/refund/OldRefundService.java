package com.tscp.mvne.refund;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.mvna.payment.PaymentTransaction;
import com.tscp.mvne.billing.Account;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.dao.OldCreditCard;

@Deprecated
public class OldRefundService {
	private RefundDao refundDao = new RefundDao();

	public List<KenanPayment> getKenanPayments(
			Account account) throws OldRefundException {
		return KenanPaymentDao.getKenanPayments(account);
	}

	public void reversePayment(
			Account account, String amount, Date transDate, String trackingId) throws OldRefundException {
		KenanPaymentDao.reversePayment(account, amount, transDate, trackingId);
	}

	// public void applyChargeCredit(
	// OldCreditCard creditCard, String amount) throws OldRefundException {
	// KenanPaymentDao.applyChargeCredit(creditCard, amount);
	// }

	// TODO unused method. check for removal.
	@Deprecated
	private void applyChargeCredit(
			int accountNo, int trackingId, String amount, String refundBy, int refundCode, String notes) throws OldRefundException {
		KenanPaymentDao.applyChargeCredit(accountNo, trackingId, amount, refundBy, refundCode, notes);
	}

	public PaymentTransaction getPaymentTransaction(
			int custId, int transId) throws PaymentException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query q;
		q = session.getNamedQuery("fetch_pmt_trans");
		q.setParameter("in_cust_id", custId);
		q.setParameter("in_trans_id", transId);
		List<PaymentTransaction> paymentTransactions = q.list();
		session.getTransaction().rollback();
		if (paymentTransactions != null && !paymentTransactions.isEmpty()) {
			if (paymentTransactions.size() == 1) {
				return paymentTransactions.get(0);
			} else {
				throw new PaymentException("More than one payment transaction found with transId " + transId + " for cust " + custId);
			}
		} else {
			throw new PaymentException("No payment transaction found with transId " + transId + " for cust " + custId);
		}
	}

	public synchronized int refundPayment(
			int accountNo, int transId, int trackingId, String amount, String refundBy, int refundCode, String notes) throws OldRefundException {
		return refundDao.refundPayment(accountNo, transId, trackingId, amount, refundBy, refundCode, notes);
	}

	// TODO unused method. check for removal.
	@Deprecated
	public OldRefund getRefundByTransId(
			int transId) {
		return refundDao.getRefundByTransId(transId);
	}

}
