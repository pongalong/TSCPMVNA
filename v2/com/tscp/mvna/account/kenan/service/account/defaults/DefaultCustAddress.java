package com.tscp.mvna.account.kenan.service.account.defaults;

import com.telscape.billingserviceinterface.CustAddress;
import com.tscp.mvna.account.kenan.Address;
import com.tscp.mvne.config.PROVISION;

public class DefaultCustAddress extends CustAddress {

	// TODO change this to a properties or database fetch
	public DefaultCustAddress() {
		setAddress1("");
		setAddress2("");
		setAddress3("");
		setCity("");
		setState("");
		setZip("");
		setCountryCode(PROVISION.SERVICE.COUNTRY.shortValue());
		setFranchiseTaxCode(PROVISION.SERVICE.SERVICE_FRANCHISE_TAX.shortValue());
		setCounty("");

		// setAddress1("355 S Grand Ave");
		// setAddress2("");
		// setAddress3("");
		// setCity("Los Angeles");
		// setCountryCode(BILLING.customerCountryCode.shortValue());
		// setCounty("Los Angeles");
		// setFranchiseTaxCode(BILLING.customerFranchiseTaxCode.shortValue());
		// setState("CA");
		// setZip("90071");
	}

	public DefaultCustAddress(Address address) {
		this();
		setAddress1(address.getAddress1());
		setAddress2(address.getAddress2());
		setAddress3(address.getAddress3());
		setCity(address.getCity());
		setState(address.getState());
		setZip(address.getZip());
	}

}