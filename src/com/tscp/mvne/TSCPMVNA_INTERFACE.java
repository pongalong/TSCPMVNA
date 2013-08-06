package com.tscp.mvne;

import java.util.Date;
import java.util.List;

import com.tscp.mvna.account.device.network.NetworkException;
import com.tscp.mvna.account.device.network.OldNetworkInfo;
import com.tscp.mvna.account.device.network.exception.NetworkQueryException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvna.account.kenan.provision.exception.ProvisionException;
import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.contract.KenanContract;
import com.tscp.mvne.billing.exception.BillingException;
import com.tscp.mvne.billing.provisioning.ServiceInstance;
import com.tscp.mvne.customer.Customer;
import com.tscp.mvne.customer.DeviceException;
import com.tscp.mvne.customer.dao.CustTopUp;
import com.tscp.mvne.device.OldDevice;
import com.tscp.mvne.notification.dao.EmailNotification;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.dao.CustPmtMap;
import com.tscp.mvne.payment.dao.OldCreditCard;
import com.tscp.mvne.payment.dao.PaymentInvoice;
import com.tscp.mvne.payment.dao.PaymentRecord;
import com.tscp.mvne.payment.dao.PaymentTransaction;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;
import com.tscp.mvne.refund.KenanPayment;
import com.tscp.mvne.refund.RefundException;

public interface TSCPMVNA_INTERFACE {

	public abstract void refundPayment(
			int accountNo, int transId, String amount, int trackingId, String refundBy, int refundCode, String notes) throws RefundException;

	public abstract PaymentTransaction getPaymentTransaction(
			int custId, int transId) throws PaymentException;

	public abstract void applyChargeCredit(
			OldCreditCard creditCard, String amount);

	public abstract int applyContract(
			KenanContract contract);

	public abstract int applyCouponPayment(
			Account account, String amount, Date date);

	public abstract Account createBillingAccount(
			Customer customer, Account account);

	public abstract List<KenanContract> getContracts(
			Account account, ServiceInstance serviceInstance);

	public abstract PaymentInvoice getCustomerInvoice(
			Customer customer, int transId);

	public abstract List<CustPmtMap> getCustPaymentList(
			int customerId, int paymentId);

	public abstract List<KenanPayment> getKenanPayments(
			Account account);

	public abstract List<PaymentRecord> getPaymentHistory(
			Customer customer);

	public abstract PaymentUnitResponse makePaymentById(
			String sessionId, int custId, int accountNo, int paymentId, String amount) throws ProvisionException, BillingException, DeviceException, NetworkException, RestoreException, NetworkQueryException, SuspendException;

	public abstract void reverseKenanPayment(
			Account account, String amount, Date transDate, String trackingId);

	public abstract void sendNotification(
			Customer customer, EmailNotification notification);

	public abstract void sendActivationSuccessNotice(
			Customer customer, Account account);

	public abstract void sendRegistrationSuccessNotice(
			int custId, String email, String username);

	public abstract CustTopUp setCustTopUpAmount(
			Customer customer, String topUpAmount, Account account);

	public abstract PaymentUnitResponse submitPaymentByCreditCard(
			String sessionId, Account account, OldCreditCard creditCard, String paymentAmount);

	public abstract PaymentUnitResponse submitPaymentByPaymentId(
			String sessionId, Customer customer, int paymentId, Account account, String paymentAmount) throws ProvisionException, BillingException, DeviceException, NetworkException, RestoreException, NetworkQueryException, SuspendException;

	public abstract OldNetworkInfo swapDevice(
			Customer customer, OldNetworkInfo oldNetworkInfo, OldDevice newDevice) throws NetworkException, NetworkQueryException;

	public abstract void updateContract(
			KenanContract contract);

}