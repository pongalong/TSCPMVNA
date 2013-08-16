package com.tscp.mvna.account.kenan;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.exception.BalanceFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;

@XmlRootElement
public class Balance extends TimeSensitiveKenanObject {
	private static final long serialVersionUID = 989832978520419350L;
	protected static final Logger logger = LoggerFactory.getLogger(Balance.class);
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

	public Balance(BigMoney value) {
		this.value = value;
		loaded = true;
	}

	/* **************************************************
	 * Getter and Setter Methods
	 */

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

	@Override
	public void reset() {
		super.reset();
		value = null;
	}

	@Override
	public void refresh() {
		BigMoney temp = loadValue();
		if (temp != null) {
			super.refresh();
			value = temp;
		}
	}

	@Override
	public void load() {
		reset();
		value = loadValue();
	}

	@Override
	protected BigMoney loadValue() {
		try {
			return AccountService.getBalance(accountNo).getValue();
		} catch (BalanceFetchException e) {
			logger.error("Error loading Balance for Account {}", accountNo);
		} finally {
			loaded = true;
		}
		return null;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Balance [account=" + accountNo + ", value=" + value + ", stale=" + isStale() + "]";
	}

}