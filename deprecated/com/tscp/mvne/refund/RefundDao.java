package com.tscp.mvne.refund;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.tscp.mvna.dao.GeneralSPResponse;
import com.tscp.mvna.dao.hibernate.HibernateUtil;

@Deprecated
@SuppressWarnings("unchecked")
public class RefundDao {

	@Deprecated
	public int refundPayment(
			int accountNo, int transId, int trackingId, String amount, String refundBy, int refundCode, String notes) throws OldRefundException {
		if (isRefunded(transId)) {
			throw new OldRefundException("Refund was alread applied for the transaction with transId = " + transId);
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		List<GeneralSPResponse> list = null;
		try {
			Query query = session.getNamedQuery("sp_refund_pmt");
			query.setParameter("in_account_no", accountNo);
			query.setParameter("in_tracking_id", trackingId);
			query.setParameter("in_refund_amount", amount);
			query.setParameter("in_refund_by", refundBy);
			query.setParameter("in_refund_reason_code", refundCode);
			query.setParameter("in_refund_notes", notes);
			list = query.list();

			GeneralSPResponse response = getQueryResponse(list, transaction, session);
			if (response != null && response.isSuccess() && !transaction.wasCommitted()) {
				transaction.commit();
				return response.getCode();
			} else {
				throw new OldRefundException("Error refund payment " + "Fail Reason is : " + response.getMsg());
			}

		} catch (HibernateException he) {
			if (transaction != null && !transaction.wasRolledBack())
				transaction.rollback();
			he.printStackTrace();
			throw new OldRefundException("refundPayment", he.getMessage());
		} finally {
			if (session.isOpen()) {
				try {
					session.close();
				} catch (HibernateException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Deprecated
	public OldRefund getRefundByTransId(
			int transId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		OldRefund refund = null;
		try {
			refund = (OldRefund) session.get(OldRefund.class, transId);
			if (!transaction.wasCommitted())
				transaction.commit();
		} catch (HibernateException he) {
			if (!transaction.wasRolledBack())
				transaction.rollback();
			he.printStackTrace();
			throw new OldRefundException("getRefundByTransId", he.getMessage());
		} finally {
			if (session.isOpen()) {
				try {
					session.close();
				} catch (HibernateException e) {
					e.printStackTrace();
				}
			}
		}
		return refund;
	}

	@Deprecated
	private static GeneralSPResponse getQueryResponse(
			List cursor, Transaction transaction, Session session) {
		GeneralSPResponse response = null;
		if (cursor == null || cursor.size() < 1) {
			if (!transaction.wasRolledBack())
				transaction.rollback();
			if (session.isOpen())
				session.close();
			response = new GeneralSPResponse();
			response.setStatus("N");
			response.setMsg("No items returned in cursor");
		} else {
			response = (GeneralSPResponse) cursor.get(0);
		}
		return response;
	}

	@Deprecated
	public boolean isRefunded(
			int transId) {
		OldRefund refund = getRefundByTransId(transId);
		return (refund != null);
	}

}