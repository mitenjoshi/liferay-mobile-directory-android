package com.rivetlogic.mobilepeopledirectory.transport;

import android.content.Context;
import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.data.DataAccess;
import com.rivetlogic.mobilepeopledirectory.model.Users;

import org.json.JSONObject;

/**
 * Created by lorenz on 1/13/15.
 */

public class PeopleDirectoryUpdateTask extends AsyncTask<Void, String, Users> {
    private PeopleDirectoryUpdateTaskCallback listener;
    private long modifiedDate;
    private int start;
    private int end;

    private Exception e;

    public interface PeopleDirectoryUpdateTaskCallback {
        void onPreExecute();
        void onSuccess(Users users);
        void onCancel(String error);
    }

    public PeopleDirectoryUpdateTask(PeopleDirectoryUpdateTaskCallback listener, long modifiedDate, int start, int end) {
        this.listener = listener;
        this.modifiedDate = modifiedDate;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Users doInBackground(Void... params) {
        Session session = SettingsUtil.getSession();
        PeopleDirectoryService ser = new PeopleDirectoryService(session);

        try {
            JSONObject json = ser.usersFetchByDate(modifiedDate, start, end);
            Users users = new Users(json);
            return users;

        } catch (Exception e) {
            this.e = e;
            cancel(true);
        }
        return null;
    }

    @Override
    public void onCancelled(Users users) {
        listener.onCancel(e.getMessage());
    }

    @Override
    public void onPostExecute(Users users) {
        listener.onSuccess(users);
    }

}