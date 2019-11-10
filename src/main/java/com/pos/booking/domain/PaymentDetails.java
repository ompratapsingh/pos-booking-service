package com.pos.booking.domain;

public class PaymentDetails {

	private String cashAmt="";
	private String cCBank="";
	private String cardAmt="";
	private String cardnumber="";
	private String chqAmt="";
	private String chqno="";
	private String chqBank="";
	private String debitAmt="";
	private String debtors="";
	private String status="";
	private String settleby="";
	private String settletime="";
	private String srl="";
	private String tableCode;
	private String refundAmt;
	
	public String getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
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

	public String getSettleby() {
		return settleby;
	}

	public void setSettleby(String settleby) {
		this.settleby = settleby;
	}

	public String getSettletime() {
		return settletime;
	}

	public void setSettletime(String settletime) {
		this.settletime = settletime;
	}

	public String getCashAmt() {
		return cashAmt;
	}

	public void setCashAmt(String cashAmt) {
		this.cashAmt = cashAmt;
	}

	public String getcCBank() {
		return cCBank;
	}

	public void setcCBank(String cCBank) {
		this.cCBank = cCBank;
	}

	public String getCardAmt() {
		return cardAmt;
	}

	public void setCardAmt(String cardAmt) {
		this.cardAmt = cardAmt;
	}

	public String getCardnumber() {
		return cardnumber;
	}

	public void setCardnumber(String cardnumber) {
		this.cardnumber = cardnumber;
	}

	public String getChqAmt() {
		return chqAmt;
	}

	public void setChqAmt(String chqAmt) {
		this.chqAmt = chqAmt;
	}

	public String getChqno() {
		return chqno;
	}

	public void setChqno(String chqno) {
		this.chqno = chqno;
	}

	public String getChqBank() {
		return chqBank;
	}

	public void setChqBank(String chqBank) {
		this.chqBank = chqBank;
	}

	public String getDebitAmt() {
		return debitAmt;
	}

	public void setDebitAmt(String debitAmt) {
		this.debitAmt = debitAmt;
	}

	public String getDebtors() {
		return debtors;
	}

	public void setDebtors(String debtors) {
		this.debtors = debtors;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
