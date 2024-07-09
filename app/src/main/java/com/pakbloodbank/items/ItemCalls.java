package com.pakbloodbank.items;

public class ItemCalls {
    String id;
    String to_id;
    String from_id;
    String toName;
    String toPhone;
    String date;
    String isRated;

    public ItemCalls(String id, String to_id, String from_id, String toName, String toPhone, String date, String isRated) {
        this.id = id;
        this.to_id = to_id;
        this.from_id = from_id;
        this.toName = toName;
        this.toPhone = toPhone;
        this.date = date;
        this.isRated = isRated;
    }

    public String getTo_id() {
        return to_id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public String getId() {
        return id;
    }

    public String getToName() {
        return toName;
    }

    public String getToPhone() {
        return toPhone;
    }

    public String getDate() {
        return date;
    }

    public String getIsRated() {
        return isRated;
    }

    public ItemCalls(String id, String toName, String toPhone, String date, String isRated) {
        this.id = id;
        this.toName = toName;
        this.toPhone = toPhone;
        this.date = date;
        this.isRated = isRated;
    }


}
