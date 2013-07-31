package com.tscp.util;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
	protected static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
	protected static final DateTimeFormatter serviceInstance = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss aa");

	public static DateTime getServiceDateTime(
			String inDate) {
		if (inDate != null && !inDate.trim().isEmpty()) {
			return serviceInstance.parseDateTime(inDate);
		} else {
			return null;
		}
	}

	public static Date getServiceDate(
			String inDate) {
		if (inDate != null && !inDate.trim().isEmpty()) {
			return serviceInstance.parseDateTime(inDate).toDate();
		} else {
			return null;
		}
	}

	public static final XMLGregorianCalendar getXMLCalendar() {
		return getXMLCalendar(new Date());
	}

	public static final XMLGregorianCalendar getXMLCalendar(
			DateTime dateTime) {
		return dateTime == null ? null : getXMLCalendar(dateTime.toDate());
	}

	public static final XMLGregorianCalendar getXMLCalendar(
			Date date) {
		try {
			return date == null ? null : DatatypeFactory.newInstance().newXMLGregorianCalendar(getGregorianCalendar(date));
		} catch (DatatypeConfigurationException e) {
			logger.error("Unable to convert Date to XMLGregorianCalendar", e);
			return null;
		}
	}

	public static final GregorianCalendar getGregorianCalendar(
			Date date) {
		GregorianCalendar gregoriainCalendar = new GregorianCalendar();
		gregoriainCalendar.setTime(date);
		return gregoriainCalendar;
	}

	public static final boolean sameYear(
			DateTime dateTime1, DateTime dateTime2) {
		if (dateTime1 == null && dateTime2 == null)
			return true;
		if (dateTime1 == null)
			return false;
		if (dateTime2 == null)
			return false;
		return dateTime1.getYear() == dateTime2.getYear();
	}

	public static final boolean sameMonth(
			DateTime dateTime1, DateTime dateTime2) {
		return sameYear(dateTime1, dateTime2) && dateTime1.getMonthOfYear() == dateTime2.getMonthOfYear();
	}

	public static final boolean sameDay(
			DateTime dateTime1, DateTime dateTime2) {
		return sameMonth(dateTime1, dateTime2) && dateTime1.getMonthOfYear() == dateTime2.getMonthOfYear();
	}

}