package org.t2health.pe.tables;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class RecordingClip extends Table {
	public static final String TABLE_NAME = "recording_clip";
	public static final String FIELD_RECORDING_ID = "recording_id";
	public static final String FIELD_FILE_PATH = "file_path";
	public static final String FIELD_DURATION = "duration";

	public long recording_id;
	public String file_path;
	public long duration = 0;

	public RecordingClip(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.recording_id = c.getLong(c.getColumnIndex(FIELD_RECORDING_ID));
		this.file_path = c.getString(c.getColumnIndex(FIELD_FILE_PATH));
		this.duration = c.getLong(c.getColumnIndex(FIELD_DURATION));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_RECORDING_ID), this.recording_id);
		cv.put(quote(FIELD_FILE_PATH), this.file_path);
		cv.put(quote(FIELD_DURATION), this.duration);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_RECORDING_ID), this.recording_id);
		cv.put(quote(FIELD_FILE_PATH), this.file_path);
		cv.put(quote(FIELD_DURATION), this.duration);
		return this.dbAdapter.getDatabase().update(
				quote(getTableName()),
				cv,
				quote(FIELD_ID) +"=?",
				new String[] {
					this._id+"",
				}) > 0;
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE IF NOT EXISTS "+ quote(getTableName()) +" (" +
				quote(FIELD_ID) +" INTEGER PRIMARY KEY AUTOINCREMENT," +
				quote(FIELD_RECORDING_ID) +" INTEGER," +
				quote(FIELD_FILE_PATH) +" TEXT," +
				quote(FIELD_DURATION) +" INTEGER" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}
}
