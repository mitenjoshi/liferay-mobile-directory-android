package com.rivetlogic.mobilepeopledirectory.data;

import com.rivetlogic.mobilepeopledirectory.model.User;

import java.util.ArrayList;

/**
 * Created by lorenz on 1/21/15.
 */
public interface IDataAccess {

    String PREFS_COUNT = "prefs_count";

    int getCount();

    void incrementCount();

    String getServer();

    void setServer(String server);

    int getCompanyId();

    void setCompanyId(int companyId);

    public void updateUsers(ArrayList<User> users);

    public void updateUser(User user);

    public ArrayList<User> getUsers();

    long getModifiedDate();
}