package com.rivetlogic.mobilepeopledirectory.utilities;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lorenz on 4/15/15.
 */
public class Utilities {

    public static void showTost(Context context, String message) {
        showTost(context, message, Toast.LENGTH_SHORT);
    }

    public static void showTost(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    public static void showTost(Context context, String message, int length, int gravity) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

}