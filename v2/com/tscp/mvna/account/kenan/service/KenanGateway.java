package com.tscp.mvna.account.kenan.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.ws.client.BindingProviderProperties;
import com.telscape.billingserviceinterface.BillingServiceInterface;
import com.telscape.billingserviceinterface.BillingServiceInterfaceSoap;
import com.tscp.mvna.ws.exception.InitializationException;
import com.tscp.mvne.config.CONFIG;
import com.tscp.mvne.config.CONNECTION;

public abstract class KenanGateway {
	protected static final Logger logger = LoggerFactory.getLogger(KenanGateway.class);
	protected static final BillingServiceInterface service = loadService();
	protected static final BillingServiceInterfaceSoap port = getInstance();

	/**
	 * Loads and returns the billing interface.
	 * 
	 * @return
	 */
	private static final BillingServiceInterface loadService() throws InitializationException {
		CONFIG.initAll();
		try {
			BillingServiceInterface service = new BillingServiceInterface(new URL(CONNECTION.billingWSDL), new QName(CONNECTION.billingNameSpace, CONNECTION.billingServiceName));
			logger.info("KenanGateway initialized to " + service.getWSDLDocumentLocation().toString());
			return service;
		} catch (MalformedURLException url_ex) {
			logger.error("Exception initializing KenanGateway at " + CONNECTION.billingWSDL, url_ex);
			throw new InitializationException(url_ex);
		}
	}

	/**
	 * Returns the singleton instance of the billing interface.
	 * 
	 * @return
	 */
	protected static final BillingServiceInterfaceSoap getInstance() {
		BillingServiceInterfaceSoap port = service.getBillingServiceInterfaceSoap();
		Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
		requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, 180000);
		requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 180000);
		return port;
	}

	/**
	 * Returns the URL of the service.
	 * 
	 * @return
	 */
	protected static final URL getUrl() {
		return service.getWSDLDocumentLocation();
	}

}