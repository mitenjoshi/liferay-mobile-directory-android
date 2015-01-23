package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

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

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by lorenz on 1/15/15.
 */
public class PeopleDirectoryListFragment extends Fragment {

    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment_styleResId";

    private int styleResId;
    private int layoutId;
    private ListView lv;
    private PeopleDirectoryListAdapter adapter;

    private LRDirectoryListFragmentCallback listener;
    private IDataAccessPeopleDirectory da;
    private ProgressDialog pd;

    private ArrayList<User> users;
    private ArrayList<User> filteredUsers;
    private SwipeRefreshLayout swipeLayout;

    public interface LRDirectoryListFragmentCallback {
        public void onItemClicked(User user);
    }

    public static PeopleDirectoryListFragment newInstance() {
        return new PeopleDirectoryListFragment();
    }

    public static PeopleDirectoryListFragment newInstance(int styleResId) {
        PeopleDirectoryListFragment fragment = new PeopleDirectoryListFragment();
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

        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_STYLE_ID)) {
            styleResId = args.getInt(KEY_STYLE_ID);
        }

        da = DataAccessPeopleDirectory.getInstance(getActivity());

        @SuppressWarnings("ResourceType")
        TypedArray a = getActivity().getApplicationContext().obtainStyledAttributes(R.style.lrThemeLoginViewDefault, R.styleable.LRLoginView);
        setStyledAttributes(a);
        if (styleResId > 0) {
            a = getActivity().getApplicationContext().obtainStyledAttributes(styleResId, R.styleable.LRLoginView);
            setStyledAttributes(a);
        }
        a.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lr_fragment_directory_list, null);

        lv = (ListView) v.findViewById(R.id.lr_fragment_directory_listview);
        adapter = new PeopleDirectoryListAdapter(getActivity());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                listener.onItemClicked(user);
            }
        });

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.lr_fragment_directory_listview_swipe_container);
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

        if (users == null)
            updateUserList();
        else
            adapter.updateAdapter(users);

        return v;
    }


    private void setStyledAttributes(TypedArray a) {
        try {

        } finally {

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
                if (pd != null && pd.isShowing())
                    pd.dismiss();
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