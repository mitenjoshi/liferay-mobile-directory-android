package com.rivetlogic.mobilepeopledirectory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.rivetlogic.mobilepeopledirectory.data.Database;
import com.rivetlogic.mobilepeopledirectory.data.TableRow;
import com.rivetlogic.mobilepeopledirectory.model.User;

import java.util.ArrayList;

/**
 * Copyright (c) 2014 HMR Weight Management Services Corp.. All rights reserved.  10/13/14.
 */
public class UserTable extends Database {
    private static final String TABLE_NAME = "people_directory_user_table";
    private static final int TABLE_VERSION = 1;

    public static final String KEY_USER_ID = "col_userId";
    public static final String KEY_MODIFIED_DATE = "key_modifiedDate";
    public static final String KEY_PORTRAIT_URL = "col_portraitUrl";
    public static final String KEY_SCREEN_NAME = "col_screenName";
    public static final String KEY_EMAIL_ADDRESS = "col_emailAddress";
    public static final String KEY_USER_PHONE = "col_userPhone";
    public static final String KEY_BIRTH_DATE = "col_birthDate";
    public static final String KEY_FULL_NAME = "col_fullName";
    public static final String KEY_SKYPE_NAME = "col_skypeName";
    public static final String KEY_JOB_TITLE = "col_jobTitle";
    public static final String KEY_CITY = "col_city";
    public static final String KEY_FAVORITE = "col_fav";

    private TableRow[] tableDef = {
            new TableRow(1, KEY_USER_ID, TableRow.DbType.INTEGER_PRIMARY_KEY, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_MODIFIED_DATE, TableRow.DbType.INT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_PORTRAIT_URL, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_SCREEN_NAME, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_EMAIL_ADDRESS, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_USER_PHONE, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_BIRTH_DATE, TableRow.DbType.INT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_FULL_NAME, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_SKYPE_NAME, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_JOB_TITLE, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_CITY, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_FAVORITE, TableRow.DbType.INT, TableRow.Nullable.FALSE)
    };

    public UserTable(Context context, String masterPassword) {
        super();
        open(context, masterPassword);
    }

    public void open(Context context, String masterPassword) throws SQLException {
        super.open(context, masterPassword, TABLE_NAME, TABLE_VERSION, tableDef);
    }

    public long getModifiedDate() {
        String sql = String.format("SELECT MAX(%s) AS %s from %s", KEY_MODIFIED_DATE, KEY_MODIFIED_DATE, TABLE_NAME);
        Cursor mCursor = mDatabase.rawQuery(sql, null);
        if (mCursor != null && mCursor.moveToFirst()) {
            long max = mCursor.getLong(mCursor.getColumnIndex(KEY_MODIFIED_DATE));
            mCursor.close();
            return max;
        }
        return 0;
    }

    public void updateUsers(ArrayList<User> users) {
        try {
            mDatabase.beginTransaction();
            for (User user : users) {
                updateUser(user);
            }
            mDatabase.setTransactionSuccessful();
        } catch (SQLException s) {

        } finally {
            mDatabase.endTransaction();
        }
    }

    public void updateUser(User user) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, user.userId);
            values.put(KEY_MODIFIED_DATE, user.modifiedDate);
            values.put(KEY_PORTRAIT_URL, user.portraitUrl);
            values.put(KEY_SCREEN_NAME, user.screenName);
            values.put(KEY_EMAIL_ADDRESS, user.emailAddress);
            values.put(KEY_USER_PHONE, user.userPhone);
            values.put(KEY_BIRTH_DATE, user.birthDate);
            values.put(KEY_FULL_NAME, user.fullName);
            values.put(KEY_SKYPE_NAME, user.skypeName);
            values.put(KEY_JOB_TITLE, user.jobTitle);
            values.put(KEY_CITY, user.city);
            values.put(KEY_FAVORITE, user.favorite);

            mDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<User> getUsers() {
        String sql = String.format("SELECT * from %s", TABLE_NAME);
        Cursor mCursor = mDatabase.rawQuery(sql, null);
        if (mCursor != null) {
            ArrayList<User> users = new ArrayList<>();
            while (mCursor.moveToNext()) {
                User user = new User();

                user.userId = mCursor.getInt(mCursor.getColumnIndex(KEY_USER_ID));
                user.modifiedDate = mCursor.getLong(mCursor.getColumnIndex(KEY_MODIFIED_DATE));
                user.portraitUrl = mCursor.getString(mCursor.getColumnIndex(KEY_PORTRAIT_URL));
                user.screenName = mCursor.getString(mCursor.getColumnIndex(KEY_SCREEN_NAME));
                user.emailAddress = mCursor.getString(mCursor.getColumnIndex(KEY_EMAIL_ADDRESS));
                user.userPhone = mCursor.getString(mCursor.getColumnIndex(KEY_USER_PHONE));
                user.birthDate = mCursor.getLong(mCursor.getColumnIndex(KEY_BIRTH_DATE));
                user.fullName = mCursor.getString(mCursor.getColumnIndex(KEY_FULL_NAME));
                user.skypeName = mCursor.getString(mCursor.getColumnIndex(KEY_SKYPE_NAME));
                user.jobTitle = mCursor.getString(mCursor.getColumnIndex(KEY_JOB_TITLE));
                user.city = mCursor.getString(mCursor.getColumnIndex(KEY_CITY));
                user.favorite = mCursor.getInt(mCursor.getColumnIndex(KEY_FAVORITE)) == 1;

                users.add(user);
            }
            mCursor.close();
            return users;
        }
        return null;
    }

    public synchronized Cursor getUsersCursor(boolean favorites, String filter) {
        StringBuilder sb = new StringBuilder(String.format("SELECT rowid _id,* from %s", TABLE_NAME));
        if(favorites) {
            sb.append(String.format(" WHERE %s = 1", KEY_FAVORITE));
        }
        if(!TextUtils.isEmpty(filter)) {
            if(favorites) {
                sb.append(" AND");
            }
            else {
                sb.append(" WHERE");
            }
            sb.append(" " + KEY_FULL_NAME + " LIKE '%" + filter + "%'");
        }
        Cursor mCursor = mDatabase.rawQuery(sb.toString(), null);
        return mCursor;
    }

}