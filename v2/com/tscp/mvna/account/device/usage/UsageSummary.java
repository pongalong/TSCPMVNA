package com.tscp.mvna.account.device.usage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

//TODO modify this class to use numbers rather than Strings for the returned values.
/**
 * This class maps to the return results of GET_UNBILLED_DATA_MBS
 * 
 * @author Jonathan
 * 
 */
@Entity
public class UsageSummary implements Serializable {
	private static final long serialVersionUID = 4747048404362635062L;
	private String externalId;
	private Double usageAmount;
	private String rate;
	private Double dollarAmount;

	@Id
	@Column(name = "MDN")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(
			String externalId) {
		this.externalId = externalId;
	}

	@Column(name = "MBS")
	public Double getUsageAmount() {
		return usageAmount;
	}

	public void setUsageAmount(
			Double usageAmount) {
		this.usageAmount = usageAmount;
	}

	@Column(name = "RATE")
	public String getRate() {
		return rate;
	}

	public void setRate(
			String rate) {
		this.rate = rate;
	}

	@Column(name = "DOLLAR_USAGE")
	public Double getDollarAmount() {
		return dollarAmount;
	}

	public void setDollarAmount(
			Double dollarAmount) {
		this.dollarAmount = dollarAmount;
	}

}