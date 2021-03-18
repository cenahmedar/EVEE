package com.eventapp.models;

public class EventFilter {
    private int TypeID = -1;
    private String TypeName;
    private String PriceTo;
    private String CityName;

    public EventFilter() {
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

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getPriceTo() {
        return PriceTo;
    }

    public void setPriceTo(String priceTo) {
        PriceTo = priceTo;
    }
}
