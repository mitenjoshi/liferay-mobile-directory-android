package com.rivetlogic.mobilepeopledirectory.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.model.User;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by lorenz on 1/21/15.
 */
public class DataAccess implements IDataAccess {
    private static IDataAccess instance = null;
    private SharedPreferences sharedPreferences;
    UserTable userTable;

    private DataAccess(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SettingsUtil.init(context);
        if(getCount() == 0) {
            String uuid = UUID.randomUUID().toString();
            setMasterPassword(uuid);
            setServer("http://mobilepeoplefinder.vm2.rivetlogic.com");
            setCompanyId(10154);
        }
        String masterPassword = getMasterPassword();
        userTable = new UserTable(context, masterPassword);
        incrementCount();
    }

    public synchronized static IDataAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DataAccess(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public int getCount() {
        return sharedPreferences.getInt(PREFS_COUNT, 0);
    }

    @Override
    public void incrementCount() {
        int count = getCount();
        sharedPreferences.edit().putInt(PREFS_COUNT, ++count).apply();
    }

    private String getMasterPassword() {
        return sharedPreferences.getString("master_password", "");
    }

    private void setMasterPassword(String password) {
        sharedPreferences.edit().putString("master_password", password).apply();
    }

    @Override
    public String getServer() {
        return SettingsUtil.getServer();
    }

    @Override
    public void setServer(String server) {
        SettingsUtil.setServer(server);
    }

    @Override
    public int getCompanyId() {
        return SettingsUtil.getCompanyId();
    }

    @Override
    public void setCompanyId(int companyId) {
        SettingsUtil.setCompanyId(companyId);
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
    public Cursor getUsersCursor(boolean favorites, String filter) {
        return userTable.getUsersCursor(favorites, filter);
    }

    @Override
    public int getUserCount() {
        return userTable.getCount();
    }

    @Override
    public long getModifiedDate() {
          return userTable.getModifiedDate();
    }

}