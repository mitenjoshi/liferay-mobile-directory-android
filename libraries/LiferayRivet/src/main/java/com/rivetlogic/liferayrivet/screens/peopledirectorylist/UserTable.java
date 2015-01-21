package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

import android.database.SQLException;

import com.rivetlogic.liferayrivet.database.Database;
import com.rivetlogic.liferayrivet.database.TableRow;

/**
 * Copyright (c) 2014 HMR Weight Management Services Corp.. All rights reserved.  10/13/14.
 */
public class UserTable extends Database {
    private static final String TABLE_NAME = "user";
    private static final int TABLE_VERSION = 1;

    private static final String KEY_TRACKER_DAY= "col_tracker_day";
    private static final String KEY_WEIGHT =       "col_weight";

    private TableRow[] tableDef = {
            new TableRow(1, KEY_WEIGHT, TableRow.DbType.REAL, TableRow.Nullable.FALSE),
            new TableRow(1, KEY_TRACKER_DAY, TableRow.DbType.INTEGER_PRIMARY_KEY, TableRow.Nullable.FALSE)
    };

    public UserTable() {
        super();
        open();
    }

    public void open() throws SQLException {
        super.open(TABLE_NAME, TABLE_VERSION, tableDef);
    }

    public long getWeightEntryCount() {
        return getCount();
    }

}