package com.pakbloodbank.items;

import java.io.Serializable;

public class ItemBloodDonor implements Serializable {
    private String id, name, mobile, city, address, dob, bloodGroup, country, state, lastDonationDate, habits;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getDob() {
        return dob;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getLastDonationDate() {
        return lastDonationDate;
    }

    public String getHabits() {
        return habits;
    }

    public ItemBloodDonor(String id, String name, String mobile, String city, String address, String dob, String bloodGroup, String country, String state, String lastDonationDate, String habits) {

        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.city = city;
        this.address = address;
        this.dob = dob;
        this.bloodGroup = bloodGroup;
        this.country = country;
        this.state = state;
        this.lastDonationDate = lastDonationDate;
        this.habits = habits;
    }
}
