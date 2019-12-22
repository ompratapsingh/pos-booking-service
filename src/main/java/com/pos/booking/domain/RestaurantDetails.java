package com.pos.booking.domain;

public class RestaurantDetails {

	private String rName;
	private String rAddress;
	private String rTelephone;
	private String rGSTNo;

	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName;
	}

	public String getrAddress() {
		return rAddress;
	}

	public void setrAddress(String rAddress) {
		this.rAddress = rAddress;
	}

	public String getrTelephone() {
		return rTelephone;
	}

	public void setrTelephone(String rTelephone) {
		this.rTelephone = rTelephone;
	}

	public String getrGSTNo() {
		return rGSTNo;
	}

	public void setrGSTNo(String rGSTNo) {
		this.rGSTNo = rGSTNo;
	}

}
