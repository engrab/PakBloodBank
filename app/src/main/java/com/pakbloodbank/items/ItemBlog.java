package com.pakbloodbank.items;


import com.pakbloodbank.utils.UrlHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemBlog {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String s_title;
    @SerializedName("blog_title")
    @Expose
    private String blog_title;
    @SerializedName("content")
    @Expose
    private String s_content;
    @SerializedName("blog_content")
    @Expose
    private String blog_content;
    @SerializedName("posted_at")
    @Expose
    private String posted_at;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("blog_image")
    @Expose
    private String blog_image;
//
//    public ItemBlog(String id, String s_title, String blog_title, String s_content, String blog_content, String posted_at, String created_at, String blog_image) {
//        this.id = id;
//        this.s_title = s_title;
//        this.blog_title = blog_title;
//        this.s_content = s_content;
//        this.blog_content = blog_content;
//        this.posted_at = posted_at;
//        this.blog_image = blog_image;
//        this.created_at = created_at;
//    }

    public String getS_title() {
        return s_title;
    }


    public String getS_content() {
        return s_content;
    }

    public String getId() {
        return id;
    }

    public String getBlog_title() {
        return blog_title;
    }

    public String getBlog_content() {
        return blog_content;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public String getBlog_image() {
        return UrlHelper.SERVER_URL + "/images/" + blog_image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getBlog_image_thumb() {
        return UrlHelper.SERVER_URL + "/images/thumb/" + blog_image;
    }
}
