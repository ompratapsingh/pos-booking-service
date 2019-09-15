package com.pos.booking.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7787456635027855015L;

	private String id;

	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	private String branch;
	private String HWSerial;
	private String Salesman;

	public String getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getHWSerial() {
		return HWSerial;
	}

	public void setHWSerial(String hWSerial) {
		HWSerial = hWSerial;
	}

	public String getSalesman() {
		return Salesman;
	}

	public void setSalesman(String salesman) {
		Salesman = salesman;
	}

}
