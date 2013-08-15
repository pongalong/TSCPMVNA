package com.tscp.mvna.tester;

import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.device.network.service.NetworkGateway;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.PaymentTransaction;
import com.tscp.mvna.payment.service.PaymentGateway;
import com.tscp.mvna.ws.TSCPMVNA2;
import com.tscp.util.profiler.Profiler;

public class Tester {

	public static void main(
			String[] args) {
		try {
			System.out.println("Hello world!\n");
			run();
		} catch (Exception e) {
			System.err.println("Global Exception caught: " + e.getMessage());
		} finally {
			System.out.println("\nGoodbye world!");
		}
	}

	public static void run() {
		TSCPMVNA2 port = new TSCPMVNA2();

		DeviceAndService device = port.getDevice(24111);

		try {
			// PaymentTransaction paymentTransaction = PaymentService.beginTransaction(device);
			// PaymentRequest request = PaymentService.submitRequest(paymentTransaction);
			// PaymentResponse response = PaymentService.submitPayment(paymentTransaction);
			// PaymentRecord record = PaymentService.submitRecord(paymentTransaction);

			// PaymentHistory paymentHistory = AccountService.getPaymentHistory(device.getAccountNo());
			// for (PaymentTransaction pt : paymentHistory.getPayments())
			// System.out.println(pt);

			PaymentTransaction pt = (PaymentTransaction) Dao.get(PaymentTransaction.class, 69068);
			System.out.println(pt);

		} catch (Exception e) {
			System.err.println("PaymentException caught: " + e.getMessage());
		} finally {
			System.out.println("* Payment Gateway Statistics*");
			Profiler paymentProfiler = PaymentGateway.getProfiler();
			System.out.println(paymentProfiler.getResultMap());
			System.out.println("\n* Network Gateway Statistics*");
			Profiler networkProfiler = NetworkGateway.getProfiler();
			System.out.println(networkProfiler.getResultMap());
			System.out.println("\n* DAO Statistics*");
			Profiler daoProfiler = Dao.getProfiler();
			System.out.println(daoProfiler.getResultMap());
		}
	}
}