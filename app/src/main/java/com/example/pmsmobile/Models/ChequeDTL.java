package com.example.pmsmobile.Models;

public class ChequeDTL {

    private String id ;
    private String slno ;
    private String MonthYear ;
    private String PaymentTypeID ;
    private String InstrumentNo ;
    private String InstrumentDate;
    private String Amount ;
    private String TenantContractID ;
    private String Balance ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }

    public String getMonthYear() {
        return MonthYear;
    }

    public void setMonthYear(String monthYear) {
        MonthYear = monthYear;
    }

    public String getPaymentTypeID() {
        return PaymentTypeID;
    }

    public void setPaymentTypeID(String paymentTypeID) {
        PaymentTypeID = paymentTypeID;
    }

    public String getInstrumentNo() {
        return InstrumentNo;
    }

    public void setInstrumentNo(String instrumentNo) {
        InstrumentNo = instrumentNo;
    }

    public String getInstrumentDate() {
        return InstrumentDate;
    }

    public void setInstrumentDate(String instrumentDate) {
        InstrumentDate = instrumentDate;
    }

    public String getAmount() {
        return Amount;
    }
    public String toString()
    {
        return MonthYear;
    }
    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTenantContractID() {
        return TenantContractID;
    }

    public void setTenantContractID(String tenantContractID) {
        TenantContractID = tenantContractID;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }
}
