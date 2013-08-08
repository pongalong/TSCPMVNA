package com.tscp.mvna;

public interface Cacheable {

	public String getCacheKey();

	public boolean isStale();

}
