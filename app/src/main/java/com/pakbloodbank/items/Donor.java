package com.pakbloodbank.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Donor {

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
    @SerializedName("habits")
    @Expose
    private String habits;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("is_reminder")
    @Expose
    private String isReminder;
    @SerializedName("lastDonationDate")
    @Expose
    private String lastDonationDate;
    @SerializedName("lastDonationDateSimple")
    @Expose
    private String lastDonationDateSimple;
    @SerializedName("points")
    @Expose
    private String points;
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
    @SerializedName("kmDistance")
    @Expose
    private String kmDistance;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHabits() {
        return habits;
    }

    public void setHabits(String habits) {
        this.habits = habits;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsReminder() {
        return isReminder;
    }

    public void setIsReminder(String isReminder) {
        this.isReminder = isReminder;
    }

    public String getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(String lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getKmDistance() {
        return kmDistance;
    }

    public void setKmDistance(String kmDistance) {
        this.kmDistance = kmDistance;
    }

    public String getDateOfBirthSimple() {
        return dateOfBirthSimple;
    }

    public void setDateOfBirthSimple(String dateOfBirthSimple) {
        this.dateOfBirthSimple = dateOfBirthSimple;
    }

    public String getLastDonationDateSimple() {
        return lastDonationDateSimple;
    }

    public void setLastDonationDateSimple(String lastDonationDateSimple) {
        this.lastDonationDateSimple = lastDonationDateSimple;
    }





    //new code


    @Override
    public String toString() {
        return "Donor{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", dateOfBirthSimple='" + dateOfBirthSimple + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", habits='" + habits + '\'' +
                ", type='" + type + '\'' +
                ", isReminder='" + isReminder + '\'' +
                ", lastDonationDate='" + lastDonationDate + '\'' +
                ", lastDonationDateSimple='" + lastDonationDateSimple + '\'' +
                ", points='" + points + '\'' +
                ", status='" + status + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", location=" + location +
                ", views='" + views + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", created='" + created + '\'' +
                ", countryName='" + countryName + '\'' +
                ", stateName='" + stateName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", distance='" + distance + '\'' +
                ", timeAgo='" + timeAgo + '\'' +
                ", kmDistance='" + kmDistance + '\'' +
                '}';
    }
}