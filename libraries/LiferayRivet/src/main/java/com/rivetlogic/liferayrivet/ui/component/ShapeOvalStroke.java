package com.rivetlogic.liferayrivet.ui.component;

import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class ShapeOvalStroke extends ShapeDrawable {

	public ShapeOvalStroke(int color, int width, float shift, int alpha) {
		super(new OvalShape());

		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);

		if (shift > 0) {
			hsv[1] -= shift;
			hsv[2] += shift;
		} else if (shift < 0) {
			hsv[1] -= shift;
			hsv[2] += shift;
		}

		color = Color.HSVToColor(hsv);
		this.getPaint().setColor(color);
		this.getPaint().setStyle(Style.STROKE);
		this.getPaint().setAntiAlias(true);
        this.getPaint().setAlpha(alpha);
		this.getPaint().setStrokeWidth(width);
	
	}

}