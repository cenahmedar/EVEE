package com.eventapp.models;

public class FirebaseModel<T> {
    String key;
    T Value;

    public FirebaseModel() {
    }

    public FirebaseModel(String key, T value) {
        this.key = key;
        Value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return Value;
    }

    public void setValue(T value) {
        Value = value;
    }
}
