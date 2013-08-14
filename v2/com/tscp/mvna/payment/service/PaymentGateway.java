package com.tscp.mvna.payment.service;

import java.util.List;

import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.exception.PaymentGatewayException;
import com.tscp.mvna.payment.method.CreditCard;
import com.tscp.mvna.user.UserEntity;
import com.tscp.util.profiler.Profiler;

public class PaymentGateway {
	protected static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
	private static final MoneyFormatter gatewayCurrencyFormatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.LOCALIZED_NO_GROUPING).toFormatter();

	protected static PaymentGatewayResponse submitPayment(
			UserEntity requester, CreditCard creditCard, Money amount) throws PaymentGatewayException {

		profiler.start();
		@SuppressWarnings("unchecked")
		List<PaymentGatewayResponse> results = Dao.executeNamedQuery("submit_payment", requester.getId(), creditCard.getId(), gatewayCurrencyFormatter.print(amount));

		if (results == null || results.isEmpty())
			throw new PaymentGatewayException("No response received");

		PaymentGatewayResponse result = results.get(0);

		stopProfiling(result);
		return result;
	}

	/* **************************************************
	 * Profiler Methods
	 */

	private static Profiler profiler = new Profiler();

	public static Profiler getProfiler() {
		return profiler;
	}

	private static void stopProfiling(
			PaymentGatewayResponse response) {

		String key;

		if (response.isSuccess())
			key = "SUCCESS";
		else
			key = response.getAuthorizationCode() != null ? response.getAuthorizationCode() : response.getConfirmationMsg();

		profiler.stop(key);
	}

}