package com.rivetlogic.mobilepeopledirectory.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.TypedArray;
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
import android.widget.TextView;

import com.rivetlogic.liferayrivet.util.ToastUtil;
import com.rivetlogic.mobilepeopledirectory.R;
import com.rivetlogic.mobilepeopledirectory.adapter.PeopleDirectoryListAdapter;
import com.rivetlogic.mobilepeopledirectory.data.DataAccess;
import com.rivetlogic.mobilepeopledirectory.data.IDataAccess;
import com.rivetlogic.mobilepeopledirectory.model.User;
import com.rivetlogic.mobilepeopledirectory.model.Users;
import com.rivetlogic.mobilepeopledirectory.transport.PeopleDirectoryUpdateTask;

import java.util.ArrayList;

/**
 * Created by lorenz on 1/15/15.
 */
public class DirectoryFavoritesFragment extends Fragment {
    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment_styleResId";

    private int styleResId;
    private ListView lv;
    private PeopleDirectoryListAdapter adapter;
    private DirectoryListInterface listener;
    private IDataAccess da;
    private ProgressDialog pd;
    private ArrayList<User> users;
    private SwipeRefreshLayout swipeLayout;
    private int iconColor;
    private SearchView searchView;
    private Toolbar toolbar;

    public static DirectoryFavoritesFragment newInstance(int styleResId) {
        DirectoryFavoritesFragment fragment = new DirectoryFavoritesFragment();
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
    public void onResume() {
        super.onResume();
        if (users == null)
            updateUserList();
        else
            updateAdapter(users);
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
        toolbar.inflateMenu(R.menu.menu_favorites);
        TextView title = (TextView) v.findViewById(R.id.toolbar_title);
        title.setText(getString(R.string.action_favorites));

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
                }
                return false;
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        lv = (ListView) v.findViewById(R.id.fragment_directory_listview);
        adapter = new PeopleDirectoryListAdapter(getActivity());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                listener.onItemClicked(user);
                adapter.setSelected(position);
            }
        });
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_directory_listview_swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUserList();
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_red_dark));

        return v;
    }

    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            filterList(s);
            return true;
        }
    };

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

    public void filterList(String input) {
        adapter.getFilter().filter(input);
    }

    private void updateUserList() {
        ArrayList<User> users = da.getUsers();
        updateAdapter(users);
    }

    private void refreshUserList() {
        PeopleDirectoryUpdateTask updateTask = new PeopleDirectoryUpdateTask(new PeopleDirectoryUpdateTask.PeopleDirectoryUpdateTaskCallback() {
            @Override
            public void onPreExecute() {
                if (!swipeLayout.isRefreshing()) {
                    pd = ProgressDialog.show(getActivity(), null, null);
                    pd.setMessage(getString(R.string.msg_updating_people_directory));
                }
            }

            @Override
            public void onSuccess(Users users) {
                if (users != null && users.total > 0) {
                    da.updateUsers(users.list);
                }

                try {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();
                } catch (IllegalArgumentException e) {

                } catch (Exception e) {

                } finally {
                    pd = null;
                }

                if (swipeLayout != null && swipeLayout.isRefreshing())
                    swipeLayout.setRefreshing(false);
            }

            @Override
            public void onCancel(String error) {
                if (pd != null && pd.isShowing())
                    pd.dismiss();
                if (swipeLayout != null && swipeLayout.isRefreshing())
                    swipeLayout.setRefreshing(false);

                ToastUtil.show(getActivity(), error, true);
            }

        }, da.getModifiedDate());

        updateTask.execute();
    }

    private void updateAdapter(ArrayList<User> users) {
        ArrayList<User> favoriteUsers = new ArrayList<>();
        for(User user : users) {
            if(user.favorite)
                favoriteUsers.add(user);
        }
        adapter.updateAdapter(favoriteUsers);
    }

}