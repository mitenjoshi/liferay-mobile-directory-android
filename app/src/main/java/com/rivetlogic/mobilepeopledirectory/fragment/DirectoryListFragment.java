package com.rivetlogic.mobilepeopledirectory.fragment;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rivetlogic.liferayrivet.util.ToastUtil;
import com.rivetlogic.mobilepeopledirectory.R;
import com.rivetlogic.mobilepeopledirectory.adapter.PeopleDirectoryListAdapter;
import com.rivetlogic.mobilepeopledirectory.data.DataAccess;
import com.rivetlogic.mobilepeopledirectory.data.IDataAccess;
import com.rivetlogic.mobilepeopledirectory.transport.PeopleDirectoryUpdateTask;
import com.rivetlogic.mobilepeopledirectory.model.User;

import java.util.ArrayList;

/**
 * Created by lorenz on 1/15/15.
 */
public class DirectoryListFragment extends Fragment {
    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment_styleResId";

    private int styleResId;
    private ListView lv;
    private PeopleDirectoryListAdapter adapter;
    private LRDirectoryListFragmentCallback listener;
    private IDataAccess da;
    private ProgressDialog pd;
    private ArrayList<User> users;
    private SwipeRefreshLayout swipeLayout;
    private int iconColor;

    public interface LRDirectoryListFragmentCallback {
        public void onItemClicked(User user);
    }

    public static DirectoryListFragment newInstance() {
        return new DirectoryListFragment();
    }

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
        this.listener = (LRDirectoryListFragmentCallback) activity;
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

        lv = (ListView) v.findViewById(R.id.fragment_directory_listview);
        adapter = new PeopleDirectoryListAdapter(getActivity(), iconColor);
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
                updateUserList();
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_red_dark));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (users == null)
            updateUserList();
        else
            adapter.updateAdapter(users);
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

    public void filterList(String input) {
        adapter.getFilter().filter(input);
    }

    private void updateUserList() {
        PeopleDirectoryUpdateTask updateTask = new PeopleDirectoryUpdateTask(getActivity(), new PeopleDirectoryUpdateTask.PeopleDirectoryUpdateTaskCallback() {
            @Override
            public void onPreExecute() {
                if (!swipeLayout.isRefreshing()) {
                    pd = ProgressDialog.show(getActivity(), null, null);
                    pd.setMessage(getString(R.string.msg_updating_people_directory));
                }
            }

            @Override
            public void onSuccess(int count) {
                if (count > 0 || users == null) {
                    users = da.getUsers();
                    adapter.updateAdapter(users);
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

}