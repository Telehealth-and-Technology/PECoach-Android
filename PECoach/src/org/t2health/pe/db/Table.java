package org.t2health.pe.db;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class Table extends AbsTable {
	public static final String FIELD_ID = "_id";
	
//	private static final String TAG = "TABLE";
	public long _id;

	public Table(DBAdapter d) {
		super(d);
	}

	@Override
	public boolean delete() {
		ContentValues whereConditions = new ContentValues();
		whereConditions.put(FIELD_ID, this._id);

		return this.delete(whereConditions) > 0;
	}

	@Override
	public boolean load() {
		ContentValues whereConditions = new ContentValues();
		whereConditions.put(FIELD_ID, this._id);

		Cursor c = this.select(whereConditions);
		if(!c.moveToNext()) {
			c.close();
			return false;
		}
		boolean res = this.load(c);
		this._id = c.getLong(c.getColumnIndex(FIELD_ID));
		c.close();

		return res;
	}
	
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex(FIELD_ID));
		return true;
	}

	@Override
	public boolean save() {
		if(this._id > 0) {
			this.update();
		} else {
			this._id = this.insert();
		}
		return this.load();
	}
	
	public void empty() {
		this.dbAdapter.getDatabase().execSQL("DELETE FROM `"+ this.getTableName()+"`");
	}

	@Override
	public abstract String getTableName();

	@Override
	public abstract long insert();

	@Override
	public abstract boolean update();

	@Override
	public abstract void onCreate();

	@Override
	public abstract void onUpgrade(int oldVersion, int newVersion);
}
