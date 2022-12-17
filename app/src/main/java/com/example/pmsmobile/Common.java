package com.example.pmsmobile;

import com.example.pmsmobile.Models.CashBank;
import com.example.pmsmobile.Models.ChequeDTL;
import com.example.pmsmobile.Models.Company;
import com.example.pmsmobile.Models.PaymentType;
import com.example.pmsmobile.Models.TC;
import com.example.pmsmobile.Models.User;

import java.util.ArrayList;

public class Common {

    public static String CompanyID ="PMS1";
    public static ArrayList<User> entUsers;
    public static User _currentUser;
    public static ArrayList<CashBank> entCashBank;
    public static ArrayList<TC> entTC;
    public  static ArrayList<PaymentType> entPaymentTypes;
    public  static  ArrayList<ChequeDTL> entChequeDTL;
    public  static String format = "%.2f";
    public static Company entCompany;

}
