package com.tscp.mvne.device;

@Deprecated
public enum DeviceStatus {
	UNKNOWN(0, "Unknown"),
	NEW(1, "New"), // never used
	ACTIVE(2, "Active"), // active
	RELEASED(3, "Released / Reactivate-able"), // disconnected
	REMOVED(4, "Released / Removed"), // never used
	SUSPENDED(5, "Released / System-Reactivate"),
	BLOCKED(6, "Blocked"); // never used

	private int value;
	private String description;

	private DeviceStatus(int value, String description) {
		this.value = value;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getValue() {
		return value;
	}
}