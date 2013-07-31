package com.tscp.mvna.account.device.usage;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.dao.Dao;

@XmlRootElement
public class UsageHistory {
	protected static final Logger logger = LoggerFactory.getLogger(UsageHistory.class);
	private ServiceInstance serviceInstance;
	private List<UsageSession> usageSessions;

	protected UsageHistory() {
		// do nothing
	}

	public UsageHistory(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	/* **************************************************
	 * Getter and Setter Methods
	 */

	@XmlElement
	public List<UsageSession> getUsageSessions() {
		if (usageSessions == null && !loadedUsageHistory)
			usageSessions = loadUsageHistory();
		else if (loadedUsageHistory && isStale())
			usageSessions = loadUsageHistory();
		return usageSessions;
	}

	public void setUsageSessions(
			List<UsageSession> usageSessions) {
		this.usageSessions = usageSessions;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@XmlAttribute
	public boolean isStale() {
		period = new Period(dateTime, new DateTime());
		return period.getMinutes() > 20;
	}

	public void refresh() {
		loadedUsageHistory = false;
		dateTime = new DateTime();
		period = null;
	}

	protected boolean loadedUsageHistory;
	protected DateTime dateTime = new DateTime();
	protected Period period;

	@XmlTransient
	protected List<UsageSession> loadUsageHistory() {
		// Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		// session.beginTransaction();
		// Query q = session.getNamedQuery("sp_fetch_charge_history");
		// q.setParameter("in_account_no", serviceInstance.getAccount().getAccountNo());
		// q.setParameter("in_external_id", serviceInstance.getExternalId());
		// List<OldUsageDetail> usageDetailList = q.list();
		// session.getTransaction().commit();

		List<UsageSession> result = null;

		try {
			result = Dao.executeNamedQuery("fetch_usage_history", serviceInstance.getAccount().getAccountNo(), serviceInstance.getExternalId());
		} catch (Exception e) {
			logger.error("Error fetching usage history for {}", serviceInstance, e);
		}

		return result;
	}
}