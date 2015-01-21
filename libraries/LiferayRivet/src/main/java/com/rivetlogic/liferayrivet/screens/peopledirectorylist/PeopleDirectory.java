package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

import com.rivetlogic.liferayrivet.screens.peopledirectorylist.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lorenz on 1/14/15.
 */
public class PeopleDirectory implements Serializable {

    public static final String DIRECTORY_TOTAL = "total";
    public static final String DIRECTORY_USERS = "users";

    public String total;
    public ArrayList<User> users;

    public PeopleDirectory(JSONObject json) throws JSONException {
        total = json.getString(DIRECTORY_TOTAL);
        JSONArray array = json.getJSONArray(DIRECTORY_USERS);
        users = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            users.add(new User(array.getJSONObject(i)));
        }
    }

}