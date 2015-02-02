package com.rivetlogic.mobilepeopledirectory.activity;

/*here is your login/password for mobilepeoplefinder vm

        wadood [4:12 PM]
        tlorenz
        shuCh3Sh

        wadood [4:13 PM]
        i dont have your public key so you need to do that yourself or upload it to webdav for future logins*/

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.rivetlogic.liferayrivet.screens.peopledirectorydetail.LRDirectoryDetailFragment;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.PeopleDirectoryListFragment;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.User;
import com.rivetlogic.liferayrivet.screens.login.LRLoginFragment;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.R;

import java.lang.reflect.InvocationTargetException;


public class MainActivity extends ActionBarActivity implements LRLoginFragment.LRLoginFragmentCallback, PeopleDirectoryListFragment.LRDirectoryListFragmentCallback {

    private static final String TAG_LOGIN_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRLoginFragment";
    private static final String TAG_LIST_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment";
    private static final String TAG_DETAIL_FRAGMENT = "com.rivetlogic.liferay.screens.login.LRDirectoryDetailFragment";

    private Menu menu;
    private SearchView searchView;

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
    }

    @Override
    public void onItemClicked(User user) {
        addDirectoryDetailFragment(user);
    }

}