package com.rivetlogic.mobilepeopledirectory.data;

import android.content.ContentValues;
import android.content.Context;


import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.Vector;

public class Database {
	private static final String DATABASE_NAME = "com.rivetlogic.mobilepeoplefinder.database.db";
	private static final int DATABASE_VERSION = 1;
	
	private static DatabaseHelper mDbHelper = null;
	protected static SQLiteDatabase mDatabase;
	protected String tableName;
	
	private static final String VERSION_TABLE_NAME = "versions";
	public static final String KEY_ID = "_id";
	private static final String KEY_TABLE_NAME = "table_name";
	private static final String KEY_TABLE_VERSION = "table_version";

	private static TableRow[] versionTableDef = {
			new TableRow(1, KEY_ID, TableRow.DbType.INTEGER_PRIMARY_KEY, TableRow.Nullable.FALSE),
			new TableRow(1, KEY_TABLE_NAME, TableRow.DbType.TEXT, TableRow.Nullable.FALSE),
			new TableRow(1, KEY_TABLE_VERSION, TableRow.DbType.INT, TableRow.Nullable.FALSE)
	};
   
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTable(db, VERSION_TABLE_NAME, -1, versionTableDef, false);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(String.format("DROP TABLE IF EXISTS %s", VERSION_TABLE_NAME));
			createTable(db, VERSION_TABLE_NAME, -1, versionTableDef, false);
		}
	}
			
	protected synchronized Database open(Context context, String masterPassword, String tableName, int tableVersion, TableRow[] tableDef) throws SQLException {
		SQLiteDatabase.loadLibs(context);
		if (mDbHelper == null) {
			mDbHelper = new DatabaseHelper(context);
			mDatabase = mDbHelper.getWritableDatabase(masterPassword);
		}
		this.tableName = tableName;
		int version = getTableVersion(mDatabase, tableName);
		if (version == -1) {
			createTable(mDatabase, tableName, tableVersion, tableDef, false);
		} 
		else if (version != tableVersion) {
			upgradeTable(mDatabase, tableName, tableVersion, version, tableDef);
		}
		return this;
	}
	
	protected static void createTable(SQLiteDatabase db, String tableName, int newTableVersion, TableRow[] tableDef, boolean noTran) {
		StringBuffer sql = new StringBuffer(String.format("create table if not exists %s (", tableName));
		for (int i = 0, s = tableDef.length; i < s; i++) {
			if (!tableDef[i].isDelete())
				sql.append(tableDef[i].toString());
		}
		sql.setLength(sql.length() - 1);
		sql.append(");");
		if (!noTran) {
			db.beginTransaction();
		}
		try {
			db.execSQL(sql.toString());
			if (newTableVersion > 0) {
				setTableVersion(db, tableName, newTableVersion);
			}
			if (!noTran) {
				db.setTransactionSuccessful();
			}
		}
		catch (Exception e) {
			
		}
		if (!noTran)
			db.endTransaction();			
	}
		
	protected static void upgradeTable(SQLiteDatabase db, String tableName, int newTableVersion, int oldTableVersion, TableRow[] tableDef) {
		String tempTableName = String.format("%s_temp", tableName);
		StringBuffer columns = new StringBuffer();
		for (int i = 0, s = tableDef.length; i < s; i++) {			
			if (!tableDef[i].isDelete() && tableDef[i].getTableVersion() <= oldTableVersion) {			
				columns.append(tableDef[i].getName()).append(',');			
			}
		}
		columns.setLength(columns.length() - 1);
		Vector<String> sql = new Vector<String>();
		sql.add(String.format("INSERT INTO %s(%s) SELECT %s FROM %s",
                tempTableName, columns.toString(), columns.toString(), tableName));
		sql.add(String.format("DROP TABLE %s", tableName));
		sql.add(String.format("ALTER TABLE %s RENAME TO %s", tempTableName, tableName));
		db.beginTransaction();
		try {
			createTable(db, tempTableName, -1, tableDef, true);
			for (int i = 0, s = sql.size(); i < s; i++) {
				db.execSQL(sql.elementAt(i));
			}
			setTableVersion(db, tableName, newTableVersion);
			db.setTransactionSuccessful();
		}
		catch (Exception e) {

		}
		db.endTransaction();
	}
	
	protected static int setTableVersion(SQLiteDatabase db, String tableName, int newVersion) {
		ContentValues values = new ContentValues();
		values.put(KEY_TABLE_VERSION, newVersion);
		int result = db.update(VERSION_TABLE_NAME, values, String.format("%s=?", KEY_TABLE_NAME), new String[] {tableName});
		if (result == 0) {
			values.clear();
			values.put(KEY_TABLE_NAME, tableName);
			values.put(KEY_TABLE_VERSION, newVersion);
			db.insert(VERSION_TABLE_NAME, null, values);
			result = 1;
		}
		return result;
	}
	
	protected static int getTableVersion(SQLiteDatabase db, String tableName) {
		int version = -1;
		Cursor cursor = null;
		try {
			cursor = db.query(VERSION_TABLE_NAME, new String[] {KEY_TABLE_VERSION}, String.format("%s=?", KEY_TABLE_NAME), new String[] {tableName}, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				try {
					version = cursor.getInt(0);
				} 
				finally {
					cursor.close();
				}
			}
		} 
		catch (Exception e) {
			
		}
		return version;
	}
	
	public int getCount() {
		String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
		Cursor cursor = mDatabase.rawQuery(sql, null);
		int count = 0;
		if(null != cursor) {			
		    if(cursor.getCount() > 0) {
		      cursor.moveToFirst();    
		      count = cursor.getInt(0);
		    }		    
		    cursor.close();
		}		
		return count;
	}
}		