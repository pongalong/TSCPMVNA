package com.tscp.mvna.payment.service;

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

	/*
	 * This method returns a Hibernate Entity made specifically for this query, resulting in larger overhead. Using
	 * submitPayment which uses scalars returns and Object[]
	 * 
	 * @Deprecated protected static PaymentGatewayResponse submitPayment_Entity( UserEntity requester, CreditCard
	 * creditCard, Money amount) throws PaymentGatewayException { profiler.start();
	 * 
	 * @SuppressWarnings("unchecked") List<PaymentGatewayResponse> results = Dao.executeNamedQuery("submit_payment",
	 * requester.getId(), creditCard.getId(), gatewayCurrencyFormatter.print(amount)); if (results == null ||
	 * results.isEmpty()) throw new PaymentGatewayException("No response received"); PaymentGatewayResponse result =
	 * results.get(0); stopProfiling(result); return result; }
	 * 
	 * private static void stopProfiling( PaymentGatewayResponse response) { String key; if (response.isSuccess()) key =
	 * "SUCCESS"; else key = response.getAuthorizationCode() != null ? response.getAuthorizationCode() :
	 * response.getConfirmationMsg(); profiler.stop(key); }
	 */

	protected static Object[] submitPayment(
			UserEntity requester, CreditCard creditCard, Money amount) throws PaymentGatewayException {
		profiler.start();
		Object[] result = Dao.listScalar("submit_payment", requester.getId(), creditCard.getId(), gatewayCurrencyFormatter.print(amount));
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

	public static void stopProfiling(
			Object[] response) {
		String key;

		if (((String) response[1]).equals("0"))
			key = "SUCCESS";
		else
			key = (String) (response[3] != null ? response[3] : response[2]);

		profiler.stop(key);
	}

}