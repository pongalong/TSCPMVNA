package com.tscp.mvna.account.device.network;

public enum NetworkStatus {
	U("UNKNOWN"),
	R("RESERVED"),
	A("ACTIVE"),
	S("SUSPENDED"),
	C("DISCONNECTED"),
	P("SWAPPED");

	private String description;

	private NetworkStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public boolean isConnectable() {
		return this == R || this == C || this == P;
	}

	public boolean isAlive() {
		return this == R || this == A || this == S;
	}

}