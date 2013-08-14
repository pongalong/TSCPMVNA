package com.tscp.mvna.ws;

import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.oracle.webservices.api.databinding.DatabindingMode;
import com.tscp.mvna.account.device.network.service.NetworkGateway;
import com.tscp.mvna.dao.Dao;
import com.tscp.mvna.payment.service.PaymentGateway;
import com.tscp.util.profiler.ProfilerEntry;

@WebService
@DatabindingMode(value = "eclipselink.jaxb")
public class TSCPMVNA2Debugger {

	@WebMethod
	public Map<String, ProfilerEntry> getPaymentGatewayStatistics() {
		return PaymentGateway.getProfiler().getResultMap();
	}

	@WebMethod
	public Map<String, ProfilerEntry> getNetworkGatewayStatistics() {
		return NetworkGateway.getProfiler().getResultMap();
	}

	@WebMethod
	public int getOpenSessionCount() {
		return Dao.getOpenSessionCount();
	}

	@WebMethod
	public int getMaxOpenSessionCount() {
		return Dao.getMaxOpenSessionCount();
	}

}