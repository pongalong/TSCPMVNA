package com.tscp.mvna;

public interface TimeSensitive {

	/**
	 * Returns TRUE if the object is no longer fresh.
	 * 
	 * @return
	 */
	public boolean isStale();

	/**
	 * Reloads the object if new values are available, otherwise the object will retain old values.
	 */
	public void refresh();

}