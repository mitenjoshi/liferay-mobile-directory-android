package com.rivetlogic.liferayrivet.ui.component;

import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;

public class StateListRect extends StateListDrawable {

    public StateListRect(int primaryColor, float scaleSelected, float radius) {
        super();
        addState(new int[]{android.R.attr.state_pressed}, new ShapeRoundRectSolid(primaryColor, scaleSelected, radius, radius, radius, radius, 255));
        addState(StateSet.NOTHING, new ShapeRoundRectSolid(primaryColor, 0, radius, radius, radius, radius, 255));
    }

}