package com.tscp.mvna.account.device.usage;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.dao.Dao;

@XmlRootElement
public class UsageHistory implements Serializable {
	private static final long serialVersionUID = 7544343617194343078L;
	protected static final Logger logger = LoggerFactory.getLogger(UsageHistory.class);
	private ServiceInstance serviceInstance;
	private List<UsageSession> usageSessions;
	private DateTime dateTime = new DateTime();
	private Period period;
	private boolean loadedUsageHistory;

	protected UsageHistory() {
		// do nothing
	}

	public UsageHistory(List<UsageSession> usageSessions) {
		this.usageSessions = usageSessions;
		this.loadedUsageHistory = true;

		if (usageSessions != null && !usageSessions.isEmpty()) {
			UsageSession firstSession = usageSessions.get(0);
			KenanAccount account = new KenanAccount();
			account.setAccountNo(firstSession.getAccountNo());

			serviceInstance = new ServiceInstance();
			serviceInstance.setAccount(account);
			serviceInstance.setExternalId(firstSession.getExternalId());
		}
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
	 * Helper Methods
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

	/* **************************************************
	 * Fetch Methods
	 */

	@SuppressWarnings("unchecked")
	protected List<UsageSession> loadUsageHistory() {
		List<UsageSession> result = null;

		try {
			result = Dao.executeNamedQuery("fetch_usage_history", serviceInstance.getAccount().getAccountNo(), serviceInstance.getExternalId());
		} catch (Exception e) {
			logger.error("Error fetching usage history for {}", serviceInstance, e);
		} finally {
			loadedUsageHistory = true;
		}

		return result;
	}
}