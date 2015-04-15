package com.rivetlogic.liferayrivet.component;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;

public class StateListLoginOptionsBackground extends StateListDrawable {
    public StateListLoginOptionsBackground(ColorDrawable color) {
        super();
        color.setAlpha(58);
        addState(new int[]{android.R.attr.state_pressed}, color);
        addState(StateSet.NOTHING, null);
    }
}