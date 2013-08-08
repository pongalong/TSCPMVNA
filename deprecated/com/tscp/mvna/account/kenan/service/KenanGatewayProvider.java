package com.tscp.mvna.account.kenan.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.BillingServiceInterface;
import com.telscape.billingserviceinterface.BillingServiceInterfaceSoap;
import com.tscp.mvna.ws.exception.InitializationException;
import com.tscp.mvne.config.CONFIG;
import com.tscp.mvne.config.CONNECTION;

/**
 * Instantiates and provides access to a singleton of BillingServiceInterfaceSoap.
 * 
 * @author Tachikoma
 * 
 */
@Deprecated
public final class KenanGatewayProvider {
	private static final Logger logger = LoggerFactory.getLogger("TSCPMVNA");
	private static final BillingServiceInterface serviceInterface = loadInterface();
	private static final BillingServiceInterfaceSoap port = serviceInterface.getBillingServiceInterfaceSoap();

	protected KenanGatewayProvider() {
		// prevent instantiation
	}

	/**
	 * Loads and returns the billing interface.
	 * 
	 * @return
	 */
	protected static final BillingServiceInterface loadInterface() throws InitializationException {
		CONFIG.initAll();
		try {
			URL url = new URL(CONNECTION.billingWSDL);
			QName qName = new QName(CONNECTION.billingNameSpace, CONNECTION.billingServiceName);
			logger.info("{} has been initialized WSDL:{}", CONNECTION.billingServiceName, CONNECTION.billingWSDL);
			return new BillingServiceInterface(url, qName);
		} catch (MalformedURLException url_ex) {
			logger.error("{} failed to initialize WSDL:{}", CONNECTION.billingServiceName, CONNECTION.billingWSDL);
			throw new InitializationException(url_ex);
		}
	}

	/**
	 * Returns the singleton instance of the billing interface.
	 * 
	 * @return
	 */
	public static final BillingServiceInterfaceSoap getInstance() {
		return port;
	}

	/**
	 * Returns the URL of the service.
	 * 
	 * @return
	 */
	public static final URL getUrl() {
		return serviceInterface.getWSDLDocumentLocation();
	}

}