package com.rivetlogic.liferayrivet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;

public class SettingsUtil {

    public static final String SERVER = "server";
    public static final String COMPANY_ID = "company_id";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    private static SharedPreferences _preferences;

    public static void init(Context context) {//10154
        if(_preferences == null)
            _preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Session getDefaultSession() {
        return new SessionImpl(getServer(), new BasicAuthentication("martin.zary@rivetlogic.com", "martin"));
    }

    public static Session getSession() {
        return new SessionImpl(getServer(), new BasicAuthentication(getLogin(), getPassword()));
    }

    public static int getCompanyId() {
        return _preferences.getInt(COMPANY_ID, 0);
    }

    public static String getServer() {
        return _preferences.getString(SERVER, "");
    }

    public static String getLogin() {
        return _preferences.getString(LOGIN, "");
    }

    public static String getPassword() {
        return _preferences.getString(PASSWORD, "");
    }

    public static void setServer(String url) {
        _preferences.edit().putString(SERVER, url).apply();
    }

    public static void setCompanyId(int companyId) {
        _preferences.edit().putInt(COMPANY_ID, companyId).apply();
    }

    public static void setLogin(String login) {
        _preferences.edit().putString(LOGIN, login).commit();
    }

    public static void setPassword(String password) {
        _preferences.edit().putString(PASSWORD, password).commit();
    }
}