package com.rivetlogic.mobilepeopledirectory.transport;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by lorenz on 4/21/15.
 */
public class PeopleDirectoryDownloadTask extends AsyncTask<Void, String, Integer> {


    private PeopleDirectoryDownloadTaskCallback listener;

    private long modifiedDate;
    private int start;


    @Override
    protected Integer doInBackground(Void... params) {
        return null;
    }


    public interface PeopleDirectoryDownloadTaskCallback {
        void onPreExecute();
        void onSuccess(int count);
        void onCancel(String error);
    }

    public PeopleDirectoryDownloadTask(Context context, PeopleDirectoryDownloadTaskCallback listener, long modifiedDate, int start) {
        this.listener = listener;
        this.modifiedDate = modifiedDate;
        this.start = start;
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





}
