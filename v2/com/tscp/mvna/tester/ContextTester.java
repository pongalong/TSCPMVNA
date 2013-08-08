package com.tscp.mvna.tester;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.tscp.mvna.account.Account;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.user.Customer;

@SuppressWarnings("unused")
public class ContextTester {
	static JAXBContext jc;
	static Marshaller marshaller;
	// static Customer customer = (Customer) Dao.get(Customer.class, 5);

	static Account account = (Account) Dao.get(Account.class, 757594);

	// static DeviceAndService serviceDevice = (DeviceAndService) Dao.get(DeviceAndService.class, 1409);
	// static NewCreditCard creditCard = (NewCreditCard) Dao.get(NewCreditCard.class, 12398);
	// static NewPaymentInfo paymentInfo = (NewPaymentInfo) Dao.get(NewPaymentInfo.class, 12398);

	public static void main(
			String[] args) throws Exception {

		jc = JAXBContext.newInstance(Customer.class);
		marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		System.out.println("Hello world! jaxbContext=" + jc.getClass().toString());

		marshallTest();
	}

	protected static void marshallTest() throws JAXBException {
		marshaller.marshal(account, System.out);

		// for (DeviceAndService device : customer.getDevices()) {
		// System.out.println("     " + device.getServicePackage().getComponentList());
		// }

		System.out.println();
	}

	protected static void unmarshallTest() throws JAXBException {
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StreamSource xml = new StreamSource("manager/com/tscp/mvna/tester/customer.xml");

		Customer xmlCustomer = (Customer) unmarshaller.unmarshal(xml);
		// Tester.printAccounts(xmlCustomer.getAccounts());
		// Tester.printDevices(xmlCustomer.getDevices());
	}
}