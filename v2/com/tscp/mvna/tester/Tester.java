package com.tscp.mvna.tester;

import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.kenan.exception.PaymentFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;
import com.tscp.mvna.payment.PaymentHistory;
import com.tscp.mvna.payment.PaymentRequest;
import com.tscp.mvna.payment.PaymentResponse;
import com.tscp.mvna.payment.PaymentTransaction;
import com.tscp.mvna.payment.exception.PaymentGatewayException;
import com.tscp.mvna.payment.exception.PaymentServiceException;
import com.tscp.mvna.payment.service.PaymentService;
import com.tscp.mvna.ws.TSCPMVNA2;

public class Tester {

	public static void main(
			String[] args) {
		try {
			System.out.println("Hello world!");

			TSCPMVNA2 port = new TSCPMVNA2();

			DeviceAndService device = port.getDevice(24111);
			System.out.println(device.getAccount());

			try {
				PaymentRequest request = PaymentService.submitRequest(device);
				PaymentResponse response = PaymentService.submitPayment(request);

				if (response.isSuccess()) {
					System.out.println("PAYMENT SUCCESS!");
					PaymentService.submitRecord(response);
				} else {
					System.err.println("PAYMENT FAILURE: " + response.getAuthorizationCode());
				}

			} catch (PaymentServiceException | PaymentGatewayException e) {
				System.err.println("PaymentException caught: " + e.getMessage());
			}

			try {
				PaymentHistory history = AccountService.getPaymentHistory(device.getAccountNo());
				for (PaymentTransaction pt : history.getPayments()) {
					System.out.println("found");
				}
			} catch (PaymentFetchException e) {
				System.err.println("PaymentFetchException caught: " + e.getMessage());
			}

		} catch (Exception e) {
			System.err.println("Global Exception caught: " + e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("Goodbye world!");
		}

	}
}