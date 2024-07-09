package com.pakbloodbank.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BloodBanksItem {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("contact")
    @Expose
    private String contact;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("hospital_name")
    @Expose
    private String hospitalName;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("location")
    @Expose
    private Object location;
    @SerializedName("views")
    @Expose
    private String views;

    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("time_ago")
    @Expose
    private String timeAgo;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getStatus() {
        return status;
    }

    public void setViews(String views) {
        this.views=views;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Object getLocation() {
        return location;
    }

    public String getViews() {
        return views;
    }

    public String getCreated() {
        return created;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDistance() {
        return distance;
    }

    public String getTimeAgo() {
        return timeAgo;
    }
}