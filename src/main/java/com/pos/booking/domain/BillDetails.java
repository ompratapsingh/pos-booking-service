package com.pos.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BillDetails {

	private String srl;

	private String tableCode;

	private String specialDiscount;
	
	private String enteredBy;
	
	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}

	public String getSpecialDiscount() {
		return specialDiscount;
	}
	
	public void setSpecialDiscount(String specialDiscount) {
		this.specialDiscount = specialDiscount;
	}

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public String getSrl() {
		return srl;
	}

	public void setSrl(String srl) {
		this.srl = srl;
	}

}
