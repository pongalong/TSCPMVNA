package com.tscp.mvna.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.oracle.webservices.api.databinding.DatabindingMode;
import com.tscp.mvna.dao.Dao;

@WebService
@DatabindingMode(value = "eclipselink.jaxb")
public class TSCPMVNA2Debugger {

	@WebMethod
	public int getOpenSessionCount() {
		return Dao.getOpenSessionCount();
	}

	@WebMethod
	public int getMaxOpenSessionCount() {
		return Dao.getMaxOpenSessionCount();
	}

}
