package com.rivetlogic.liferayrivet.screens.forgotpassword;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.component.ShapeRoundRectStroke;
import com.rivetlogic.liferayrivet.component.StateListLoginOptionsBackground;
import com.rivetlogic.liferayrivet.component.StateListRect;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.liferayrivet.util.ToastUtil;

/**
 * Created by lorenz on 2/3/15.
 */
public class LRPasswordFragment extends Fragment {
    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRPasswordFragment_styleResId";

    private LRPasswordFragmentCallback listener;
    private int styleResId;

    private int backgroundRes;
    private int topImageRes;
    private int bottomImageRes;

    private EditText email;
    private TextView submitButton;

    private TextView option1;
    private TextView option2;
    private View optionDiv;
    private String option1Text;
    private String option2Text;

    private String buttonText;
    private int buttonTextColor;
    private int buttonBackgroundColor;

    private int editTextColor;
    private int editTextHintColor;

    private String emailTextHint;
    private int emailDrawable;

    private int optionsTextColor;

    public interface LRPasswordFragmentCallback {

        public void onPasswordOption1Clicked();

        public void onPasswordOption2Clicked();
    }

    public static LRPasswordFragment newInstance() {
        return new LRPasswordFragment();
    }

    public static LRPasswordFragment newInstance(int styleResId) {
        LRPasswordFragment fragment = new LRPasswordFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_STYLE_ID, styleResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (LRPasswordFragmentCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        styleResId = R.style.LRThemePasswordViewDefault;
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
        View v = inflater.inflate(R.layout.lr_fragment_forgot_password, null);

        submitButton = (TextView) v.findViewById(R.id.lr_password_submit);
        email = (EditText) v.findViewById(R.id.lr_password_email);

        optionDiv = v.findViewById(R.id.lr_password_options_div);
        option1 = (TextView) v.findViewById(R.id.lr_password_option_1);
        option2 = (TextView) v.findViewById(R.id.lr_password_option_2);

        float radius = getResources().getDimension(R.dimen.corner_radius);
        float strokeWidth = getResources().getDimension(R.dimen.stroke_width);
        submitButton.setBackground(new StateListRect(buttonBackgroundColor, .08f, radius, 255));
        submitButton.setTextColor(buttonTextColor);
        submitButton.setText(buttonText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEmail();
            }
        });

        ImageView background = (ImageView) v.findViewById(R.id.lr_password_background);
        background.setImageResource(backgroundRes);

        ImageView topImage = (ImageView) v.findViewById(R.id.lr_password_image_top);
        topImage.setImageResource(topImageRes);

        ImageView bottomImage = (ImageView) v.findViewById(R.id.lr_password_image_bottom);
        bottomImage.setImageResource(bottomImageRes);

        Drawable textDrawable = new ShapeRoundRectStroke(buttonBackgroundColor, 0f, radius, radius, radius, radius, 255, strokeWidth);

        email.setBackground(textDrawable);
        email.setHint(emailTextHint);
        email.setTextColor(editTextColor);
        email.setHintTextColor(editTextHintColor);

        if (emailDrawable > 0)
            email.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(emailDrawable), null, null, null);

        optionDiv.setBackgroundColor(optionsTextColor);
        option1.setTextColor(optionsTextColor);
        option2.setTextColor(optionsTextColor);

        option1.setText(option1Text);
        option1.setBackground(new StateListLoginOptionsBackground(new ColorDrawable(optionsTextColor)));
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPasswordOption1Clicked();
            }
        });
        option2.setText(option2Text);
        option2.setBackground(new StateListLoginOptionsBackground(new ColorDrawable(optionsTextColor)));
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPasswordOption2Clicked();
            }
        });
        if (option1Text == null || option2Text == null)
            optionDiv.setVisibility(View.GONE);

        return v;
    }

    private void setStyledAttributes() {
        TypedArray a = getActivity().getApplicationContext().obtainStyledAttributes(styleResId, R.styleable.LRLoginView);
        if (a != null) {
            try {

                backgroundRes = a.getResourceId(R.styleable.LRLoginView_lrScreenLoginImageBackground, backgroundRes);
                topImageRes = a.getResourceId(R.styleable.LRLoginView_lrScreenLoginImageTop, topImageRes);
                bottomImageRes = a.getResourceId(R.styleable.LRLoginView_lrScreenLoginImageBottom, bottomImageRes);

                buttonTextColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginButtonTextColor, buttonTextColor);
                String src = a.getString(R.styleable.LRLoginView_lrScreenLoginButtonText);
                if (src != null)
                    buttonText = src;
                buttonBackgroundColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginButtonBackgroundColor, buttonBackgroundColor);

                editTextColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginEditTextColor, editTextColor);
                editTextHintColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginEditTextHintColor, editTextHintColor);

                src = a.getString(R.styleable.LRLoginView_lrScreenLoginForgotPasswordEmailTextHint);
                if (src != null)
                    emailTextHint = src;
                emailDrawable = a.getResourceId(R.styleable.LRLoginView_lrScreenLoginForgotPasswordEmailDrawable, 0);

                optionsTextColor = a.getColor(R.styleable.LRLoginView_lrScreenLoginOptionsTextColor, optionsTextColor);
                option1Text = a.getString(R.styleable.LRLoginView_lrScreenLoginOption1Text);
                option2Text = a.getString(R.styleable.LRLoginView_lrScreenLoginOption2Text);

            } finally {
                a.recycle();
            }
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void submitEmail() {
        final String str = email.getText().toString();
        if(!isEmailValid(email.getText())) {
            ToastUtil.show(getActivity(), getString(R.string.lr_email_error), false);
            return;
        }

        new SendPasswordByEmailAddressTask(getActivity(), SettingsUtil.getCompanyId(), str,
                new SendPasswordByEmailAddressTask.SendPasswordByEmailAddressTaskCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        getActivity().onBackPressed();
                        ToastUtil.show(getActivity(), String.format(getString(R.string.lr_password_sent), str), false);
                    }

                    @Override
                    public void onCancel(String error) {
                        if (error.startsWith("No User exists"))
                            ToastUtil.show(getActivity(), String.format(getString(R.string.lr_send_email_error), str), false);
                        else
                            ToastUtil.show(getActivity(), error, false);
                    }
                }).execute();

    }
}