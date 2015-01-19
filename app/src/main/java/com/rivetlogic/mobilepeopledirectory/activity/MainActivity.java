package com.rivetlogic.mobilepeopledirectory.activity;

/*here is your login/password for mobilepeoplefinder vm

        wadood [4:12 PM]
        tlorenz
        shuCh3Sh

        wadood [4:13 PM]
        i dont have your public key so you need to do that yourself or upload it to webdav for future logins*/


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.rivetlogic.liferayrivet.screens.peopledirectorydetail.LRDirectoryDetailFragment;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.data.User;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.ui.LRDirectoryListFragment;
import com.rivetlogic.liferayrivet.screens.login.LRLoginFragment;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.data.PeopleDirectory;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.R;


public class MainActivity extends Activity implements LRLoginFragment.LRLoginFragmentCallback, LRDirectoryListFragment.LRDirectoryListFragmentCallback {

    private static final String TAG_LOGIN_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRLoginFragment";
    private static final String TAG_LIST_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment";
    private static final String TAG_DETAIL_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRDirectoryDetailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SettingsUtil.init(this);

        addLoginFragment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addLoginFragment() {
        FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment prev = fm.findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (prev == null) {
            LRLoginFragment loginFragment = LRLoginFragment.newInstance(R.style.CustomLoginTheme);
            ft.replace(R.id.main_container, loginFragment, TAG_LOGIN_FRAGMENT);
         //   ft.addToBackStack(TAG_LOGIN_FRAGMENT);
            ft.commit();
        }
    }

    public void addDirectoryListFragment(PeopleDirectory directory) {
        FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment prev = fm.findFragmentByTag(TAG_LIST_FRAGMENT);
        if (prev == null) {
            LRDirectoryListFragment loginFragment = LRDirectoryListFragment.newInstance(R.style.CustomLoginTheme, directory);
            ft.replace(R.id.main_container, loginFragment, TAG_LIST_FRAGMENT);
            //  ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void addDirectoryDetailFragment(User user) {
        FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment prev = fm.findFragmentByTag(TAG_DETAIL_FRAGMENT);
        if (prev == null) {
            LRDirectoryDetailFragment detailFragment = LRDirectoryDetailFragment.newInstance(R.style.CustomLoginTheme, user);
            ft.replace(R.id.main_container, detailFragment, TAG_DETAIL_FRAGMENT);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onLoginSucess(PeopleDirectory directory) {
        addDirectoryListFragment(directory);
    }

    @Override
    public void onItemClicked(User user) {
        addDirectoryDetailFragment(user);
    }
}
