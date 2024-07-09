package com.pakbloodbank.utils;

public class UrlHelper {

    public static final String SERVER_URL = "https://uncovertarget.com/BloodBankAdminPanel/";
//    public static final String SERVER_URL = "http://bloodbankv3.almahirhub.com/";
//    public static final String SERVER_URL = "http://bloodbankv3.1.0.local.192.168.43.5.xip.io/";
    public static final String API        = SERVER_URL + "index.php/api/";
    public static final String imageUrl   = SERVER_URL + "images/";
    public static final String homeUrl             = API + "home";
    public static final String settingsUrl         = API + "index";
    public static final String requestsHomeUrl     = API + "requests_home";
    public static final String bloodBanksHomeUrl   = API + "blood_banks_home";
    public static final String donorsByFilter      = API + "donors_by_filter";
    public static final String requestsByFilter    = API + "requests_by_filter";
    public static final String bloodBanksByFilter  = API + "blood_banks_by_filter";
    public static final String getSomeData         = API + "get_some_data";
    public static final String countViewUrl        = API + "count_view";
    public static final String checkUserPhone      = API + "checkUserPhone"; // check if mobile phone is registered or not
    public static final String saveCallUrl         = API + "save_call"; // save call from & call to
    public static final String rateCallUrl         = API + "rate_call"; // rate a call...
    public static final String addUserUrl          = API + "addUser"; // add user
    public static final String reminderUrl         = API + "toggle_reminder"; // reminder setup
    public static final String callsUrl            = API + "getCalls"; // get calls of logged in user
    public static final String addDonorUrl         = API + "add_donor";  // add donor url
    public static final String addBloodRequestUrl  = API + "add_blood_request"; // add request
    public static final String donorsByUserId      = API + "donors_by_user_id"; // get donors by device id
    public static final String profileUrl          = API + "profile_data";
    public static final String editDonorUrl        = API + "edit_donor"; // edit donor
    public static final String updateRequest       = API + "update_request";
    public static final String editBloodRequestUrl = API + "edit_blood_request"; // edit request
    public static final String blogsUrl            = API + "blog_posts"; // blog


}
