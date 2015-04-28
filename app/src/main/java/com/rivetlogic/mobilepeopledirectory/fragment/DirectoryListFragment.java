package com.rivetlogic.mobilepeopledirectory.fragment;

import android.app.Activity;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rivetlogic.liferayrivet.util.ToastUtil;
import com.rivetlogic.mobilepeopledirectory.R;
import com.rivetlogic.mobilepeopledirectory.adapter.PeopleDirectoryCursorAdapter;
import com.rivetlogic.mobilepeopledirectory.data.DataAccess;
import com.rivetlogic.mobilepeopledirectory.data.IDataAccess;
import com.rivetlogic.mobilepeopledirectory.model.User;
import com.rivetlogic.mobilepeopledirectory.model.Users;
import com.rivetlogic.mobilepeopledirectory.transport.PeopleDirectoryUpdateTask;

/**
 * Created by lorenz on 1/15/15.
 */
public class DirectoryListFragment extends Fragment {
    private static final String KEY_STYLE_ID = "com.rivetlogic.mobilepeopledirectory.fragment.DirectoryListFragment_styleResId";
    private static final String KEY_IS_CHECKED = "com.rivetlogic.mobilepeopledirectory.fragment.DirectoryListFragment_isChecked";

    private int styleResId;
    private ListView lv;
    private PeopleDirectoryCursorAdapter cursorAdapter;
    private DirectoryListInterface listener;
    private IDataAccess da;
    private SwipeRefreshLayout swipeLayout;
    private int iconColor;
    private SearchView searchView;
    private Toolbar toolbar;

    public static DirectoryListFragment newInstance(int styleResId) {
        DirectoryListFragment fragment = new DirectoryListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_STYLE_ID, styleResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (DirectoryListInterface) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        da = DataAccess.getInstance(getActivity());
        Bundle args = getArguments();
        styleResId = R.style.LRThemeUserDetailDefault;
        setStyledAttributes();
        if (args != null && args.containsKey(KEY_STYLE_ID)) {
            styleResId = args.getInt(KEY_STYLE_ID);
            if (styleResId > 0)
                setStyledAttributes();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_directory_list, null);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_list);
        searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(searchListener);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_about:
                        listener.about();
                        break;
                    case R.id.action_logout:
                        listener.logout();
                        break;
                    case R.id.action_favorites:
                        setChecked();
                        updateAdapter(isChecked(), "");
                        break;
                }
                return false;
            }
        });

        lv = (ListView) v.findViewById(R.id.fragment_directory_listview);
        Cursor mCursor = da.getUsersCursor(false, "");
        cursorAdapter = new PeopleDirectoryCursorAdapter(getActivity(), mCursor, 0);
        lv.setAdapter(cursorAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeSearch();
                Cursor mCursor = (Cursor) cursorAdapter.getItem(position);
                User user = new User(mCursor);
                listener.onItemClicked(user);
            }
        });
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_directory_listview_swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                closeSearch();
                updateUserList();
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_red_dark));

        updateUserList();

        return v;
    }

    private void setStyledAttributes() {
        TypedArray a = getActivity().getApplicationContext().obtainStyledAttributes(styleResId, R.styleable.LRUserListView);
        if (a != null) {
            try {
                iconColor = a.getColor(R.styleable.LRUserListView_lrScreenUserListIconColor, iconColor);

            } finally {
                a.recycle();
            }
        }
    }

    private boolean isChecked() {
        MenuItem item = toolbar.getMenu().findItem(R.id.action_favorites);
        return item.isChecked();
    }

    private void setChecked() {
        closeSearch();
        MenuItem item = toolbar.getMenu().findItem(R.id.action_favorites);
        item.setChecked(!item.isChecked());
        item.setIcon(item.isChecked() ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_outline_white_24dp);
    }

    private void closeSearch() {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        searchItem.collapseActionView();
    }

    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            updateAdapter(isChecked(), s);
            return true;
        }
    };

    private void updateUserList() {
        PeopleDirectoryUpdateTask updateTask = new PeopleDirectoryUpdateTask(new PeopleDirectoryUpdateTask.PeopleDirectoryUpdateTaskCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onSuccess(Users users) {
                if (users != null && users.total > 0) {
                    da.updateUsers(users.list);
                    updateAdapter(isChecked(), "");
                }
            }

            @Override
            public void onCancel(String error) {
                ToastUtil.show(getActivity(), error, true);
            }

        }, da.getModifiedDate(), 0 , 1000);
        updateTask.execute();
    }

    public void updateAdapter(boolean favorites, String filter) {
        cursorAdapter.changeCursor(da.getUsersCursor(favorites, filter));
        if (swipeLayout != null && swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(false);
    }

}