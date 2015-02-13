package com.rivetlogic.liferayrivet.screens.forgotpassword;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.rivetlogic.liferayrivet.R;

/**
 * Created by lorenz on 2/3/15.
 */
public class LRPasswordFragment extends Fragment {
    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.forgotPassword.LRLoginFragment_styleResId";


    private LRPasswordFragmentCallback listener;

    private int styleResId;


    public interface LRPasswordFragmentCallback {
        public void onComplete();
    }

    public static LRPasswordFragment newInstance() {
        return new LRPasswordFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (LRPasswordFragmentCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

}