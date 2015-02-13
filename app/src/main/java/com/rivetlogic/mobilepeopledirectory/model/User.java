package com.rivetlogic.mobilepeopledirectory.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lorenz on 1/14/15.
 */
public class User implements Serializable {

    /*
      "birthDate": 1420733424000,
      "city": "",
      "deleted": false,
      "emailAddress": "default@liferay.com",
      "fullName": "",
      "jobTitle": "",
      "male": true,
      "modifiedDate": 1420733424000,
      "portraitUrl": "/image/user_male_portrait?img_id=0&img_id_token=2jmj7l5rSw0yVb%2FvlWAYkK%2FYBwk%3D&t=1421859060998",
      "screenName": "10158",
      "skypeName": "",
      "userId": 10158,
      "userPhone": ""
    */

    public static final String USER_ID = "userId";
    public static final String MODIFIED_DATE = "modifiedDate";
    public static final String PORTRAIT_URL = "portraitUrl";
    public static final String SCREEN_NAME = "screenName";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String USER_PHONE = "userPhone";
    public static final String BIRTH_DATE = "birthDate";
    public static final String FULL_NAME = "fullName";
    public static final String SKYPE_NAME = "skypeName";
    public static final String JOB_TITLE = "jobTitle";
    public static final String CITY = "city";

    public int userId;
    public long modifiedDate;
    public String portraitUrl;
    public String screenName;
    public String emailAddress;
    public String userPhone;
    public long birthDate;
    public String fullName;
    public String skypeName;
    public String jobTitle;
    public String city;

    public User() {

    }

    public User(JSONObject json) throws JSONException {
        userId = json.getInt(USER_ID);
        modifiedDate = json.getLong(MODIFIED_DATE);
        portraitUrl = json.getString(PORTRAIT_URL);
        screenName = json.getString(SCREEN_NAME);
        emailAddress = json.getString(EMAIL_ADDRESS);
        userPhone = json.getString(USER_PHONE);
        birthDate = json.getLong(BIRTH_DATE);
        fullName = json.getString(FULL_NAME);
        skypeName = json.getString(SKYPE_NAME);
        jobTitle = json.getString(JOB_TITLE);
        city = json.getString(CITY);
    }

}