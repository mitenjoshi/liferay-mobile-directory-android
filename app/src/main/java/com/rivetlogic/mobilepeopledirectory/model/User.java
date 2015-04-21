package com.rivetlogic.mobilepeopledirectory.model;

import android.database.Cursor;

import com.rivetlogic.mobilepeopledirectory.data.UserTable;

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
    public static final String DELETED = "deleted";
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
    public boolean deleted;
    public String portraitUrl;
    public String screenName;
    public String emailAddress;
    public String userPhone;
    public long birthDate;
    public String fullName;
    public String skypeName;
    public String jobTitle;
    public String city;
    public boolean favorite;

    public User() {

    }

    public User(JSONObject json) throws JSONException {
        this.userId = json.getInt(USER_ID);
        this.modifiedDate = json.getLong(MODIFIED_DATE);
        this.deleted = json.getBoolean(DELETED);
        this.portraitUrl = json.getString(PORTRAIT_URL);
        this.screenName = json.getString(SCREEN_NAME);
        this.emailAddress = json.getString(EMAIL_ADDRESS);
        this.userPhone = json.getString(USER_PHONE);
        this.birthDate = json.getLong(BIRTH_DATE);
        this.fullName = json.getString(FULL_NAME);
        this.skypeName = json.getString(SKYPE_NAME);
        this.jobTitle = json.getString(JOB_TITLE);
        this.city = json.getString(CITY);
        this.favorite = false;
    }

    public User(Cursor mCursor) {
        this.userId = mCursor.getInt(mCursor.getColumnIndex(UserTable.KEY_USER_ID));
        this.modifiedDate = mCursor.getLong(mCursor.getColumnIndex(UserTable.KEY_MODIFIED_DATE));
        this.portraitUrl = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_PORTRAIT_URL));
        this.screenName = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_SCREEN_NAME));
        this.emailAddress = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_EMAIL_ADDRESS));
        this.userPhone = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_USER_PHONE));
        this.birthDate = mCursor.getLong(mCursor.getColumnIndex(UserTable.KEY_BIRTH_DATE));
        this.fullName = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_FULL_NAME));
        this.skypeName = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_SKYPE_NAME));
        this.jobTitle = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_JOB_TITLE));
        this.city = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_CITY));
        this.favorite = mCursor.getInt(mCursor.getColumnIndex(UserTable.KEY_FAVORITE)) == 1;
    }

}