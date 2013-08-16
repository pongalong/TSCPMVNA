package com.tscp.mvna.account.kenan;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.tscp.mvna.TimeSensitive;
import com.tscp.mvne.config.CONFIG;

public abstract class TimeSensitiveKenanObjectCollection<T> extends KenanObjectCollection<T> implements TimeSensitive {
	private static final long serialVersionUID = -311585095860473160L;
	protected DateTime lastRefresh = new DateTime();

	@Override
	public boolean isStale() {
		Period elapsed = new Period(lastRefresh, new DateTime());
		return elapsed.getMinutes() > CONFIG.defaultStaleTime;
	}

	@Override
	public T get(
			int index) {
		if (super.isEmpty() || !loaded)
			refresh();
		else if (loaded && isStale())
			refresh();
		return super.get(index);
	}

	@Override
	public void reset() {
		super.clear();
		loaded = false;
		lastRefresh = new DateTime();
	}

	@Override
	public void refresh() {
		List<T> temp = (List<T>) loadValue();
		if (temp != null) {
			reset();
			super.addAll(temp);
		}
	}

}