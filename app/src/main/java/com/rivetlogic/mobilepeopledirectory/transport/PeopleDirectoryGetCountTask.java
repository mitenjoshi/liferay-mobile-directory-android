package com.rivetlogic.mobilepeopledirectory.transport;

import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.data.DataAccess;
import com.rivetlogic.mobilepeopledirectory.model.Users;

import org.json.JSONObject;

/**
 * Created by lorenz on 1/13/15.
 */

public class PeopleDirectoryGetCountTask extends AsyncTask<Void, String, Integer> {
    private PeopleDirectoryGetCountTaskCallback listener;

    private Exception e;

    public interface PeopleDirectoryGetCountTaskCallback {
        void onPreExecute();
        void onSuccess(int count);
        void onCancel(String error);
    }

    public PeopleDirectoryGetCountTask(PeopleDirectoryGetCountTaskCallback listener) {
        this.listener = listener;
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
    protected Integer doInBackground(Void... params) {
        Session session = SettingsUtil.getSession();
        PeopleDirectoryService ser = new PeopleDirectoryService(session);
        try {
            return ser.getActiveUsersCount();
        } catch (Exception e) {
           this.e = e;
        }
        return 0;
    }

    @Override
    public void onCancelled(Integer count) {
        listener.onCancel(e.getMessage());
    }

    @Override
    public void onPostExecute(Integer count) {
        listener.onSuccess(count);
    }

}