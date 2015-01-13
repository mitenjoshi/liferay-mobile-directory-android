package com.rivetlogic.liferayrivet.screens.login;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.ui.component.ShapeRoundRectStroke;
import com.rivetlogic.liferayrivet.ui.component.StateListRect;

/**
 * Created by lorenz on 1/7/15.
 */
public class LoginView extends LinearLayout {

    private LoginRivetCallback listener;

    private String emailTextHint;
    private int emailTextColor;
    private int emailTextHintColor;
    private int emailDrawable;

    private String passwordTextHint;
    private int passwordTextColor;
    private int passwordTextHintColor;
    private int passwordDrawable;

    private String buttonText;
    private int buttonTextColor;
    private int buttonBackgroundColor;

    public LoginView(Context context) {
        super(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoginView, 0, 0);
        try {

            emailTextHint = a.getString(R.styleable.LoginView_lrEmailTextHint);
            emailTextColor = a.getInt(R.styleable.LoginView_lrEmailTextColor, 0);
            emailTextHintColor = a.getInt(R.styleable.LoginView_lrEmailTextHintColor, 0);
            emailDrawable = a.getInt(R.styleable.LoginView_lrEmailDrawable, 0);

            passwordTextHint = a.getString(R.styleable.LoginView_lrPasswordTextHint);
            passwordTextColor = a.getInt(R.styleable.LoginView_lrPasswordTextColor, 0);
            passwordTextHintColor = a.getInt(R.styleable.LoginView_lrPasswordTextHintColor, 0);
            passwordDrawable = a.getInt(R.styleable.LoginView_lrPasswordDrawable, 0);

            buttonText = a.getString(R.styleable.LoginView_lrButtonText);
            buttonTextColor = a.getInt(R.styleable.LoginView_lrButtonTextColor, 0);
            buttonBackgroundColor = a.getInt(R.styleable.LoginView_lrButtonBackgroundColor, 0);

        } finally {
            a.recycle();
        }
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface LoginRivetCallback {
        public void onError();

        public void onUpdate();

        public void onSuccess();
    }



    private void init() {
        inflate(getContext(), R.layout.login, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);
        TextView button = (TextView) findViewById(R.id.login_submit);

        if (emailTextHint != null)
            email.setHint(emailTextHint);
        if (emailTextColor > 0)
            email.setTextColor(emailTextColor);
        if (emailTextHintColor > 0)
            email.setHintTextColor(emailTextHintColor);
        if (emailDrawable > 0)
            email.setCompoundDrawablesWithIntrinsicBounds(emailDrawable, 0, 0, 0);
        if (passwordTextHint != null)
            password.setHint(passwordTextHint);
        if (passwordTextColor > 0)
            password.setTextColor(passwordTextColor);
        if (passwordTextHintColor > 0)
            password.setHintTextColor(passwordTextHintColor);
        if (passwordDrawable > 0)
            password.setCompoundDrawablesWithIntrinsicBounds(passwordDrawable, 0, 0, 0);
        if (buttonText != null)
            button.setText(buttonText);
        if (buttonTextColor != 0)
            button.setTextColor(buttonTextColor);
        if (buttonBackgroundColor != 0) {

            float radius = getResources().getDimension(R.dimen.corner_radius);
            float strokeWidth = getResources().getDimension(R.dimen.stroke_width);
            Drawable buttonDrawable = new StateListRect(buttonBackgroundColor, .08f, radius);
            button.setBackground(buttonDrawable);

            Drawable textDrawable = new ShapeRoundRectStroke(buttonBackgroundColor, 0f, radius, radius, radius, radius, 255, strokeWidth);
            email.setBackground(textDrawable);
            password.setBackground(textDrawable);

        }

    }

    public void setLoginListener(LoginRivetCallback listener) {
        this.listener = listener;
    }

}