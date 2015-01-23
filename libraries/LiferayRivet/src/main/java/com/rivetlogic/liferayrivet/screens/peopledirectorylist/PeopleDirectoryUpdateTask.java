package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

import android.content.Context;
import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.rivetlogic.liferayrivet.util.SettingsUtil;

import org.json.JSONObject;

/**
 * Created by lorenz on 1/13/15.
 */

public class PeopleDirectoryUpdateTask extends AsyncTask<Void, String, Integer> {
    private Context context;
    private PeopleDirectoryUpdateTaskCallback listener;
    private long modifiedDate;

    private Exception e;

    public interface PeopleDirectoryUpdateTaskCallback {
        public void onPreExecute();
        public void onSuccess(int count);
        public void onCancel(String error);
    }

    public PeopleDirectoryUpdateTask(Context context, PeopleDirectoryUpdateTaskCallback listener, long modifiedDate) {
        this.context = context;
        this.listener = listener;
        this.modifiedDate = modifiedDate;
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
            JSONObject json = ser.usersFetchByDate(modifiedDate);
            PeopleDirectory dir = new PeopleDirectory(json);

            if(dir != null && dir.users != null && dir.users.size() > 0) {
                DataAccessPeopleDirectory.getInstance(context).updateUsers(dir.users);
                return dir.users.size();
            }

        } catch (Exception e) {
            this.e = e;
            cancel(true);
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