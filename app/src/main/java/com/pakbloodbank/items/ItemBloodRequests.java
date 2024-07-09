package com.pakbloodbank.items;

import java.io.Serializable;

public class ItemBloodRequests implements Serializable {
    private final String id;
    private final String full_name;
    private final String mobile;
    private final String bloodGroup;
    private final String noOfBags;
    private final String city;
    private final String hospital;
    private final String message;
    private final String dob;

    public ItemBloodRequests(String id, String full_name, String mobile, String bloodGroup, String noOfBags, String city, String hospital, String message, String dob) {
        this.id = id;
        this.full_name = full_name;
        this.mobile = mobile;
        this.bloodGroup = bloodGroup;
        this.noOfBags = noOfBags;
        this.city = city;
        this.hospital = hospital;
        this.message = message;
        this.dob = dob;
    }

    public String getDob() {
        return dob;
    }

    public String getId() {
        return id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getNoOfBags() {
        return noOfBags;
    }

    public String getCity() {
        return city;
    }

    public String getHospital() {
        return hospital;
    }

    public String getMessage() {
        return message;
    }
}
