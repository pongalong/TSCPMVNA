package com.tscp.jaxb.xml.adapter;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeAdapter extends XmlAdapter<Date, DateTime> {
	protected static final Logger logger = LoggerFactory.getLogger(DateTimeAdapter.class);

	public DateTime unmarshal(
			Date v) throws Exception {
		try {
			return v == null ? null : new DateTime(v);
		} catch (Exception e) {
			logger.error("Error unmarshaling {} to DateTime", v, e);
			return null;
		}
	}

	public Date marshal(
			DateTime v) throws Exception {
		try {
			return v == null ? null : v.toDate();
		} catch (Exception e) {
			logger.error("Error marshaling {} to String", v, e);
			return null;
		}
	}

	// @Override
	// public DateTime unmarshal(Calendar calendar) throws Exception {
	// if (calendar == null)
	// return null;
	//
	// return new DateTime(
	// calendar.get(Calendar.YEAR),
	// // Correct for the difference between Joda DateTime and JDK Calendar.
	// calendar.get(Calendar.MONTH) + 1,
	// calendar.get(Calendar.DATE),
	// calendar.get(Calendar.HOUR_OF_DAY),
	// calendar.get(Calendar.MINUTE),
	// calendar.get(Calendar.SECOND),
	// calendar.get(Calendar.MILLISECOND),
	// DateTimeZone.forTimeZone(calendar.getTimeZone()));
	// }
	//
	// @Override
	// public Calendar marshal(DateTime v) throws Exception {
	// if (v == null)
	// return null;
	// Calendar calendar = Calendar.getInstance(v.getZone().toTimeZone());
	// calendar.setTime(v.toDate());
	// return calendar;
	// }

}