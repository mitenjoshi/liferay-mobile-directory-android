package com.rivetlogic.liferayrivet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;

public class SettingsUtil {

	public static final String LOGIN = "login";
	public static final String PASSWORD = "password";
	public static final String SERVER = "server";

    private static SharedPreferences _preferences;

    public static void init(Context context) {
        if(_preferences == null)
            _preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Session getSession(String login, String password) {
        return new SessionImpl(getServer(), login, password);
    }

	public static Session getSession() {
		return new SessionImpl(getServer(), getLogin(), getPassword());
	}

	public static String getLogin() {
		return _preferences.getString(LOGIN, "");
	}
	
	public static String getPassword() {
		return _preferences.getString(PASSWORD, "");
	}
	
	public static String getServer() {
		return _preferences.getString(SERVER, "http://mobilepeoplefinder.vm2.rivetlogic.com");
	}

    public static void setLogin(String login) {
        _preferences.edit().putString(LOGIN, login).apply();
    }

    public static void setPassword(String password) {
        _preferences.edit().putString(PASSWORD, password).apply();
    }

    public static void setServer(String url) {
        _preferences.edit().putString(SERVER, url).apply();
    }

}