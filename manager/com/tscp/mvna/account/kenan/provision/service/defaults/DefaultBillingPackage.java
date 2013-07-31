package com.tscp.mvna.account.kenan.provision.service.defaults;

import com.telscape.billingserviceinterface.Package;
import com.tscp.mvne.config.PROVISION;
import com.tscp.util.DateUtils;

public class DefaultBillingPackage extends Package {

	public DefaultBillingPackage() {
		setPackageId(PROVISION.PACKAGE.ID);
		setExternalIdType(PROVISION.PACKAGE.EXTERNAL_ID_TYPE.shortValue());
		setPackageInstanceIdServ(PROVISION.PACKAGE.INSTANCE_SERV_ID.shortValue());
		setActiveDate(DateUtils.getXMLCalendar());
		setAccountNo("");
	}

	// public DefaultBillingPackage(int accountNo) {
	// this();
	// setAccountNo(Integer.toString(accountNo));
	// }

}