package com.rivetlogic.mobilepeopledirectory.transport;

import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v62.user.UserService;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.model.Users;

import org.json.JSONObject;

/**
 * Created by lorenz on 1/13/15.
 */

public class PeopleDirectorySearchTask extends AsyncTask<Void, String, Users> {
    private PeopleDirectorySearchTaskCallback listener;
    private String keywords;
    private int start;
    private int end;
    private Exception e;

    public interface PeopleDirectorySearchTaskCallback {
        void onPreExecute();
        void onSuccess(Users dir);
        void onCancel(String error);
    }

    public PeopleDirectorySearchTask(PeopleDirectorySearchTaskCallback listener, String keywords, int start, int end) {
        this.listener = listener;
        this.keywords = keywords;
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


        UserService service = new UserService(session);

        try {
            JSONObject json = service.getUserByEmailAddress(1234, SettingsUtil.getLogin());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        PeopleDirectoryService ser = new PeopleDirectoryService(session);
        try {
            JSONObject json = ser.search(keywords, start, end);
            Users dir = new Users(json);
            return dir;
        } catch (Exception e) {
            this.e = e;
            cancel(true);
        }
        return null;
    }

    @Override
    public void onCancelled(Users dir) {
        listener.onCancel(e.getMessage());
    }

    @Override
    public void onPostExecute(Users dir) {
        listener.onSuccess(dir);
    }

}