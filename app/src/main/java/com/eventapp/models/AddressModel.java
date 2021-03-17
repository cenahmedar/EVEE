package com.eventapp.models;

import java.io.Serializable;

public class AddressModel implements Serializable {

    private String Address;
    private String City;
    private double Latitude;
    private double Longitude;

    public AddressModel() {
    }

    public AddressModel(String address, String city, double latitude, double longitude) {
        Address = address;
        City = city;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
