package com.tscp.jaxb.xsd;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

public class XSDateTimeCustomBinder {

	public static Date parseDateTime(
			String s) {
		return DatatypeConverter.parseDate(s).getTime();
	}

	public static String printDateTime(
			Date dt) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(dt);
		return DatatypeConverter.printDate(cal);
	}

}
