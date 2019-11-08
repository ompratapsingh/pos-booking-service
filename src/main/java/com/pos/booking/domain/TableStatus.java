package com.pos.booking.domain;

public enum TableStatus {

	TABALE_STATUS_OCCUPIED("Y", "Occupied"), TABALE_STATUS_SETTLEMENT_PENDING("O",
			"Settlement pending"), TABLE_STATUS_AVAILABLE("A", "Available");

	private String statusCode;
	private String statusDescription;

	private TableStatus(String statusCode, String statusDescription) {
		this.statusCode = statusCode;
		this.statusDescription = statusDescription;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}
}
