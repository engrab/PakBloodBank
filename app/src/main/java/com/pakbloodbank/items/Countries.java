package com.pakbloodbank.items;

import com.pakbloodbank.utils.interfaces.DataItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Countries implements DataItem {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("short_name")
    @Expose
    private String short_name;

    @SerializedName("phone_code")
    @Expose
    private String phone_code;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public Countries(String id, String name, String short_name, String phone_code) {
        this.id = id;
        this.name = name;
        this.short_name = short_name;
        this.phone_code = phone_code;
    }
}
