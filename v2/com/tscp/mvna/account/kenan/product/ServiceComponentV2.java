package com.tscp.mvna.account.kenan.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.kenan.provision.ServiceComponent;
import com.tscp.mvna.account.kenan.provision.ServiceInstance;

public class ServiceComponentV2 extends ServiceComponent {
	private static final long serialVersionUID = -6764777879445869086L;
	protected static final Logger logger = LoggerFactory.getLogger(ServiceComponentV2.class);
	protected ServiceInstance serviceInstance;

	/* **************************************************
	 * Constructors
	 */

	public ServiceComponentV2(ServiceComponent serviceComponent, ServiceInstance serviceInstance) {
		super(serviceComponent);
		this.serviceInstance = serviceInstance;
	}

	/* **************************************************
	 * Getter and Setter Methods
	 */

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(
			ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

}