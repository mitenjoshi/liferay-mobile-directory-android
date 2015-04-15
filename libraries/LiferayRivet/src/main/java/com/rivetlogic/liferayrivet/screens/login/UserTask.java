package com.rivetlogic.liferayrivet.screens.login;

import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v62.user.UserService;
import com.rivetlogic.liferayrivet.util.SettingsUtil;

import org.json.JSONObject;

/**
 * Created by lorenz on 1/13/15.
 */

public class UserTask extends AsyncTask<Void, String, JSONObject> {
    private UserTaskCallback listener;
    private Exception e;

    public interface UserTaskCallback {
        public void onPreExecute();

        public void onSuccess(JSONObject obj);

        public void onCancel(String error);
    }

    public UserTask(UserTaskCallback listener) {
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
    protected JSONObject doInBackground(Void... params) {
        Session session = SettingsUtil.getSession();
        UserService service = new UserService(session);
        try {
            JSONObject json = service.getUserByEmailAddress(SettingsUtil.getCompanyId(), SettingsUtil.getLogin());
            return json;
        } catch (Exception e) {
            this.e = e;
            cancel(true);
        }

        return null;
    }

    @Override
    public void onCancelled(JSONObject obj) {
        listener.onCancel(e.getMessage());
    }

    @Override
    public void onPostExecute(JSONObject obj) {
        listener.onSuccess(obj);
    }

}