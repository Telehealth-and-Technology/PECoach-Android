package org.t2health.pe.tables.qa;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class QAAnswer extends Table {
	public static final String TABLE_NAME = "qa_answer";
	public static final String FIELD_TEXT = "text";
	public static final String FIELD_VALUE = "value";
	public static final String FIELD_WEIGHT = "weight";

	public String text;
	public int value;
	public int weight;

	public QAAnswer(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.text = c.getString(c.getColumnIndex(FIELD_TEXT));
		this.value = c.getInt(c.getColumnIndex(FIELD_VALUE));
		this.weight = c.getInt(c.getColumnIndex(FIELD_WEIGHT));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_TEXT), this.text);
		cv.put(quote(FIELD_VALUE), this.value);
		cv.put(quote(FIELD_WEIGHT), this.weight);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_TEXT), this.text);
		cv.put(quote(FIELD_VALUE), this.value);
		cv.put(quote(FIELD_WEIGHT), this.weight);
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
				quote(FIELD_TEXT) +" TEXT," +
				quote(FIELD_VALUE) +" INTEGER," +
				quote(FIELD_WEIGHT) +" INTEGER" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

}
