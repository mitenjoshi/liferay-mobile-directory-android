package com.rivetlogic.liferayrivet.screens.forgotpassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.liferay.mobile.android.service.Session;
import com.rivetlogic.liferayrivet.util.SettingsUtil;

/**
 * Created by lorenz on 3/19/15.
 */
public class SendPasswordByEmailAddressTask extends AsyncTask<Void, String, Boolean> {

    private Context context;
    private SendPasswordByEmailAddressTaskCallback listener;
    private Exception e;
    private ProgressDialog pd;
    private long companyId;
    private String emailAddress;

    public interface SendPasswordByEmailAddressTaskCallback {
        public void onSuccess(Boolean result);
        public void onCancel(String error);
    }

    public SendPasswordByEmailAddressTask(Context context, long companyId, String emailAddress, SendPasswordByEmailAddressTaskCallback listener) {
        this.context = context;
        this.listener = listener;
        this.companyId = companyId;
        this.emailAddress = emailAddress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = ProgressDialog.show(context, null, null);
        pd.setMessage("sendPasswordByEmailAddress");
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        pd.setMessage(values[0]);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Session session = SettingsUtil.getDefaultSession();
       MobileWidgetsUserService mws = new MobileWidgetsUserService(session);
        try {
            Boolean obj = mws.sendPasswordByEmailAddress(companyId, emailAddress);
            publishProgress("hi");
            return obj;
        } catch (Exception e) {
            this.e = e;
            cancel(true);
        }
        return null;
    }

    @Override
    public void onCancelled(Boolean result) {
        if (pd != null)
            pd.dismiss();
        listener.onCancel(e.getMessage());
    }

    @Override
    public void onPostExecute(Boolean result) {
        if (pd != null)
            pd.dismiss();
        listener.onSuccess(result);
    }

}