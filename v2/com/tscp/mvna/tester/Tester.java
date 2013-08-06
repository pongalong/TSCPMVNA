package com.tscp.mvna.tester;

import com.tscp.mvna.account.payment.PaymentRequest;
import com.tscp.mvna.payment.method.CreditCard;
import com.tscp.mvna.ws.TSCPMVNA2;

public class Tester {

	public static void main(
			String[] args) {
		try {
			System.out.println("Hello world!");

			PaymentRequest pr = new PaymentRequest();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Goodbye world!");
		}

	}
}