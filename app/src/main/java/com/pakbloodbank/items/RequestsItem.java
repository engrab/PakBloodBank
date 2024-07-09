package com.pakbloodbank.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestsItem {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("no_of_bags")
    @Expose
    private String noOfBags;

    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;

    @SerializedName("date_of_birth_simple")
    @Expose
    private String dateOfBirthSimple;

    @SerializedName("blood_group")
    @Expose
    private String bloodGroup;

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

    @SerializedName("message")
    @Expose
    private String message;

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
    @SerializedName("addedBy")
    @Expose
    private String addedBy;
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

    public String getFullName() {
        return fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getNoOfBags() {
        return noOfBags;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
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

    public String getCity() {
        return city;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
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
    public void setViews(String views) {
        this.views=views;
    }

    public String getAddedBy() {
        return addedBy;
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

    public String getDateOfBirthSimple() {
        return dateOfBirthSimple;
    }
}