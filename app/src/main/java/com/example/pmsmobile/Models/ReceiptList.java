package com.example.pmsmobile.Models;

public class ReceiptList {

    private String MobileVNo;
    private String TenantName;
    private String Amount;
    private String MonthYear;


    public String getTenantName() {
        return TenantName;
    }

    public void setTenantName(String tenantName) {
        TenantName = tenantName;
    }

    public String getMobileVNo() {
        return MobileVNo;
    }

    public void setMobileVNo(String mobileVNo) {
        MobileVNo = mobileVNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getMonthYear() {
        return MonthYear;
    }

    public void setMonthYear(String monthYear) {
        MonthYear = monthYear;
    }
}
