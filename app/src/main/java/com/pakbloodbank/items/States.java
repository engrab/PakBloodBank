package com.pakbloodbank.items;

import com.pakbloodbank.utils.interfaces.DataItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class States implements DataItem {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("country_id")
    @Expose
    private String country_id;

    public States(String id, String name, String country_id) {
        this.id = id;
        this.name = name;
        this.country_id = country_id;
    }


    public String getCountry_id() {
        return country_id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
