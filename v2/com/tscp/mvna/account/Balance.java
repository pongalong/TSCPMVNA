package com.tscp.mvna.account;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.CustBalanceHolder;
import com.tscp.mvna.TimeSensitive;
import com.tscp.mvna.account.kenan.KenanObject;
import com.tscp.mvna.account.kenan.exception.BalanceFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;

@XmlRootElement
public class Balance extends TimeSensitive implements KenanObject {
	protected static final Logger logger = LoggerFactory.getLogger(Balance.class);
	protected static final DecimalFormat df = new DecimalFormat("0.00000");
	protected int accountNo;
	protected Double value;

	/* **************************************************
	 * Constructors
	 */

	protected Balance() {
		// do nothing;
	}

	public Balance(int accountNo) {
		this.accountNo = accountNo;
	}

	public Balance(CustBalanceHolder valueHolder) {
		if (valueHolder != null) {
			if (valueHolder.getCustBalance() != null) {
				if (valueHolder.getCustBalance().getAccountNo() != null && !valueHolder.getCustBalance().getAccountNo().isEmpty())
					accountNo = Integer.parseInt(valueHolder.getCustBalance().getAccountNo());
				value = valueHolder.getCustBalance().getRealBalance() * -1;
			}
		}
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
			this.value = AccountService.getBalance(accountNo).getValue();
		} catch (BalanceFetchException e) {
			logger.error("Unable to fetch Balance for Account {}", accountNo);
		} finally {
			loaded = true;
		}
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@XmlElement
	public String getCurrencyValue() {
		return value == null ? null : NumberFormat.getCurrencyInstance().format(value);
	}

	@XmlElement
	public Double getValue() {
		return value;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Balance [account=" + accountNo + ", value=" + value + ", stale=" + isStale() + "]";
	}

}