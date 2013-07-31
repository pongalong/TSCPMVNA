package com.tscp.mvna.account.kenan.service.account.defaults;

import com.telscape.billingserviceinterface.ContactInfo;
import com.tscp.mvna.account.Contact;

public class DefaultContactInfo extends ContactInfo {

	// TODO change this to a properties or database fetch
	public DefaultContactInfo() {
		setContact1Name("");
		setContact1Phone("");
	}

	public DefaultContactInfo(Contact contact) {
		this();
		this.setContact1Name(buildContactName(contact));
	}

	protected String buildContactName(
			Contact contact) {

		String contactName = "";
		boolean addSpace = false;

		if (contact.hasFirstName()) {
			contactName += contact.getFirstName();
			addSpace = true;
		}

		if (contact.hasMiddleName()) {
			if (addSpace)
				contactName += " ";
			contactName += contact.getMiddleName();
			addSpace = true;
		}

		if (contact.hasLastName()) {
			if (addSpace)
				contactName += " ";
			contactName += contact.getLastName();
			addSpace = true;
		}

		return contactName;
	}
}