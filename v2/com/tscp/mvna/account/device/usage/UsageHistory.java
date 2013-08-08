package com.tscp.mvna.account.device.usage;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.TimeSensitive;
import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;
import com.tscp.mvna.dao.Dao;

@XmlRootElement
public class UsageHistory implements KenanObject, TimeSensitive {
	protected static final Logger logger = LoggerFactory.getLogger(UsageHistory.class);
	private ServiceInstance serviceInstance;
	private List<UsageSession> usageSessions;

	/* **************************************************
	 * Constructors
	 */

	protected UsageHistory() {
		// do nothing
	}

	public UsageHistory(List<UsageSession> usageSessions) {
		this.usageSessions = usageSessions;
		this.loaded = true;

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
		if (usageSessions == null && !loaded)
			refresh();
		else if (loaded && isStale())
			refresh();
		return usageSessions;
	}

	public void setUsageSessions(
			List<UsageSession> usageSessions) {
		this.usageSessions = usageSessions;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	protected boolean loaded;

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh() {
		try {
			usageSessions = Dao.executeNamedQuery("fetch_usage_history", serviceInstance.getAccount().getAccountNo(), serviceInstance.getExternalId());
		} catch (Exception e) {
			logger.error("Error fetching usage history for {}", serviceInstance, e);
		} finally {
			loaded = true;
		}
	}

	protected DateTime instantiationTime = new DateTime();

	@Override
	public boolean isStale() {
		Period elapsed = new Period(instantiationTime, new DateTime());
		return elapsed.getMinutes() > 15;
	}

}