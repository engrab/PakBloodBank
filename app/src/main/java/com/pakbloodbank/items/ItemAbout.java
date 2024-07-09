package com.pakbloodbank.items;

public class ItemAbout {

    private final String app_name;
    private final String app_logo;
    private final String app_desc;
    private final String app_version;
    private final String author;
    private final String contact;
    private final String email;
    private final String website;
    private final String privacy;
    private final String developedby;

    public ItemAbout(String app_name, String app_logo, String app_desc, String app_version, String author, String contact, String email, String website, String privacy, String developedby) {
        this.app_name = app_name;
        this.app_logo = app_logo;
        this.app_desc = app_desc;
        this.app_version = app_version;
        this.author = author;
        this.contact = contact;
        this.email = email;
        this.website = website;
        this.privacy = privacy;
        this.developedby = developedby;
    }

    public String getAppName() {
        return app_name;
    }

    public String getAppLogo() {
        return app_logo;
    }

    public String getAppDesc() {
        return app_desc;
    }

    public String getAppVersion() {
        return app_version;
    }

    public String getAuthor() {
        return author;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getDevelopedby() {
        return developedby;
    }
}
