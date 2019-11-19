package com.pos.booking.domain;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartItems {
	
	@JsonProperty(value = "branch", required = true)
	private String branch;
	private String srl;
	private String prefix;
	private String docdate;
	private String doctime;
	private int noofPrints;
	private String store;
	private String tableCode;
	private String type;
	private String paxNo;
	private String captain;
	@JsonProperty(value = "Hwserial", required = true)
	private String hwserial;
	private String storeCode;
	private String enteredBy;
	private String totalDiscAmt;
	private String roundoff;
	private String partyEmail = "";
	private String partyContact = "";
	private String partyAddr = "";
	private String totalbillAmount;
	private String partyName = "";
	private String touchValue;
	private String billno;
	private String taxAmt;
	private String Addtaxamt;
	private String tableStatus;
	private boolean isBillGenerate;
	private String sepecialDiscount;
	private int disPrcnt;
	private LocalDate systemDate;
	
	
	public LocalDate getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(LocalDate systemDate) {
		this.systemDate = systemDate;
	}

	public int getDisPrcnt() {
		return disPrcnt;
	}

	public void setDisPrcnt(int disPrcnt) {
		this.disPrcnt = disPrcnt;
	}

	public String getSepecialDiscount() {
		return sepecialDiscount;
	}

	public void setSepecialDiscount(String sepecialDiscount) {
		this.sepecialDiscount = sepecialDiscount;
	}

	public boolean isBillGenerate() {
		return isBillGenerate;
	}

	public void setBillGenerate(boolean isBillGenerate) {
		this.isBillGenerate = isBillGenerate;
	}

	public String getTableStatus() {
		return tableStatus;
	}

	public void setTableStatus(String tableStatus) {
		this.tableStatus = tableStatus;
	}

	public String getTaxAmt() {
		return taxAmt;
	}

	public void setTaxAmt(String taxAmt) {
		this.taxAmt = taxAmt;
	}

	public String getAddtaxamt() {
		return Addtaxamt;
	}

	public void setAddtaxamt(String addtaxamt) {
		Addtaxamt = addtaxamt;
	}

	public String getBillno() {
		return billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	private List<Items> items;

	public List<Items> getItems() {
		return items;
	}

	public String getTouchValue() {
		return touchValue;
	}

	public void setTouchValue(String touchValue) {
		this.touchValue = touchValue;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}

	public String getTotalDiscAmt() {
		return totalDiscAmt;
	}

	public void setTotalDiscAmt(String totalDiscAmt) {
		this.totalDiscAmt = totalDiscAmt;
	}

	public String getRoundoff() {
		return roundoff;
	}

	public void setRoundoff(String roundoff) {
		this.roundoff = roundoff;
	}

	public String getPartyEmail() {
		return partyEmail;
	}

	public void setPartyEmail(String partyEmail) {
		this.partyEmail = partyEmail;
	}

	public String getPartyContact() {
		return partyContact;
	}

	public void setPartyContact(String partyContact) {
		this.partyContact = partyContact;
	}

	public String getPartyAddr() {
		return partyAddr;
	}

	public void setPartyAddr(String partyAddr) {
		this.partyAddr = partyAddr;
	}

	public String getTotalbillAmount() {
		return totalbillAmount;
	}

	public void setTotalbillAmount(String totalbillAmount) {
		this.totalbillAmount = totalbillAmount;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBranch() {
		return branch;
	}

	@JsonProperty(value = "branch", required = true)
	public void setbranch(String branch) {
		this.branch = branch;
	}

	public String getSrl() {
		return srl;
	}

	public void setSrl(String srl) {
		this.srl = srl;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDocdate() {
		return docdate;

	}
	public void setDocdate(String docdate) {
		this.docdate = docdate;
	}

	public String getDoctime() {
		return doctime;
	}

	public void setDoctime(String doctime) {
		this.doctime = doctime;
	}

	public int getNoofPrints() {
		return noofPrints;
	}

	public void setNoofPrints(int noofPrints) {
		this.noofPrints = noofPrints;
	}

	public String getPaxNo() {
		return paxNo;
	}

	public void setPaxNo(String paxNo) {
		this.paxNo = paxNo;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCaptain() {
		return captain;
	}

	public void setCaptain(String captain) {
		this.captain = captain;
	}

	public String getHwserial() {
		return hwserial;
	}

	public void setHwserial(String hwserial) {
		this.hwserial = hwserial;
	}

	@Override
	public String toString() {
		return "CartItems [branch=" + branch + ", srl=" + srl + ", prefix=" + prefix + ", docdate=" + docdate
				+ ", doctime=" + doctime + ", noofPrints=" + noofPrints + ", store=" + store + ", tableCode="
				+ tableCode + ", type=" + type + ", paxNo=" + paxNo + ", captain=" + captain + ", hwserial=" + hwserial
				+ ", storeCode=" + storeCode + ", enteredBy=" + enteredBy + ", totalDiscAmt=" + totalDiscAmt
				+ ", roundoff=" + roundoff + ", partyEmail=" + partyEmail + ", partyContact=" + partyContact
				+ ", partyAddr=" + partyAddr + ", totalbillAmount=" + totalbillAmount + ", partyName=" + partyName
				+ ", touchValue=" + touchValue + ", billno=" + billno + ", taxAmt=" + taxAmt + ", Addtaxamt="
				+ Addtaxamt + ", tableStatus=" + tableStatus + ", items=" + items + "]";
	}

}
