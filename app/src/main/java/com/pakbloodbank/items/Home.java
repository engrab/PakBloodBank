package com.pakbloodbank.items;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Home {

    @SerializedName("RecentDonors")
    @Expose
    private List<Donor> recentDonors = null;
    @SerializedName("PopularDonors")
    @Expose
    private List<Donor> popularDonors = null;
    @SerializedName("Donor")
    @Expose
    private List<Donor> donorByUser = null;
    @SerializedName("Blogs")
    @Expose
    private List<Blog> blogs = null;

    public List<Donor> getRecentDonors() {
        return recentDonors;
    }

    public void setRecentDonors(List<Donor> recentDonors) {
        this.recentDonors = recentDonors;
    }

    public List<Donor> getPopularDonors() {
        return popularDonors;
    }

    public void setPopularDonors(List<Donor> popularDonors) {
        this.popularDonors = popularDonors;
    }

    public List<Donor> getDonorByUser() {
        return donorByUser;
    }

    public void setDonorByUser(List<Donor> donorByUser) {
        this.donorByUser = donorByUser;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }

}