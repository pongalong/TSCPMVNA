package com.tscp.xml.adapter;

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

}