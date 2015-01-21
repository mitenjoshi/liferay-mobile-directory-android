package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.data.PeopleDirectory;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.data.User;

/**
 * Created by lorenz on 1/15/15.
 */
public class LRDirectoryListFragment extends Fragment {

    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment_styleResId";
    private static final String KEY_DIRECTORY = "com.rivetlogic.liferay.screens.login.LRDirectoryListFragment_directory";

    private int styleResId;
    private int layoutId;
    private PeopleDirectory directory;
    private ListView lv;
    private LRDirectoryListAdapter adapter;

    private LRDirectoryListFragmentCallback listener;

    public interface LRDirectoryListFragmentCallback {
        public void onItemClicked(User user);
    }

    public static LRDirectoryListFragment newInstance() {
        return new LRDirectoryListFragment();
    }

    public static LRDirectoryListFragment newInstance(int styleResId) {
        LRDirectoryListFragment fragment = new LRDirectoryListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_STYLE_ID, styleResId);
        fragment.setArguments(args);
        return fragment;
    }

    public static LRDirectoryListFragment newInstance(PeopleDirectory directory) {
        LRDirectoryListFragment fragment = new LRDirectoryListFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DIRECTORY, directory);
        fragment.setArguments(args);
        return fragment;
    }

    public static LRDirectoryListFragment newInstance(int styleResId, PeopleDirectory directory) {
        LRDirectoryListFragment fragment = new LRDirectoryListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_STYLE_ID, styleResId);
        args.putSerializable(KEY_DIRECTORY, directory);
        fragment.setArguments(args);
        return fragment;
    }

    public LRDirectoryListFragment() {

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
        if (args != null && args.containsKey(KEY_DIRECTORY)) {
            directory = (PeopleDirectory) args.getSerializable(KEY_DIRECTORY);
        }

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
        adapter = new LRDirectoryListAdapter(getActivity(), directory.users);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                listener.onItemClicked(user);
            }
        });

        return v;
    }

    private void setStyledAttributes(TypedArray a) {
        try {

        } finally {

        }
    }

}
