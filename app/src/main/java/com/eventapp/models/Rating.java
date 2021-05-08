package com.eventapp.models;

public class Rating {
    private String UserKey;
    private String EventKey;
    private String RateValue;
    private String Comment;

    public Rating(String userKey, String eventKey, String rateValue, String comment) {
        UserKey = userKey;
        EventKey = eventKey;
        RateValue = rateValue;
        Comment = comment;
    }

    public Rating() {
    }

    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }

    public String getRateValue() {
        return RateValue;
    }

    public void setRateValue(String rateValue) {
        RateValue = rateValue;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
