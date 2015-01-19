package com.rivetlogic.liferayrivet.component;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

public class ShapeRoundRectSolid extends ShapeDrawable {

	public ShapeRoundRectSolid(int color, float shift, float topLeft, float topRight, float bottomRight, float bottomLeft, int alpha) {
		super(new RoundRectShape(new float[] { topLeft, topLeft, topRight, topRight, bottomRight, 
				bottomRight, bottomLeft, bottomLeft }, null, null));

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
		this.getPaint().setAlpha(alpha);
	}

}