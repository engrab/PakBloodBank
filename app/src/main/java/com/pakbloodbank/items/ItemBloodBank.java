package com.pakbloodbank.items;

public class ItemBloodBank {
    private String id, name, address, contact, lat, lon;

    public ItemBloodBank(String id, String name, String address, String contact, String lat, String lon) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

}
