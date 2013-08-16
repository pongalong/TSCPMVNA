package com.tscp.mvna.account.kenan;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.CustAddress;
import com.tscp.mvna.account.kenan.exception.ContactFetchException;
import com.tscp.mvna.account.kenan.service.AccountService;
import com.tscp.mvna.account.kenan.service.account.defaults.DefaultCustAddress;

@XmlRootElement
@XmlType(propOrder = {
		"address1",
		"address2",
		"address3",
		"city",
		"state",
		"zip" })
public class Address extends KenanObject {
	private static final long serialVersionUID = 8693917969138968240L;
	protected static final Logger logger = LoggerFactory.getLogger(Address.class);
	protected int accountNo;
	protected String address1;
	protected String address2;
	protected String address3;
	protected String city;
	protected String state;
	protected String zip;

	/* **************************************************
	 * Constructors
	 */

	public Address() {
		// do nothing
	}

	public Address(int accountNo) {
		this.accountNo = accountNo;
	}

	public Address(CustAddress custAddress) {
		address1 = custAddress.getAddress1();
		address2 = custAddress.getAddress2();
		address3 = custAddress.getAddress3();
		city = custAddress.getCity();
		state = custAddress.getState();
		zip = custAddress.getZip();
	}

	/* **************************************************
	 * Fetch Methods
	 */

	@Override
	public void reset() {
		super.reset();
		address1 = null;
		address2 = null;
		address3 = null;
		city = null;
		state = null;
		zip = null;
	}

	@Override
	public void load() {
		Address temp = loadValue();
		if (temp != null) {
			address1 = temp.getAddress1();
			address2 = temp.getAddress2();
			address3 = temp.getAddress3();
			city = temp.getCity();
			state = temp.getState();
			zip = temp.getZip();
		}
	}

	@Override
	protected Address loadValue() {
		try {
			return AccountService.getAddress(accountNo);
		} catch (ContactFetchException e) {
			logger.error("Error loading {}", this);
		} finally {
			loaded = true;
		}
		return null;
	}

	/* **************************************************
	 * Getter and Setter Methods
	 */

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(
			String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(
			String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(
			String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(
			String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(
			String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(
			String zip) {
		this.zip = zip;
	}

	/* **************************************************
	 * Adaptor Methods
	 */

	public CustAddress toCustAddress() {
		return new DefaultCustAddress(this);
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "Address [account=" + accountNo + ", address1=" + address1 + ", zip=" + zip + "]";
	}

}