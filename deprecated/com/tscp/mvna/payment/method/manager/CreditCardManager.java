package com.tscp.mvna.payment.method.manager;

import com.tscp.mvne.payment.dao.OldCreditCard;


public class CreditCardManager {

	@Deprecated
	public static String getMaskedNumber(
			OldCreditCard cc) {
		String result = cc.getCreditCardNumber().substring(0, 1);
		for (int i = 1; i < cc.getCreditCardNumber().length(); i++) {
			if (i < cc.getCreditCardNumber().length() - 4)
				result += "X";
			else
				result += cc.getCreditCardNumber().substring(i, i + 1);
		}
		return result;
	}

	@Deprecated
	public static String getAlias(
			OldCreditCard cc) {

		int myFirstCardNumber = Integer.parseInt(cc.getCreditCardNumber().substring(0, 1));
		String alias = " " + cc.getCreditCardNumber().substring(cc.getCreditCardNumber().length() - 4, cc.getCreditCardNumber().length());

		switch (myFirstCardNumber) {
			case 3:
				alias = "AMEX" + alias;
				break;
			case 4:
				alias = "VISA" + alias;
				break;
			case 5:
				alias = "MasterCard" + alias;
				break;
			case 6:
				alias = "Discover" + alias;
				break;
		}

		return alias;
	}

}