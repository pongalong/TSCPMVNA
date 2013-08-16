package com.tscp.mvna.account.kenan;

import java.util.ArrayList;
import java.util.List;

public abstract class KenanObjectCollection<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1413117768283701186L;
	protected boolean loaded;

	public void reset() {
		super.clear();
		loaded = false;
	}

	public void load() {
		reset();
		super.addAll(loadValue());
	}

	/* **************************************************
	 * Getters and Setters
	 */

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(
			boolean loaded) {
		this.loaded = loaded;
	}

	/* **************************************************
	 * Abstract Methods
	 */

	protected abstract List<T> loadValue();

}