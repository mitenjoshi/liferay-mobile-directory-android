package com.rivetlogic.mobilepeopledirectory.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rivetlogic.mobilepeopledirectory.R;

/**
 * Created by lorenz on 5/19/15.
 */
public class DetailRow extends RelativeLayout {

    private ImageView icon;
    private TextView title;
    private TextView data;

    public DetailRow(Context context) {
        super(context);
        init();
    }

    public DetailRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.detail_row, this);
        this.icon = (ImageView) findViewById(R.id.detail_row_icon);
        this.title = (TextView) findViewById(R.id.detail_row_title);
        this.data = (TextView) findViewById(R.id.detail_row_data);
    }

    public void setBackgroundRes(int color) {
        this.setBackgroundResource(color);
    }

    public void setIcon(int resId) {
        this.icon.setImageResource(resId);
    }

    public void setIconColor(int color) {
        this.icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void setTitle(int resId) {
        setTitle(getResources().getString(resId));
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setData(String data) {
        this.data.setText(data);
    }

}