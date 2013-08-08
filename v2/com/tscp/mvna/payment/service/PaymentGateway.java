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

public class PaymentGateway {
	protected static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
	private static final MoneyFormatter gatewayCurrencyFormatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.LOCALIZED_NO_GROUPING).toFormatter();

	protected static PaymentGatewayResponseEntity submitPayment(
			UserEntity requester, CreditCard creditCard, Money amount) throws PaymentGatewayException {

		@SuppressWarnings("unchecked")
		List<PaymentGatewayResponseEntity> result = Dao.executeNamedQuery("submit_payment", requester.getId(), creditCard.getId(), gatewayCurrencyFormatter.print(amount));

		if (result == null || result.isEmpty())
			throw new PaymentGatewayException("No response received");
		else
			return result.get(0);
	}

}