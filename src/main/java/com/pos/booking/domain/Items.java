package com.pos.booking.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Items {

	private String code;
	@JsonProperty(value = "Qty", required = true)
	private int qty;
	private String rate;
	private String disc;
	private String discAmt;
	private String taxCode;
	private String remarks;
	private String taxamt;
	private String addtaxAmt;
	private String addtaxAmt2;
	private String item_name;
	
	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	@JsonProperty(value = "sno", required = true)
	private String sno;
	
	
	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getDisc() {
		return disc;
	}

	public void setDisc(String disc) {
		this.disc = disc;
	}

	public String getDiscAmt() {
		return discAmt;
	}

	public void setDiscAmt(String discAmt) {
		this.discAmt = discAmt;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getTaxamt() {
		return taxamt;
	}

	public void setTaxamt(String taxamt) {
		this.taxamt = taxamt;
	}

	public String getAddtaxAmt() {
		return addtaxAmt;
	}

	public void setAddtaxAmt(String addtaxAmt) {
		this.addtaxAmt = addtaxAmt;
	}

	public String getAddtaxAmt2() {
		return addtaxAmt2;
	}

	public void setAddtaxAmt2(String addtaxAmt2) {
		this.addtaxAmt2 = addtaxAmt2;
	}

	@Override
	public String toString() {
		return "Items [code=" + code + ", qty=" + qty + ", rate=" + rate + ", disc=" + disc + ", discAmt=" + discAmt
				+ ", taxCode=" + taxCode + ", remarks=" + remarks + ", taxamt=" + taxamt + ", addtaxAmt=" + addtaxAmt
				+ ", addtaxAmt2=" + addtaxAmt2 + ", item_name=" + item_name + ", sno=" + sno + "]";
	}
	
	
}
