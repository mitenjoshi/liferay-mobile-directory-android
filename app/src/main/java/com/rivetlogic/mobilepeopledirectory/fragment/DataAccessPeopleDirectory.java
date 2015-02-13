package com.rivetlogic.mobilepeopledirectory.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.rivetlogic.mobilepeopledirectory.model.User;

import java.util.ArrayList;

/**
 * Created by lorenz on 1/21/15.
 */
public class DataAccessPeopleDirectory implements IDataAccessPeopleDirectory {
    private static IDataAccessPeopleDirectory instance = null;
    private SharedPreferences sharedPreferences;
    UserTable userTable;

    private DataAccessPeopleDirectory(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userTable = new UserTable(context);
    }

    public synchronized static IDataAccessPeopleDirectory getInstance(Context context) {
        if (instance == null) {
            instance = new DataAccessPeopleDirectory(context);
        }
        return instance;
    }

    @Override
    public void updateUsers(ArrayList<User> users) {
        userTable.updateUsers(users);
    }
    @Override
    public void updateUser(User user) {
        userTable.updateUser(user);
    }
    @Override
    public ArrayList<User> getUsers() {
        return userTable.getUsers();
    }
    @Override
    public long getModifiedDate() {
          return userTable.getModifiedDate();
    }

}