package com.example.pmsmobile.Models;

public class User {
    private  String id ;

    private String UserName;
    private String Password;
    private String PINNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPINNumber() {
        return PINNumber;
    }
    public String toString()
    {
        return UserName;
    }

    public void setPINNumber(String PINNumber) {
        this.PINNumber = PINNumber;
    }
}
