package com.example.pmsmobile.Models;

public class TC {
    private String id ;
    private String VoucherNo ;
    private String VoucherDate;
    private String Amount ;
    private String TenantAccountID;
    private String TenantName ;
    private String MobileNumber;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
    public String toString()
    {
        return TenantName;
    }
    public String getVoucherNo() {
        return VoucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        VoucherNo = voucherNo;
    }

    public String getVoucherDate() {
        return VoucherDate;
    }

    public void setVoucherDate(String voucherDate) {
        VoucherDate = voucherDate;
    }

    public String getTenantAccountID() {
        return TenantAccountID;
    }

    public void setTenantAccountID(String tenantAccountID) {
        TenantAccountID = tenantAccountID;
    }

    public String getTenantName() {
        return TenantName;
    }

    public void setTenantName(String tenantName) {
        TenantName = tenantName;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }
}
