package com.example.pmsmobile.Models;

public class Property {
    private  String id;
    private String PropertyName;

    public String getPropertyName() {
        return PropertyName;
    }

    public void setPropertyName(String propertyName) {
        PropertyName = propertyName;
    }
    public String toString()
    {
        return PropertyName;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
