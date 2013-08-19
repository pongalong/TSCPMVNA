package com.tscp.mvna.tester;

import com.tscp.mvna.Problem;
import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.device.network.service.NetworkGateway;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.service.PaymentGateway;
import com.tscp.mvna.ws.TSCPMVNA2;

public class Tester {

	public static void run() {
		TSCPMVNA2 port = new TSCPMVNA2();
		try {

			DeviceAndService device = port.getDevice(24111);
			for (Problem p : device.getIntegrity().getProblemObjects()) {
				System.out.println(p.getDescription());
			}

			// PaymentHistory paymentHistory = new PaymentHistory(device.getAccountNo());
			// paymentHistory.load();
			//
			// for (PaymentRequest pr : paymentHistory) {
			// if (pr.getPaymentResponse() != null) {
			// System.out.println(pr);
			// System.out.println("    " + pr.getPaymentResponse());
			// if (pr.getPaymentResponse().getPaymentRecord() != null)
			// System.out.println("        " + pr.getPaymentResponse().getPaymentRecord());
			// }
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(
			String[] args) {
		try {
			System.out.println("Hello world!\n");
			run();
		} catch (Exception e) {
			System.err.println("Global Exception caught: " + e.getMessage());
		} finally {
			System.out.println("***Payment Gateway Statistics***");
			System.out.println(PaymentGateway.getProfiler().getResultMap());
			System.out.println("***Network Gateway Statistics***");
			System.out.println(NetworkGateway.getProfiler().getResultMap());
			System.out.println("***DAO Statistics***");
			System.out.println(Dao.getProfiler().getResultMap());
			System.out.println("\nGoodbye world!");
		}
	}

}