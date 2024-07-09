package com.pakbloodbank.items;

import com.pakbloodbank.utils.interfaces.DataItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cities implements DataItem {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("state_id")
    @Expose
    private String state_id;

    public String getName() {
        return name;
    }

    public String getState_id() {
        return state_id;
    }

    public String getId() {
        return id;
    }

    public Cities(String id, String name, String state_id) {
        this.id = id;
        this.name = name;
        this.state_id = state_id;
    }
}
