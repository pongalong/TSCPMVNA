package com.tscp.mvna.account.device.network;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.tscp.mvna.account.device.network.exception.InvalidEsnException;

@XmlRootElement
public class NetworkEsn {
	// TODO move these values to a configurable database
	public static int ESN_DEC_LENGTH_MIN = 11;
	public static int ESN_DEC_LENGTH_MAX = 18;
	public static int ESN_HEX_LENGTH_MIN = 8;
	public static int ESN_HEX_LENGTH_MAX = 14;
	private String dec;
	private String hex;

	public NetworkEsn() {
		// do nothing
	}

	public NetworkEsn(String value) throws InvalidEsnException {
		if (isDec(value))
			dec = value;
		else if (isHex(value))
			hex = value;
		else
			throw new InvalidEsnException(value + " is neither DEC or HEX format");
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@XmlTransient
	public String getValue() {
		return dec != null ? dec : hex;
	}

	@XmlAttribute
	public String getDec() {
		return dec;
	}

	public void setDec(
			String dec) {
		this.dec = dec;
	}

	@XmlAttribute
	public String getHex() {
		return hex;
	}

	public void setHex(
			String hex) {
		this.hex = hex;
	}

	/* **************************************************
	 * Helper Methods
	 */

	public boolean isEmpty() {
		return dec == null && hex == null;
	}

	@XmlTransient
	public boolean equals(
			String value) {
		return (dec != null && dec.equals(value)) || (hex != null && hex.equals(value));
	}

	@XmlTransient
	protected boolean isAlphaNumeric(
			String value) {
		return !isNumeric(value) && !isAlpha(value);
	}

	@XmlTransient
	protected boolean isNumeric(
			String value) {
		return value.matches("\\d+");
	}

	@XmlTransient
	protected boolean isAlpha(
			String value) {
		return value.matches("\\D+");
	}

	@XmlTransient
	protected boolean isDecLength(
			String esn) {
		return esn.length() == ESN_DEC_LENGTH_MIN || esn.length() == ESN_DEC_LENGTH_MAX;
	}

	@XmlTransient
	protected boolean isHexLength(
			String esn) {
		return esn.length() == ESN_HEX_LENGTH_MIN || esn.length() == ESN_HEX_LENGTH_MAX;
	}

	@XmlTransient
	protected boolean isDec(
			String esn) {
		return esn != null && isNumeric(esn) && isDecLength(esn);
	}

	@XmlTransient
	protected boolean isHex(
			String esn) {
		return esn != null && isAlphaNumeric(esn) && isHexLength(esn);
	}

}