package com.tscp.mvna.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.CustBalanceHolder;
import com.tscp.mvna.TimeSensitive;
import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvna.account.kenan.exception.BalanceFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;

@XmlRootElement
public class Balance implements Serializable, KenanObject, TimeSensitive {
	protected static final Logger logger = LoggerFactory.getLogger(Balance.class);
	private static final long serialVersionUID = -6611563549733421204L;
	protected int accountNo;
	protected BigMoney value;

	/* **************************************************
	 * Constructors
	 */

	protected Balance() {
		// prevent construction
	}

	public Balance(int accountNo) {
		this.accountNo = accountNo;
	}

	public Balance(CustBalanceHolder valueHolder) {
		if (valueHolder != null) {
			if (valueHolder.getCustBalance() != null) {
				if (valueHolder.getCustBalance().getAccountNo() != null && !valueHolder.getCustBalance().getAccountNo().isEmpty())
					accountNo = Integer.parseInt(valueHolder.getCustBalance().getAccountNo());
				value = BigMoney.of(CurrencyUnit.USD, valueHolder.getCustBalance().getRealBalance() * -1);
			}
		}
	}

	public BigMoney getValue() {
		return value;
	}

	public void setValue(
			BigMoney value) {
		this.value = value;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	protected boolean loaded;

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void refresh() {
		try {
			value = AccountService.getBalance(accountNo).getValue();
		} catch (BalanceFetchException e) {
			logger.error("Unable to fetch Balance for Account {}", accountNo);
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

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Balance [account=" + accountNo + ", value=" + value + ", stale=" + isStale() + "]";
	}

}