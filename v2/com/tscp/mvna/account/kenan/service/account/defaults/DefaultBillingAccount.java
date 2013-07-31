package com.tscp.mvna.account.kenan.service.account.defaults;

import com.telscape.billingserviceinterface.BillingAccount;
import com.tscp.mvne.config.BILLING;
import com.tscp.util.DateUtils;

public class DefaultBillingAccount extends BillingAccount {

	public DefaultBillingAccount() {
		super.setBillName(new DefaultBillingName());
		super.setContactInfo(new DefaultContactInfo());
		super.setCustAddress(new DefaultCustAddress());
		super.setBillAddress(super.getCustAddress());

		setAccountCategory(BILLING.accountCategory.shortValue());
		setBillDispMethod(BILLING.billDisplayMethod.shortValue());
		setBillFormatOpt(BILLING.billFormatOption);

		setBillPeriod(BILLING.billPeriod.toString());
		setCCardIdServ(BILLING.defaultCreditCardIdServ.shortValue());
		setCollectionIndicator(BILLING.collectionIndicator.shortValue());

		setCreditThresh(BILLING.creditThreshold.toString());
		setCredStatus(BILLING.creditStatus.shortValue());
		setCurrencyCode(BILLING.currencyCode.shortValue());

		setCustEmail("tscwebgeek@telscape.net");
		setCustFaxNo("");
		setCustPhone1("2133880022");
		setCustPhone2("");

		setExrateClass(BILLING.exrateClass.shortValue());
		setExternalAccountNoType(BILLING.accountType.shortValue());
		setInsertGrpId(BILLING.insertGroupId.shortValue());

		setLanguageCode(BILLING.languageCode.shortValue());
		setMarketCode(BILLING.marketCode.shortValue());
		setMsgGroupId(BILLING.messageGroupId.shortValue());

		setOwningCostCtr(BILLING.owningCostCenter.shortValue());
		setPaymentMethod(BILLING.paymentMethod.shortValue());

		setRateClassDefault(BILLING.rateClassDefault.shortValue());

		// TODO Double check this field
		setServiceCenterId(BILLING.serviceCenterId.shortValue());
		setServiceCenterType(BILLING.serviceCenterType.shortValue());

		setSicCode(BILLING.sicCode.shortValue());
		setTieCode(BILLING.tieCode.shortValue());

		setVipCode(BILLING.vipCode.shortValue());

		setSysDate(DateUtils.getXMLCalendar());
		setAccountDateActive(DateUtils.getXMLCalendar());
	}

}