package com.tscp.mvna;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;
import org.joda.time.Period;

public abstract class TimeSensitive {
	protected DateTime instantiationTime = new DateTime();
	protected int interval = 15;

	@XmlAttribute
	public boolean isStale() {
		Period period = new Period(instantiationTime, new DateTime());
		if (period.getMinutes() > interval)
			return true;
		return false;
	}

	@XmlTransient
	protected int getInterval() {
		return interval;
	}

	protected void setInterval(
			int interval) {
		this.interval = interval;
	}

}