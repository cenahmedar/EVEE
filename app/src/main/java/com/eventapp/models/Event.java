package com.eventapp.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Event implements Serializable {

    private String Name;
    private int TypeID;
    private String TypeName;
    private String Date;
    private String Time;
    private String Phone;
    private boolean IsFree;
    private Double  Price;
    private String Description;
    private String Image;
    private AddressModel Address;
    private String UserKey;

    @Exclude
    private User User;


    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Event() {
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public boolean isFree() {
        return IsFree;
    }

    public void setFree(boolean free) {
        IsFree = free;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public AddressModel getAddress() {
        return Address;
    }

    public void setAddress(AddressModel address) {
        Address = address;
    }

    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }

    public com.eventapp.models.User getUser() {
        return User;
    }

    public void setUser(com.eventapp.models.User user) {
        User = user;
    }
}
