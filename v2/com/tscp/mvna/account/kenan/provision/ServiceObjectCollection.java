package com.tscp.mvna.account.kenan.provision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.KenanAccount;
import com.tscp.mvna.account.kenan.KenanObjectCollection;

public abstract class ServiceObjectCollection<T> extends KenanObjectCollection<T> {
	private static final long serialVersionUID = 7282930688605389614L;
	protected static final Logger logger = LoggerFactory.getLogger(ServiceObjectCollection.class);
	protected KenanAccount account;

	public KenanAccount getAccount() {
		return account;
	}

	public void setAccount(
			KenanAccount account) {
		this.account = account;
	}

}