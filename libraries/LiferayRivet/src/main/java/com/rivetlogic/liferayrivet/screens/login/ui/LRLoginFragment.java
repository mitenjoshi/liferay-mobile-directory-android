package com.rivetlogic.liferayrivet.screens.login.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.transport.PeopleDirectoryTask;
import com.rivetlogic.liferayrivet.ui.component.ShapeRoundRectStroke;
import com.rivetlogic.liferayrivet.ui.component.StateListRect;
import com.rivetlogic.liferayrivet.screens.directorylist.model.PeopleDirectory;

/**
 * Created by lorenz on 1/13/15.
 */
public class LRLoginFragment extends Fragment {

    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRLoginFragment_styleResId";

    private LRLoginFragmentCallback listener;

    private int styleResId;
    private int layoutId;

    private EditText email;
    private EditText password;
    private TextView submitButton;

    private String buttonText;
    private int buttonTextColor;
    private int buttonBackgroundColor;

    private int emailTextColor;
    private String emailTextHint;
    private int emailTextHintColor;
    private int emailDrawable;

    private int passwordTextColor;
    private String passwordTextHint;
    private int passwordTextHintColor;
    private int passwordDrawable;

    private ProgressDialog pd;

    public interface LRLoginFragmentCallback {
        public void onLoginSucess(PeopleDirectory directory);
    }

    public static LRLoginFragment newInstance() {
        return new LRLoginFragment();
    }

    public static LRLoginFragment newInstance(int styleResId) {
        LRLoginFragment fragment = new LRLoginFragment();
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

        this.listener = (LRLoginFragmentCallback) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_STYLE_ID)) {
            styleResId = args.getInt(KEY_STYLE_ID);
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

        View v = inflater.inflate(layoutId, null);

        submitButton = (TextView) v.findViewById(R.id.lr_login_submit);
        email = (EditText) v.findViewById(R.id.lr_login_email);
        password = (EditText) v.findViewById(R.id.lr_login_password);

        float radius = getResources().getDimension(R.dimen.corner_radius);
        float strokeWidth = getResources().getDimension(R.dimen.stroke_width);
        submitButton.setBackground(new StateListRect(buttonBackgroundColor, .08f, radius, 255));
        submitButton.setTextColor(buttonTextColor);
        submitButton.setText(buttonText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeopleDirectoryTask searchTask = new PeopleDirectoryTask(callback, "", -1, -1);
                searchTask.execute();
            }
        });

        Drawable textDrawable = new ShapeRoundRectStroke(buttonBackgroundColor, 0f, radius, radius, radius, radius, 255, strokeWidth);
        email.setBackground(textDrawable);
        password.setBackground(textDrawable);

        email.setHint(emailTextHint);
        email.setTextColor(emailTextColor);
        email.setHintTextColor(emailTextHintColor);
        email.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(emailDrawable), null, null, null);

        password.setHint(passwordTextHint);
        password.setTextColor(passwordTextColor);
        password.setHintTextColor(passwordTextHintColor);
        password.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(passwordDrawable), null, null, null);

        return v;
    }

    private void setStyledAttributes(TypedArray a) {
        try {
            layoutId = a.getResourceId(R.styleable.LRLoginView_lrTheme, layoutId);

            buttonTextColor = a.getColor(R.styleable.LRLoginView_lrButtonTextColor, buttonTextColor);
            String src = a.getString(R.styleable.LRLoginView_lrButtonText);
            if(src != null)
                buttonText = src;
            buttonBackgroundColor = a.getColor(R.styleable.LRLoginView_lrButtonBackgroundColor, buttonBackgroundColor);

            emailTextColor = a.getColor(R.styleable.LRLoginView_lrEmailTextColor, emailTextColor);
            src = a.getString(R.styleable.LRLoginView_lrEmailTextHint);
            if(src != null)
                emailTextHint = src;
            emailTextHintColor = a.getColor(R.styleable.LRLoginView_lrEmailTextHintColor, emailTextHintColor);
            emailDrawable = a.getResourceId(R.styleable.LRLoginView_lrEmailDrawable, emailDrawable);

            passwordTextColor = a.getColor(R.styleable.LRLoginView_lrPasswordTextColor, passwordTextColor);
            src = a.getString(R.styleable.LRLoginView_lrPasswordTextHint);
            if(src != null)
                passwordTextHint = src;
            passwordTextHintColor = a.getColor(R.styleable.LRLoginView_lrPasswordTextHintColor, passwordTextHintColor);
            passwordDrawable = a.getResourceId(R.styleable.LRLoginView_lrPasswordDrawable, passwordDrawable);

        } finally {

        }
    }

    private PeopleDirectoryTask.PeopleDirectoryTaskCallback callback = new PeopleDirectoryTask.PeopleDirectoryTaskCallback() {
        @Override
        public void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), null, null);
            pd.setMessage("Searching...");
        }

        @Override
        public void onSuccess(PeopleDirectory dir) {
            listener.onLoginSucess(dir);
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }

        @Override
        public void onCancel(String error) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    };

}