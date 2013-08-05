package com.tscp.mvna.account.device.usage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.tscp.jaxb.xml.adapter.DateTimeAdapter;

//TODO modify this class to use numbers rather than Strings for the returned values. This query is also highly inefficient and needs to be re-thought.
/**
 * This class maps to the return results of SP_FETCH_CHARGE_HISTORY
 * 
 * @author Jonathan
 * 
 */
@Entity
@XmlRootElement
public class UsageSession implements Serializable {
	private static final long serialVersionUID = -298371789365302489L;
	private DateTime dateTime;
	private String type;
	private Double usageAmount;
	private String rate;
	private Double dollarAmount;
	private Double discountAmount;
	private DateTime startTime;
	private DateTime endTime;
	private String notes;
	private Integer accountNo;
	private String externalId;
	private Double balance;

	@Id
	@Column(name = "DATE_AND_TIME")
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(
			DateTime dateTime) {
		this.dateTime = dateTime;
	}

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(
			String type) {
		this.type = type;
	}

	@Column(name = "USAGE")
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

	@Column(name = "AMOUNT")
	public Double getDollarAmount() {
		return dollarAmount;
	}

	public void setDollarAmount(
			Double dollarAmount) {
		this.dollarAmount = dollarAmount;
	}

	@Column(name = "DISCOUNT")
	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(
			Double discountAmount) {
		this.discountAmount = discountAmount;
	}

	@Column(name = "START_TIME")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(
			DateTime startTime) {
		this.startTime = startTime;
	}

	@Column(name = "END_TIME")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(
			DateTime endTime) {
		this.endTime = endTime;
	}

	@Column(name = "NOTES")
	public String getNotes() {
		return notes;
	}

	public void setNotes(
			String notes) {
		this.notes = notes;
	}

	@Column(name = "ACCOUNT_NO")
	public Integer getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(
			Integer accountNo) {
		this.accountNo = accountNo;
	}

	@Column(name = "EXTERNAL_ID")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(
			String externalId) {
		this.externalId = externalId;
	}

	@Column(name = "BALANCE")
	public Double getBalance() {
		return balance;
	}

	public void setBalance(
			Double balance) {
		this.balance = balance;
	}

}