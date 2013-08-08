package com.tscp.mvna.payment.service;

import java.math.RoundingMode;

import org.hibernate.HibernateException;
import org.joda.money.CurrencyUnit;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.Device;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.PaymentRequest;
import com.tscp.mvna.payment.PaymentResponse;
import com.tscp.mvna.payment.exception.PaymentGatewayException;
import com.tscp.mvna.payment.exception.PaymentServiceException;

public class PaymentService extends PaymentGateway {
	protected static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
	public static final CurrencyUnit CURRENCY = CurrencyUnit.USD;
	public static final RoundingMode roundingMode = RoundingMode.DOWN;
	public static final MoneyFormatter stringFormatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.LOCALIZED_NO_GROUPING).toFormatter();
	public static final MoneyFormatter currencyStringFormatter = new MoneyFormatterBuilder().appendCurrencySymbolLocalized().append(stringFormatter).toFormatter();

	public static PaymentRequest submitRequest(
			Device device) throws PaymentServiceException {

		PaymentRequest request = new PaymentRequest(device);

		try {
			Dao.save(request);
		} catch (HibernateException e) {
			if (request.getTransactionId() < 1)
				throw new PaymentServiceException(request.getClass().getSimpleName() + " does not have a valid Transaction ID");
			throw new PaymentServiceException(e);
		}

		return request;
	}

	public static PaymentResponse submitPayment(
			PaymentRequest paymentRequest) throws PaymentServiceException {

		if (paymentRequest.getTransactionId() < 1)
			throw new PaymentServiceException(paymentRequest.getClass().getSimpleName() + " does not have a valid Transaction ID");

		PaymentResponse paymentResponse = new PaymentResponse(paymentRequest);
		PaymentGatewayResponseEntity gatewayResponse;

		try {
			gatewayResponse = PaymentGateway.submitPayment(paymentRequest.getRequestBy(), paymentRequest.getCreditCard(), paymentRequest.getAmount());
		} catch (PaymentGatewayException e) {
			logger.warn("Error posting payment to gateway for {}", paymentRequest, e);
			throw new PaymentServiceException(e.getMessage());
		}

		try {
			paymentResponse.parseGatewayResponse(PaymentGateway.submitPayment(paymentRequest.getRequestBy(), paymentRequest.getCreditCard(), paymentRequest.getAmount()));
		} catch (PaymentGatewayException e) {
			logger.warn("Error parsing {}", gatewayResponse, e);
			throw new PaymentServiceException(e);
		}

		try {
			Dao.save(paymentResponse);
		} catch (HibernateException e) {
			throw new PaymentServiceException(e);
		}

		return paymentResponse;
	}
}