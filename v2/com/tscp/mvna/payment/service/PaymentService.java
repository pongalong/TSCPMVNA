package com.tscp.mvna.payment.service;

import java.math.RoundingMode;

import org.hibernate.HibernateException;
import org.joda.money.CurrencyUnit;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.kenan.service.AccountService;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.PaymentRecord;
import com.tscp.mvna.payment.PaymentRequest;
import com.tscp.mvna.payment.PaymentResponse;
import com.tscp.mvna.payment.PaymentTransaction;
import com.tscp.mvna.payment.exception.PaymentGatewayException;
import com.tscp.mvna.payment.exception.PaymentServiceException;

public class PaymentService extends PaymentGateway {
	protected static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
	public static final CurrencyUnit CURRENCY = CurrencyUnit.USD;
	public static final RoundingMode roundingMode = RoundingMode.DOWN;
	public static final MoneyFormatter stringFormatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.LOCALIZED_NO_GROUPING).toFormatter();
	public static final MoneyFormatter currencyStringFormatter = new MoneyFormatterBuilder().appendCurrencySymbolLocalized().append(stringFormatter).toFormatter();

	public static PaymentTransaction beginTransaction(
			DeviceAndService device) {
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		paymentTransaction.setPaymentRequest(new PaymentRequest(device));
		return paymentTransaction;
	}

	public static PaymentRequest submitRequest(
			PaymentTransaction paymentTransaction) throws PaymentServiceException {
		paymentTransaction.setPaymentRequest(submitRequest(paymentTransaction.getPaymentRequest()));
		return paymentTransaction.getPaymentRequest();
	}

	protected static PaymentRequest submitRequest(
			PaymentRequest paymentRequest) throws PaymentServiceException {

		try {
			Dao.save(paymentRequest);
		} catch (HibernateException e) {
			if (paymentRequest.getTransactionId() < 1)
				throw new PaymentServiceException(paymentRequest.getClass().getSimpleName() + " does not have a valid Transaction ID and cannot be saved");
			throw new PaymentServiceException("Unable to save PaymentRequest", e);
		}

		return paymentRequest;
	}

	public static PaymentResponse submitPayment(
			PaymentTransaction paymentTransaction) throws PaymentServiceException {
		paymentTransaction.setPaymentResponse(submitPayment(paymentTransaction.getPaymentRequest()));
		return paymentTransaction.getPaymentResponse();
	}

	protected static PaymentResponse submitPayment(
			PaymentRequest paymentRequest) throws PaymentServiceException {

		if (paymentRequest == null)
			throw new PaymentServiceException("No PaymentRequest to submit");
		if (paymentRequest.getTransactionId() < 1)
			throw new PaymentServiceException(paymentRequest.getClass().getSimpleName() + " does not have a valid Transaction ID");

		PaymentResponse paymentResponse = new PaymentResponse(paymentRequest);
		Object[] gatewayResponse;

		try {
			gatewayResponse = PaymentGateway.submitPayment(paymentRequest.getRequestBy(), paymentRequest.getCreditCard(), paymentRequest.getAmount());
			paymentResponse.setGatewayTransactionId((int) gatewayResponse[0]);
			paymentResponse.setConfirmationCode((String) gatewayResponse[1]);
			paymentResponse.setConfirmationMsg((String) gatewayResponse[2]);
			paymentResponse.setAuthorizationCode((String) gatewayResponse[3]);
			paymentResponse.setCvvCode((String) gatewayResponse[4]);
		} catch (PaymentGatewayException e) {
			logger.warn("Error posting payment to gateway for {}", paymentRequest, e);
			throw new PaymentServiceException(e.getMessage());
		}

		try {
			Dao.save(paymentResponse);
		} catch (HibernateException e) {
			throw new PaymentServiceException("Unable to save PaymentResponse", e);
		}

		return paymentResponse;
	}

	public static PaymentRecord submitRecord(
			PaymentTransaction paymentTransaction) throws PaymentServiceException {
		paymentTransaction.setPaymentRecord(saveRecord(paymentTransaction.getPaymentResponse()));
		return paymentTransaction.getPaymentRecord();
	}

	protected static PaymentRecord saveRecord(
			PaymentResponse paymentResponse) throws PaymentServiceException {

		if (paymentResponse == null)
			throw new PaymentServiceException("No PaymentResponse to record");
		if (paymentResponse.getTransactionId() < 1)
			throw new PaymentServiceException(paymentResponse.getClass().getSimpleName() + " does not have a valid Transaction ID");

		if (!paymentResponse.isSuccess())
			return null;

		PaymentRecord paymentRecord = AccountService.addPayment(paymentResponse);

		try {
			Dao.save(paymentRecord);
		} catch (HibernateException e) {
			throw new PaymentServiceException("Unable to save PaymentRecord", e);
		}
		return paymentRecord;
	}

}