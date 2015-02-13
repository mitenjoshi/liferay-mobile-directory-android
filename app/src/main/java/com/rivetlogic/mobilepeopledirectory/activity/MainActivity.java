package com.rivetlogic.mobilepeopledirectory.activity;

/*here is your login/password for mobilepeoplefinder vm

        wadood [4:12 PM]
        tlorenz
        shuCh3Sh

        wadood [4:13 PM]
        i dont have your public key so you need to do that yourself or upload it to webdav for future logins*/

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.rivetlogic.liferayrivet.screens.login.LRLoginFragment;
import com.rivetlogic.mobilepeopledirectory.fragment.LRDirectoryDetailFragment;
import com.rivetlogic.mobilepeopledirectory.fragment.PeopleDirectoryListFragment;
import com.rivetlogic.mobilepeopledirectory.model.User;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity implements LRLoginFragment.LRLoginFragmentCallback, PeopleDirectoryListFragment.LRDirectoryListFragmentCallback {

    private static final String TAG_LOGIN_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRLoginFragment";
    private static final String TAG_LIST_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment";
    private static final String TAG_DETAIL_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRDirectoryDetailFragment";

    private static final int TAG_QR_RESULT = 1001;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private Menu menu;
    private SearchView searchView;

    @Override
    protected void onPause() {
        if (mNfcAdapter != null)
            stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null)
            setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setTheme(R.style.MPDTheme);
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SettingsUtil.init(this);
        SettingsUtil.setServer("https://mobilepeoplefinder.vm2.rivetlogic.com");

        if (savedInstanceState == null) {
            if (SettingsUtil.getLogin() == null || SettingsUtil.getLogin().length() == 0 ||
                    SettingsUtil.getPassword() == null || SettingsUtil.getPassword().length() == 0) {
                addLoginFragment();
            } else {
                addDirectoryListFragment();
            }
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment prev = fm.findFragmentByTag(TAG_LOGIN_FRAGMENT);
            if (prev != null)
                getSupportActionBar().hide();

        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    public FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount == 0) {
                searchView.setVisibility(View.VISIBLE);
                menu.findItem(R.id.action_search).setVisible(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                menu.findItem(R.id.action_search).collapseActionView();
                searchView.setVisibility(View.GONE);
                menu.findItem(R.id.action_search).setVisible(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(searchListener);

        FragmentManager fm = getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag(TAG_DETAIL_FRAGMENT);
        if (prev != null) {
            searchView.setVisibility(View.GONE);
            menu.findItem(R.id.action_search).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                SettingsUtil.setPassword("");
                addLoginFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            FragmentManager fm = getSupportFragmentManager();
            PeopleDirectoryListFragment fragment = (PeopleDirectoryListFragment) fm.findFragmentByTag(TAG_LIST_FRAGMENT);
            if (fragment != null)
                fragment.filterList(s);
            return true;
        }
    };

    public void addLoginFragment() {
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment prev = fm.findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (prev == null) {
            getSupportActionBar().setShowHideAnimationEnabled(false);
            getSupportActionBar().hide();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            LRLoginFragment loginFragment = LRLoginFragment.newInstance(R.style.CustomLoginTheme);
            ft.replace(R.id.main_container, loginFragment, TAG_LOGIN_FRAGMENT);
            ft.commit();
        }

        ImageView qrButton = (ImageView) findViewById(R.id.main_container_qr_code_button);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    startActivityForResult(intent, TAG_QR_RESULT);
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
            }
        });
        qrButton.setVisibility(View.VISIBLE);
    }

    private LRLoginFragment getLoginFragment() {
        FragmentManager fm = getSupportFragmentManager();
        final LRLoginFragment frag = (LRLoginFragment) fm.findFragmentByTag(TAG_LOGIN_FRAGMENT);

        return frag;
    }

    public void addDirectoryListFragment() {
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment prev = fm.findFragmentByTag(TAG_LIST_FRAGMENT);
        if (prev == null) {
            getSupportActionBar().show();
            PeopleDirectoryListFragment listFragment = PeopleDirectoryListFragment.newInstance(R.style.CustomListTheme);
            if (getResources().getBoolean(R.bool.tablet_10)) {
                ft.replace(R.id.list_container, listFragment, TAG_DETAIL_FRAGMENT);
                final Fragment frag = fm.findFragmentByTag(TAG_LOGIN_FRAGMENT);
                if (frag != null) {
                    ft.remove(frag);
                }
                ft.commit();

            } else {
                ft.replace(R.id.main_container, listFragment, TAG_LIST_FRAGMENT);
                ft.commit();
            }
        }
    }

    public void addDirectoryDetailFragment(User user) {
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        LRDirectoryDetailFragment detailFragment = LRDirectoryDetailFragment.newInstance(R.style.CustomDetailTheme, user);
        if (getResources().getBoolean(R.bool.tablet_10)) {
            ft.replace(R.id.detail_container, detailFragment, TAG_DETAIL_FRAGMENT);
        } else {
            ft.setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit);
            ft.replace(R.id.main_container, detailFragment, TAG_DETAIL_FRAGMENT);
            ft.addToBackStack(TAG_DETAIL_FRAGMENT);
        }
        ft.commit();
    }

    @Override
    public void onLoginSuccess() {
        addDirectoryListFragment();
        ((ImageView) findViewById(R.id.main_container_qr_code_button)).setVisibility(View.GONE);
        if (mNfcAdapter != null) {
            stopForegroundDispatch(this, mNfcAdapter);
        }
    }

    @Override
    public void onLoginOption1Clicked() {

    }

    @Override
    public void onLoginOption2Clicked() {

    }


    @Override
    public void onItemClicked(User user) {
        addDirectoryDetailFragment(user);
    }



    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {

            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];
            Ndef ndef = Ndef.get(tag);
            if (ndef == null)
                return null;
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        String foo = "bar";
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                autoLoginNDEF(result);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAG_QR_RESULT) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                autoLoginQR(contents);
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

    private void autoLoginNDEF(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String password = json.getString("password");
            String email = json.getString("email");
            LRLoginFragment frag = getLoginFragment();
            if (frag != null) {
                frag.autoLogin(email, password);
            }
        } catch (JSONException e) {

        }
    }

    private void autoLoginQR(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String password = json.getString("password");
            String email = json.getString("email");
            LRLoginFragment frag = getLoginFragment();
            if (frag != null) {
                frag.autoLogin(email, password);
            }
        } catch (JSONException e) {

        }
    }

}