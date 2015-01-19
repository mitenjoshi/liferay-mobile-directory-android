package com.rivetlogic.liferayrivet.component;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

public class ShapeRoundRectStroke extends ShapeDrawable {
    private float strokeWidth;

    public ShapeRoundRectStroke(int color, float shift, float topLeft, float topRight, float bottomRight,
                                float bottomLeft, int alpha, float strokeWidth) {

        super(new RoundRectShape(new float[]{topLeft, topLeft, topRight, topRight, bottomRight,
                bottomRight, bottomLeft, bottomLeft}, null, null));

        this.strokeWidth = strokeWidth;
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
        this.getPaint().setStyle(Paint.Style.STROKE);
        this.getPaint().setAntiAlias(true);
        this.getPaint().setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        shape.resize(canvas.getClipBounds().right, canvas.getClipBounds().bottom);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
                new RectF(strokeWidth / 2, strokeWidth / 2, canvas.getClipBounds().right - strokeWidth / 2,
                        canvas.getClipBounds().bottom - strokeWidth / 2), Matrix.ScaleToFit.FILL);
        canvas.concat(matrix);
        shape.draw(canvas, getPaint());
    }

}