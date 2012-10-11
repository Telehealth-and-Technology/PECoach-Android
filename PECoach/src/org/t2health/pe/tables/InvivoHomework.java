package org.t2health.pe.tables;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class InvivoHomework extends Table {
	public static final String TABLE_NAME = "invivo_homework";
	public static final String FIELD_SESSION_ID = "session_id";
	public static final String FIELD_INVIVO_ID = "invivo_id";
	public static final String FIELD_RATING_ID = "rating_id";
	
	public long session_id;
	public long invivo_id;
	public long rating_id;
	
	public InvivoHomework(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.session_id = c.getLong(c.getColumnIndex(FIELD_SESSION_ID));
		this.invivo_id = c.getLong(c.getColumnIndex(FIELD_INVIVO_ID));
		this.rating_id = c.getLong(c.getColumnIndex(FIELD_RATING_ID));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SESSION_ID), this.session_id);
		cv.put(quote(FIELD_INVIVO_ID), this.invivo_id);
		cv.put(quote(FIELD_RATING_ID), this.rating_id);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SESSION_ID), this.session_id);
		cv.put(quote(FIELD_INVIVO_ID), this.invivo_id);
		cv.put(quote(FIELD_RATING_ID), this.rating_id);
		return this.dbAdapter.getDatabase().update(
				quote(getTableName()),
				cv,
				quote(FIELD_ID) +"=?",
				new String[] {
					this._id+""
				}
		) > 0;
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE IF NOT EXISTS "+ quote(getTableName()) +" (" +
				quote(FIELD_ID) +" INTEGER PRIMARY KEY AUTOINCREMENT," +
				quote(FIELD_SESSION_ID) +" INTEGER," +
				quote(FIELD_INVIVO_ID) +" INTEGER," +
				quote(FIELD_RATING_ID) +" INTEGER" +
			")");
		this.dbAdapter.getDatabase().execSQL("CREATE UNIQUE INDEX IF NOT EXISTS map_index ON "+ quote(getTableName()) +" ("+ quote(FIELD_SESSION_ID) +", "+ quote(FIELD_INVIVO_ID) +", "+ quote(FIELD_RATING_ID)+")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}
}
