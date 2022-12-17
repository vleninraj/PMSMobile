package com.example.pmsmobile.Models;

public class PaymentType {
    private String id;
    private  String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return id;
    }
    public String toString()
    {
        return Name;
    }
    public void setId(String id) {
        this.id = id;
    }
}
