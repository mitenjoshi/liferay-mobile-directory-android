package com.rivetlogic.liferayrivet.screens.login;

import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.PeopleDirectory;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.PeopleDirectoryService;
import com.rivetlogic.liferayrivet.util.SettingsUtil;

import org.json.JSONObject;

/**
 * Created by lorenz on 1/13/15.
 */

public class PeopleDirectorySearchTask extends AsyncTask<Void, String, PeopleDirectory> {
    private PeopleDirectorySearchTaskCallback listener;
    private String keywords;
    private int start;
    private int end;
    private Exception e;

    public interface PeopleDirectorySearchTaskCallback {
        public void onPreExecute();
        public void onSuccess(PeopleDirectory dir);
        public void onCancel(String error);
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
    protected PeopleDirectory doInBackground(Void... params) {
        Session session = SettingsUtil.getSession();
        PeopleDirectoryService ser = new PeopleDirectoryService(session);
        try {
            JSONObject json = ser.search(keywords, start, end);
            PeopleDirectory dir = new PeopleDirectory(json);
            return dir;
        } catch (Exception e) {
            this.e = e;
            cancel(true);
        }
        return null;
    }

    @Override
    public void onCancelled(PeopleDirectory dir) {
        listener.onCancel(e.getMessage());
    }

    @Override
    public void onPostExecute(PeopleDirectory dir) {
        listener.onSuccess(dir);
    }

}