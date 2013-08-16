package com.tscp.mvna.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.TimeSensitiveKenanObjectCollection;
import com.tscp.mvna.account.kenan.exception.PaymentFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;

@XmlRootElement
public class PaymentHistory extends TimeSensitiveKenanObjectCollection<PaymentRequest> {
	private static final long serialVersionUID = -3380526992973052463L;
	protected static final Logger logger = LoggerFactory.getLogger(PaymentHistory.class);
	private int accountNo;

	/* **************************************************
	 * Constructors
	 */

	public PaymentHistory(int accountNo) {
		this.accountNo = accountNo;
	}

	public PaymentHistory(List<PaymentRequest> paymentRequests) {
		super.addAll(paymentRequests);
		loaded = true;
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	protected List<PaymentRequest> loadValue() {
		try {
			return AccountService.getPaymentRequests(accountNo);
		} catch (PaymentFetchException e) {
			logger.error("Error loading PaymentHistory for Account {}", accountNo, e);
		} finally {
			loaded = true;
		}
		return null;
	}

}