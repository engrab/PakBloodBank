package com.pakbloodbank.items;

import com.pakbloodbank.utils.Constant;

public class BloodBankFilterItem {
    private String latitude, longitude, radius, order_by, city, state, country;
    boolean isEdit;

    public BloodBankFilterItem getFilter() {
        this.latitude  = String.valueOf(Constant.curr_latitude);
        this.longitude = String.valueOf(Constant.curr_longitude);
        this.radius    = "none";
        this.order_by  = "none";
        this.city      = "";
        this.state     = "";
        this.country   = "";
        this.isEdit    = false;
        return this;
    }

    public BloodBankFilterItem getFilterWith(String latitude, String longitude, String radius, String order_by, String city, String state, String country) {
        this.latitude  = latitude != null ? latitude : "";
        this.longitude = longitude != null ? longitude : "";
        this.radius    = radius != null ? radius : "";
        this.order_by  = order_by != null ? order_by : "";
        this.city      = city != null ? city : "";
        this.state     = state != null ? state : "";
        this.country   = country != null ? country : "";
        this.isEdit    = false;
        return this;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
