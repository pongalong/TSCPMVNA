package com.tscp.mvne.billing;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.joda.time.DateTime;

import com.tscp.mvna.account.device.usage.OldUsageDetail;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.mvne.billing.exception.BillingException;
import com.tscp.mvne.billing.provisioning.Component;
import com.tscp.mvne.billing.provisioning.ProvisionUtil;

public class BillingUtil extends BillingServerUtil {

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

	public static final void checkAccountNumber(
			int accountNumber) throws BillingException, ProvisionException {
		if (accountNumber == 0) {
			throw new ProvisionException("Account Number is not set");
		}
	}

}
