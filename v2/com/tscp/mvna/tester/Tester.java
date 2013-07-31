package com.tscp.mvna.tester;

import java.util.Set;

import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.device.Device;
import com.tscp.mvna.account.device.DeviceAndService;
import com.tscp.mvna.account.device.network.NetworkStatus;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.user.customer.Customer;

public class Tester {

	public static void main(
			String[] args) {
		System.out.println("Hello world!");

		Customer customer = (Customer) Dao.get(Customer.class, 5);

		printAccounts(customer.getAccounts());
		printDevices(customer.getDevices());

		// Device device = customer.getDevice(24111);
		// cycleDevice(device);
	}

	protected static void cycleDevice(
			Device device) throws Exception {
		try {
			if (device.getStatus() != NetworkStatus.C) {
				device.disconnect();
				Thread.sleep(5000);
			}
			if (device.getStatus() == NetworkStatus.C) {
				device.connect();
				Thread.sleep(5000);
			}
			if (device.getStatus() == NetworkStatus.A) {
				device.suspend();
				Thread.sleep(5000);
			}
			if (device.getStatus() == NetworkStatus.S) {
				device.restore();
			}
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted!");
		}
	}

	protected static void printAccounts(
			Set<Account> accounts) {
		for (Account account : accounts)
			if (!account.isEmpty())
				System.out.println("  " + account.toString());
	}

	protected static void printDevices(
			Set<DeviceAndService> devices) {
		for (DeviceAndService device : devices) {
			System.out.println("  ..." + device.toString() + " " + device.getNetworkInfo());
			System.out.println("    ... with payment method" + device.getPaymentMethod().toString());
			System.out.println("    ... on account " + device.getAccount().toString());
		}
	}

}