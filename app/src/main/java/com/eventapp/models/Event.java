package com.eventapp.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class Event implements Serializable {

    private String Name;
    private int TypeID;
    private String TypeName;
    private String Date;
    private String Time;
    private String DateTime;
    private String Phone;
    private boolean IsFree;
    private Double Price;
    private String Description;
    private String Image;
    private AddressModel Address;
    private String UserKey;
    private ArrayList<String> Participants;


    @Exclude
    private User User;

    public boolean isJoined(String userKey) {
        return Participants != null && Participants.contains(userKey);
    }

    public int getUsersCount() {
        return Participants == null ? 0 : Participants.size();
    }

    public ArrayList<String> getParticipants() {
        return Participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        Participants = participants;
    }

    public void addParticipant(String userKey) {
        Participants.add(userKey);
    }

    public void removeParticipant(String userKey) {
        Participants.remove(userKey);
    }

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

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }
}
