package com.pakbloodbank.items;

import com.pakbloodbank.utils.Constant;

public class RequestFilterItem {
    private String blood_group, latitude, longitude, radius, order_by, city, state, country, added_by;
    boolean isEdit=false;

    public RequestFilterItem getFilter() {
        this.blood_group = "";
        this.latitude = String.valueOf(Constant.curr_latitude);
        this.longitude = String.valueOf(Constant.curr_longitude);
        this.radius = "none";
        this.order_by = "none";
        this.city = "";
        this.state = "";
        this.country = "";
        this.added_by = "";
        this.isEdit = false;
        return this;
    }

    public RequestFilterItem getFilterWith(String blood_group, String latitude, String longitude, String radius, String order_by, String city, String state, String country, String added_by) {
        this.blood_group = blood_group != null ? blood_group : "";
        this.latitude = latitude != null ? latitude : "";
        this.longitude = longitude != null ? longitude : "";
        this.radius = radius != null ? radius : "";
        this.order_by = order_by != null ? order_by : "";
        this.city = city != null ? city : "";
        this.state = state != null ? state : "";
        this.country = country != null ? country : "";
        this.added_by = added_by != null ? added_by : "";
        this.isEdit = false;
        return this;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRadius() {
        return radius;
    }

    public String getOrder_by() {
        return order_by;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }
}
