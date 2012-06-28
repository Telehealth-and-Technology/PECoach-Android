package org.t2health.pe.tables.qa;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class QASet extends Table {
	public static final String TABLE_NAME = "qa_set";
	public static final String FIELD_TITLE = "title";

	public String title;

	public QASet(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.title = c.getString(c.getColumnIndex(FIELD_TITLE));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_TITLE), this.title);
		return this.dbAdapter.getDatabase().insert(getTableName(), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_TITLE), this.title);
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
				quote(FIELD_TITLE) +" TEXT" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

	public Cursor getQuestionsCursor() {
		return dbAdapter.getDatabase().query(
				quote(QAQuestion.TABLE_NAME),
				null,
				quote(QAQuestion.FIELD_SET_ID) +"=?",
				new String[] {
						this._id+""
				},
				null,
				null,
				null
		);
	}

	public QAQuestion[] getQuestions() {
		Cursor c = this.getQuestionsCursor();
		QAQuestion[] questions = new QAQuestion[c.getCount()];
		while(c.moveToNext()) {
			QAQuestion q = new QAQuestion(dbAdapter);
			q.load(c);
			questions[c.getPosition()] = q;
		}
		c.close();
		return questions;
	}
}
