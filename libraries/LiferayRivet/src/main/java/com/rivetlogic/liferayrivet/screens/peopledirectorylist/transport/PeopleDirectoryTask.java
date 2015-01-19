package com.rivetlogic.liferayrivet.screens.peopledirectorylist.transport;

import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.sdk.PeopleDirectoryService;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.model.PeopleDirectory;

import org.json.JSONObject;

/**
 * Created by lorenz on 1/13/15.
 */

public class PeopleDirectoryTask extends AsyncTask<Void, String, PeopleDirectory> {
    private PeopleDirectoryTaskCallback listener;
    private String keywords;
    private int start;
    private int end;
    private Exception e;

    public interface PeopleDirectoryTaskCallback {
        public void onPreExecute();
        public void onSuccess(PeopleDirectory dir);
        public void onCancel(String error);
    }

    public PeopleDirectoryTask(PeopleDirectoryTaskCallback listener, String keywords, int start, int end) {
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
        listener.onCancel(e.toString());
    }

    @Override
    public void onPostExecute(PeopleDirectory dir) {
        listener.onSuccess(dir);
    }

}