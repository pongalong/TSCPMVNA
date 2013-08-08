package com.tscp.mvna.tester;

import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.payment.PaymentRequest;
import com.tscp.mvna.payment.PaymentResponse;
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

			try {
				PaymentRequest request = PaymentService.submitRequest(device);
				PaymentResponse response = PaymentService.submitPayment(request);
				if (response.isSuccess())
					System.out.println("PAYMENT SUCCESS!");
				else
					System.err.println("PAYMENT FAILURE: " + response.getAuthorizationCode());
			} catch (PaymentServiceException | PaymentGatewayException e) {
				System.err.println("PaymentException caught: " + e.getMessage());
			}

		} catch (Exception e) {
			System.err.println("Global Exception caught: " + e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("Goodbye world!");
		}

	}
}