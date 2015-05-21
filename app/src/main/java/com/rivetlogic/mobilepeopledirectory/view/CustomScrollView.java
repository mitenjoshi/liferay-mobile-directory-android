package com.rivetlogic.mobilepeopledirectory.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    private OnScrollChangedListener onScrollChangedListener;

    public interface OnScrollChangedListener {
        void onScrollChanged(int mScrollX, int mScrollY);
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int mScrollX, int mScrollY, int oldX, int oldY) {
        super.onScrollChanged(mScrollX, mScrollY, oldX, oldY);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(mScrollX, mScrollY);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

}