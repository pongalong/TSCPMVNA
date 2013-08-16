package com.tscp.mvna.account.kenan;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.tscp.mvna.TimeSensitive;
import com.tscp.mvne.config.CONFIG;

public abstract class TimeSensitiveKenanObject extends KenanObject implements TimeSensitive {
	private static final long serialVersionUID = 6281388645367897504L;
	protected DateTime lastRefresh = new DateTime();

	@Override
	public boolean isStale() {
		Period elapsed = new Period(lastRefresh, new DateTime());
		return elapsed.getMinutes() > CONFIG.defaultStaleTime;
	}

	@Override
	public void refresh() {
		super.reset();
	}

}