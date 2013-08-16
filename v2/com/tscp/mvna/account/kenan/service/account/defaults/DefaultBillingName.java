package com.tscp.mvna.account.kenan.service.account.defaults;

import com.telscape.billingserviceinterface.BillName;
import com.tscp.mvna.account.kenan.Contact;

public class DefaultBillingName extends BillName {

	// TODO change this to a properties or database fetch
	public DefaultBillingName() {
		setFirstName("Shell Account");
		setMiddleName("");
		setLastName("Web On The Go");
	}

	public DefaultBillingName(Contact contact) {
		this();
		setFirstName(contact.getFirstName());
		setMiddleName(contact.getMiddleName());
		setLastName(contact.getLastName());
	}

}