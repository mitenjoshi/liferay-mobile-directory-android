package com.rivetlogic.mobilepeopledirectory.model;

import com.rivetlogic.mobilepeopledirectory.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lorenz on 1/14/15.
 */
public class Users implements Serializable {

    public static final String DIRECTORY_TOTAL = "total";
    public static final String DIRECTORY_USERS = "users";

    public int total;
    public ArrayList<User> list;

    public Users(JSONObject json) throws JSONException {
        total = json.getInt(DIRECTORY_TOTAL);
        JSONArray array = json.getJSONArray(DIRECTORY_USERS);
        list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            list.add(new User(array.getJSONObject(i)));
        }
    }

}