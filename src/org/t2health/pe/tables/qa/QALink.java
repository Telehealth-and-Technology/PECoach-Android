package org.t2health.pe.tables.qa;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class QALink extends Table {
	public static final String TABLE_NAME = "qa_link";
	public static final String FIELD_QUESTION_ID = "question_id";
	public static final String FIELD_ANSWER_ID = "answer_id";

	public long question_id;
	public long answer_id;

	public QALink(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.question_id = c.getLong(c.getColumnIndex(FIELD_QUESTION_ID));
		this.answer_id = c.getLong(c.getColumnIndex(FIELD_ANSWER_ID));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_QUESTION_ID), this.question_id);
		cv.put(quote(FIELD_ANSWER_ID), this.answer_id);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_QUESTION_ID), this.question_id);
		cv.put(quote(FIELD_ANSWER_ID), this.answer_id);
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
				quote(FIELD_QUESTION_ID) +" INTEGER," +
				quote(FIELD_ANSWER_ID) +" INTEGER" +
			")");
		this.dbAdapter.getDatabase().execSQL("CREATE UNIQUE INDEX IF NOT EXISTS question_answer ON "+ quote(getTableName()) +" ("+ quote(FIELD_QUESTION_ID) +", "+ quote(FIELD_ANSWER_ID) +")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

}
