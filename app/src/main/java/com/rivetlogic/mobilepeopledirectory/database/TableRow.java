package com.rivetlogic.mobilepeopledirectory.database;

public class TableRow {
	private String name;
	private DbType type;
	private Nullable nullable;
	private Object defaultValue;
	private Delete delete;
	private int tableVersion;

	public enum DbType {
		INTEGER_PRIMARY_KEY, INTEGER_PRIMARY_KEY_AUTOINCREMENT, PRIMARY_KEY, INT, REAL, TEXT, BLOB;
		public String toString() {
			String typeStr;

            if (ordinal() == INTEGER_PRIMARY_KEY.ordinal())
                typeStr = "integer primary key";
            else if (ordinal() == INTEGER_PRIMARY_KEY_AUTOINCREMENT.ordinal())
                typeStr = "integer primary key autoincrement";
			else if (ordinal() == PRIMARY_KEY.ordinal())
				typeStr = "primary key";
			else if (ordinal() == INT.ordinal())
                typeStr = "integer";
            else if (ordinal() == REAL.ordinal())
                typeStr = "real";
			else if (ordinal() == TEXT.ordinal())
				typeStr = "text";
			else
				typeStr = "blob";
			return typeStr;
		}
	};

	public enum Nullable {
		TRUE, FALSE;
		public String toString() {
			String str;
			if (ordinal() == FALSE.ordinal())
				str = "not null";
			else
				str = "null";
			return str;
		}
	};

	public enum Delete {
		TRUE, FALSE;
	};

	public TableRow(int tableVersion, String name, Delete delete) {
		this.tableVersion = tableVersion;
		this.name = name;
		this.delete = delete;			
	}
	
	public TableRow(int tableVersion, String name, DbType type, Nullable nullable) {
		this(tableVersion, name, type, nullable, null);
	}
	
	public TableRow(int tableVersion, String name, DbType type, Nullable nullable, Object defaultValue) {
		this.tableVersion = tableVersion;
		this.name = name;
		this.type = type;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
	}

	public int getTableVersion() {
		return tableVersion;
	}
	public Delete getDelete() {
		return delete;
	}
	public boolean isDelete() {
		return delete == Delete.TRUE;
	}
	public String getName() {
		return name;
	}
	public DbType getType() {
		return type;
	}
	public Nullable getNullabe() {
		return nullable;
	}
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public String toString() {
		StringBuffer sql =
			new StringBuffer(String.format("%s %s %s", name, type.toString(), nullable.toString()));
		if (defaultValue != null) {
			sql.append(" default ");
			if (defaultValue instanceof String)
				sql.append(String.format("'%s'", defaultValue.toString()));
			else if (defaultValue instanceof Integer || defaultValue instanceof Long)
				sql.append(String.format("%d", defaultValue));
		}
		sql.append(',');
		return sql.toString();
	}
}
