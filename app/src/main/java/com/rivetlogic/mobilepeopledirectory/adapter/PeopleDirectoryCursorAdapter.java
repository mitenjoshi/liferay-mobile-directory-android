package com.rivetlogic.mobilepeopledirectory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.R;
import com.rivetlogic.mobilepeopledirectory.data.UserTable;
import com.rivetlogic.mobilepeopledirectory.model.User;
import com.rivetlogic.mobilepeopledirectory.view.RippleView;
import com.rivetlogic.mobilepeopledirectory.view.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by lorenz on 4/21/15.
 */
public class PeopleDirectoryCursorAdapter extends CursorAdapter implements Filterable {
    private LayoutInflater cursorInflater;
    private boolean isTablet;
    private int primaryColor;
    private  User user;
    private Context context;
    private AdapterCallback mAdapterCallback;

    public interface AdapterCallback {
        void onMethodCallback(int position);
    }

    public void setOnClickListener(AdapterCallback listener) {
        mAdapterCallback = listener;
    }

    public PeopleDirectoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isTablet = context.getResources().getBoolean(R.bool.tablet_10);
        this.primaryColor = context.getResources().getColor(R.color.primary);
        user = new User();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_row, parent, false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_row,
                    null);
        }
        convertView.setTag(position);
        return super.getView(position, convertView, arg2);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor mCursor) {

        if ((Integer) view.getTag() % 2 == 0) {
            view.setBackgroundColor(context.getResources().getColor(R.color.black_05));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.black_10));
        }

        user.userId = mCursor.getInt(mCursor.getColumnIndex(UserTable.KEY_USER_ID));
        user.modifiedDate = mCursor.getLong(mCursor.getColumnIndex(UserTable.KEY_MODIFIED_DATE));
        user.portraitUrl = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_PORTRAIT_URL));
        user.screenName = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_SCREEN_NAME));
        user.emailAddress = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_EMAIL_ADDRESS));
        user.userPhone = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_USER_PHONE));
        user.birthDate = mCursor.getLong(mCursor.getColumnIndex(UserTable.KEY_BIRTH_DATE));
        user.fullName = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_FULL_NAME));
        user.skypeName = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_SKYPE_NAME));
        user.jobTitle = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_JOB_TITLE));
        user.city = mCursor.getString(mCursor.getColumnIndex(UserTable.KEY_CITY));
        user.favorite = mCursor.getInt(mCursor.getColumnIndex(UserTable.KEY_FAVORITE)) == 1;


        ImageView image = (ImageView) view.findViewById(R.id.list_row_directory_image);
        TextView name = (TextView) view.findViewById(R.id.list_row_directory_name);
        TextView screenName = (TextView) view.findViewById(R.id.list_row_directory_screen_name);
        ImageView skypeIcon = (ImageView) view.findViewById(R.id.list_row_directory_icon_skype);
        ImageView phoneIcon = (ImageView) view.findViewById(R.id.list_row_directory_icon_phone);
        ImageView emailIcon = (ImageView) view.findViewById(R.id.list_row_directory_icon_email);
        ImageView pointer = (ImageView) view.findViewById(R.id.list_row_directory_pointer);
        RippleView rippleView = (RippleView) view.findViewById(R.id.userListRippleView);

        Picasso.with(context).load(SettingsUtil.getServer() + user.portraitUrl)
                .placeholder(R.drawable.ic_list_image_default)
                .error(R.drawable.ic_list_image_default)
                .transform(new RoundedTransformation((int) (context.getResources().getDimension(R.dimen.listview_image_size) / 2), 2))
                .centerCrop()
                .resizeDimen(R.dimen.listview_image_size, R.dimen.listview_image_size)
                .into(image);

        name.setText(user.fullName);
        if (user.favorite) {
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_star_white_18dp, 0);
            Drawable[] drawables = name.getCompoundDrawables();
            if (drawables[2] != null)
                drawables[2].setColorFilter(primaryColor, PorterDuff.Mode.MULTIPLY);

        } else {
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        screenName.setText(user.screenName);
        phoneIcon.setVisibility(user.userPhone != null && user.userPhone.length() > 0 ? View.VISIBLE : View.GONE);
        skypeIcon.setVisibility(user.skypeName != null && user.skypeName.length() > 0 ? View.VISIBLE : View.GONE);
        emailIcon.setVisibility(user.emailAddress != null && user.emailAddress.length() > 0 ? View.VISIBLE : View.GONE);

        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mAdapterCallback.onMethodCallback((Integer) view.getTag());
                        } catch (ClassCastException exception) {
                            exception.printStackTrace();
                        }
                    }
                }, 500);
            }
        });
    }

}