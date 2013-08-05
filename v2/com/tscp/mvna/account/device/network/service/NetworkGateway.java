package com.tscp.mvna.account.device.network.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.ws.client.BindingProviderProperties;
import com.tscp.mvna.ws.InitializationException;
import com.tscp.mvne.config.CONFIG;
import com.tscp.mvne.config.CONNECTION;
import com.tscp.mvno.webservices.API3;
import com.tscp.mvno.webservices.API3Service;

public abstract class NetworkGateway {
	protected static final Logger logger = LoggerFactory.getLogger(NetworkGateway.class);
	protected static final API3Service service = loadService();
	protected static final API3 port = service.getAPI3Port();
	protected static int gatewaySessionCount;

	/**
	 * Loads and returns the API3 Service.
	 * 
	 * @return
	 */
	protected static final API3Service loadService() throws InitializationException {
		CONFIG.initAll();
		try {
			API3Service service = new API3Service(new URL(CONNECTION.networkWSDL), new QName(CONNECTION.networkNameSpace, CONNECTION.networkServiceName));
			logger.info("NetworkGateway initialized to {}", service.getWSDLDocumentLocation());
			return service;
		} catch (MalformedURLException url_ex) {
			logger.error("Exception initializing NetworkGateway at " + CONNECTION.networkWSDL, url_ex);
			throw new InitializationException(url_ex);
		}
	}

	/**
	 * Returns the singleton instance of the API3 Service.
	 * 
	 * @return
	 */
	public static final API3 getInstance() {
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