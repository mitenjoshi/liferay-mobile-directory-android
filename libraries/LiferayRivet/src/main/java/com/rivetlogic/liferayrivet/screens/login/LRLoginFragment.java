package com.rivetlogic.liferayrivet.screens.login;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.component.ShapeRoundRectStroke;
import com.rivetlogic.liferayrivet.component.StateListRect;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.PeopleDirectory;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.liferayrivet.util.ToastUtil;

/**
 * Created by lorenz on 1/13/15.
 */
public class LRLoginFragment extends Fragment {

    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRLoginFragment_styleResId";

    private LRLoginFragmentCallback listener;

    private int styleResId;
    private int layoutId;

    private int backgroundRes;

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
        public void onLoginSuccess();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (LRLoginFragmentCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        styleResId = R.style.LRThemeLoginViewDefault;
        setStyledAttributes();
        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_STYLE_ID)) {
            styleResId = args.getInt(KEY_STYLE_ID);
            if (styleResId > 0)
                setStyledAttributes();
        }
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
                SettingsUtil.setLogin(email.getText().toString());
                SettingsUtil.setPassword(password.getText().toString());

                PeopleDirectorySearchTask searchTask = new PeopleDirectorySearchTask(callback, "", 0, 0);
                searchTask.execute();
            }
        });

        RelativeLayout background = (RelativeLayout) v.findViewById(R.id.lr_login_background);
        background.setBackgroundResource(backgroundRes);

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

        email.setText(SettingsUtil.getLogin());
        password.setText(SettingsUtil.getPassword());

        return v;
    }

    private void setStyledAttributes() {
        TypedArray a = getActivity().getApplicationContext().obtainStyledAttributes(styleResId, R.styleable.LRLoginView);
        if(a != null) {
            try {
                layoutId = a.getResourceId(R.styleable.LRLoginView_lrTheme, layoutId);
                backgroundRes = a.getResourceId(R.styleable.LRLoginView_lrScreenLoginLayoutBackground, backgroundRes);

                buttonTextColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginButtonTextColor, buttonTextColor);
                String src = a.getString(R.styleable.LRLoginView_lrScreenLoginButtonText);
                if (src != null)
                    buttonText = src;
                buttonBackgroundColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginButtonBackgroundColor, buttonBackgroundColor);

                emailTextColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginEmailTextColor, emailTextColor);
                src = a.getString(R.styleable.LRLoginView_lrScreenLoginEmailTextHint);
                if (src != null)
                    emailTextHint = src;
                emailTextHintColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginEmailTextHintColor, emailTextHintColor);
                emailDrawable = a.getResourceId(R.styleable.LRLoginView_lrScreenLoginEmailDrawable, emailDrawable);

                passwordTextColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginPasswordTextColor, passwordTextColor);
                src = a.getString(R.styleable.LRLoginView_lrScreenLoginPasswordTextHint);
                if (src != null)
                    passwordTextHint = src;
                passwordTextHintColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginPasswordTextHintColor, passwordTextHintColor);
                passwordDrawable = a.getResourceId(R.styleable.LRLoginView_lrScreenLoginPasswordDrawable, passwordDrawable);

            } finally {
                a.recycle();
            }
        }
    }

    private PeopleDirectorySearchTask.PeopleDirectorySearchTaskCallback callback = new PeopleDirectorySearchTask.PeopleDirectorySearchTaskCallback() {
        @Override
        public void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), null, null);
            pd.setMessage(getString(R.string.msg_signing_in));
        }

        @Override
        public void onSuccess(PeopleDirectory dir) {
            listener.onLoginSuccess();
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }

        @Override
        public void onCancel(String error) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            ToastUtil.show(getActivity(), error, true);
        }
    };

}