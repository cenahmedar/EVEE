package com.eventapp.models;

public class BasicModel {

    private int ID;
    private String Name;

    public BasicModel(int ID, String name) {
        this.ID = ID;
        Name = name;
    }

    public BasicModel() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }
}
