package com.rivetlogic.liferayrivet.screens.peopledirectorylist.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lorenz on 1/14/15.
 */
public class User implements Serializable {

    /*
    "portraitUrl": "\/image\/user_male_portrait?img_id=0&img_id_token=2jmj7l5rSw0yVb%2FvlWAYkK%2FYBwk%3D&t=1421271597118",
    "userId": 10624,
    "screenName": "dlc1",
    "emailAddress": "test.dlc.1@liferay.com",
    "userPhone": "",
    "birthDate": 0,
    "fullName": "Test DLC 1",
    "skypeName": "",
    "jobTitle": "",
    "city": ""
    */

    public static final String PORTRAIT_URL = "portraitUrl";
    public static final String USER_ID = "userId";
    public static final String SCREEN_NAME = "screenName";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String USER_PHONE = "userPhone";
    public static final String BIRTH_DATE = "birthDate";
    public static final String FULL_NAME = "fullName";
    public static final String SKYPE_NAME = "skypeName";
    public static final String JOB_TITLE = "jobTitle";
    public static final String CITY = "city";

    public String portraitUrl;
    public int userId;
    public String screenName;
    public String emailAddress;
    public String userPhone;
    public int birthDate;
    public String fullName;
    public String skypeName;
    public String jobTitle;
    public String city;

    public User(JSONObject json) throws JSONException {
        portraitUrl = json.getString(PORTRAIT_URL);
        userId = json.getInt(USER_ID);
        screenName = json.getString(SCREEN_NAME);
        emailAddress = json.getString(EMAIL_ADDRESS);
        userPhone = json.getString(USER_PHONE);
        birthDate = json.getInt(BIRTH_DATE);
        fullName = json.getString(FULL_NAME);
        skypeName = json.getString(SKYPE_NAME);
        jobTitle = json.getString(JOB_TITLE);
        city = json.getString(CITY);
    }

}