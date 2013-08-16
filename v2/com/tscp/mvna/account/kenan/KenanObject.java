package com.tscp.mvna.account.kenan;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KenanObject implements Serializable {
	private static final long serialVersionUID = -7793324980986239824L;
	protected static final Logger logger = LoggerFactory.getLogger(KenanObject.class);
	protected boolean loaded;

	/**
	 * Reset to an empty state.
	 */
	public void reset() {
		loaded = false;
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

	/**
	 * Loads and sets all values.
	 */
	public abstract void load();

	/**
	 * Loads but does not set any values.
	 * 
	 * @return
	 */
	protected abstract Object loadValue();

}