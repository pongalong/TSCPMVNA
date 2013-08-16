package com.tscp.mvna.account.device.usage;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.TimeSensitiveKenanObjectCollection;
import com.tscp.mvna.account.kenan.product.ServiceInstanceV2;
import com.tscp.mvna.dao.Dao;

@XmlRootElement
public class UsageHistory extends TimeSensitiveKenanObjectCollection<UsageSession> {
	private static final long serialVersionUID = 5564459631548588005L;
	protected static final Logger logger = LoggerFactory.getLogger(UsageHistory.class);
	private ServiceInstanceV2 serviceInstance;

	/* **************************************************
	 * Constructors
	 */

	protected UsageHistory() {
		// do nothing
	}

	public UsageHistory(ServiceInstanceV2 serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public UsageHistory(List<UsageSession> usageSessions) {
		super.addAll(usageSessions);
		this.loaded = true;

		// TODO check if there's a better way to set the account rather than building it from a UsageSession
		if (usageSessions != null && !usageSessions.isEmpty()) {
			UsageSession firstSession = usageSessions.get(0);
			serviceInstance = new ServiceInstanceV2();
			serviceInstance.setExternalId(firstSession.getExternalId());
		}
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<UsageSession> loadValue() {
		try {
			// TODO consider moving these fetches to a DeviceService class for better separation
			return Dao.list("fetch_usage_history", serviceInstance.getServicePackage().getAccount().getAccountNo(), serviceInstance.getExternalId());
		} catch (HibernateException e) {
			logger.error("Error loading UsageHistory for", serviceInstance, e);
		} finally {
			loaded = true;
		}
		return null;
	}

}