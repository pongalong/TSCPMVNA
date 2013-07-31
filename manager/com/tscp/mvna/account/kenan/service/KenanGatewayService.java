package com.tscp.mvna.account.kenan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telscape.billingserviceinterface.ArrayOfMessageHolder;
import com.telscape.billingserviceinterface.ArrayOfValueHolder;
import com.telscape.billingserviceinterface.MessageHolder;
import com.telscape.billingserviceinterface.ValueHolder;
import com.tscp.mvna.account.kenan.exception.KenanException;

public class KenanGatewayService extends KenanGateway {
	protected static final Logger logger = LoggerFactory.getLogger(KenanGatewayService.class);

	/* **************************************************
	 * Server Response Validation Methods
	 */

	public static MessageHolder checkResponse(
			ArrayOfMessageHolder response) throws KenanException {
		if (response == null)
			throw new KenanException("No response received from server");
		if (response.getMessageHolder() == null || response.getMessageHolder().isEmpty())
			throw new KenanException("Null or empty response received from server");
		if (response.getMessageHolder().size() > 1)
			throw new KenanException("Multiple responses received from server");

		return checkResponse(response.getMessageHolder().get(0));
	}

	public static MessageHolder checkResponse(
			MessageHolder response) throws KenanException {
		if (response.getStatus() == null || response.getStatus().isEmpty())
			throw new KenanException("Null or empty status message received from server");
		if (!response.getStatus().equalsIgnoreCase("SUCCESS"))
			if (response.getMessage() != null)
				throw new KenanException("Error from server " + getUrl() + ": " + response.getMessage());
			else
				throw new KenanException("Error from server " + getUrl());
		return response;
	}

	public static ValueHolder checkResponse(
			ArrayOfValueHolder response) throws KenanException {

		if (response == null)
			throw new KenanException("No response received from server");
		if (response.getValueHolder() == null || response.getValueHolder().isEmpty())
			throw new KenanException("Null or empty response received from server");
		if (response.getValueHolder().size() > 1)
			throw new KenanException("Multiple responses received from server");

		return checkResponse(response.getValueHolder().get(0));
	}

	public static ValueHolder checkResponse(
			ValueHolder response) throws KenanException {
		if (response.getStatusMessage() == null)
			throw new KenanException("Null status message received from server");
		if (response.getStatusMessage().getStatus() == null || response.getStatusMessage().getStatus().isEmpty())
			throw new KenanException("Null or empty status message received from server");
		if (!response.getStatusMessage().getStatus().equalsIgnoreCase("SUCCESS"))
			if (response.getStatusMessage().getMessage() != null)
				throw new KenanException("Error from server " + getUrl() + response.getStatusMessage().getMessage());
			else
				throw new KenanException("Error from server " + getUrl());

		return response;
	}
}
