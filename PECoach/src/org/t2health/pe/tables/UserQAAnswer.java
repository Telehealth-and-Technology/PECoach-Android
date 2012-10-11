package org.t2health.pe.tables;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class UserQAAnswer extends Table {
	public static final String TABLE_NAME = "user_qa_answer";
	public static final String FIELD_QUESTION_ID = "question_id";
	public static final String FIELD_ANSWER_ID = "answer_id";
	public static final String FIELD_SESSION_ID = "session_id";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_QUESTION_TEXT = "question_text";
	public static final String FIELD_QUESTION_WEIGHT = "question_weight";
	public static final String FIELD_ANSWER_TEXT = "answer_text";
	public static final String FIELD_ANSWER_VALUE = "answer_value";

	public long question_id;
	public long answer_id;
	public long session_id;
	public long timestamp;
	public String questionText;
	public int questionWeight;
	public String answerText;
	public int answerValue;

	public UserQAAnswer(DBAdapter d) {
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
		this.session_id = c.getLong(c.getColumnIndex(FIELD_SESSION_ID));
		this.timestamp = c.getLong(c.getColumnIndex(FIELD_TIMESTAMP));
		this.questionText = c.getString(c.getColumnIndex(FIELD_QUESTION_TEXT));
		this.questionWeight = c.getInt(c.getColumnIndex(FIELD_QUESTION_WEIGHT));
		this.answerText = c.getString(c.getColumnIndex(FIELD_ANSWER_TEXT));
		this.answerValue = c.getInt(c.getColumnIndex(FIELD_ANSWER_VALUE));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_QUESTION_ID), this.question_id);
		cv.put(quote(FIELD_ANSWER_ID), this.answer_id);
		cv.put(quote(FIELD_SESSION_ID), this.session_id);
		cv.put(quote(FIELD_TIMESTAMP), this.timestamp);
		cv.put(quote(FIELD_QUESTION_TEXT), this.questionText);
		cv.put(quote(FIELD_QUESTION_WEIGHT), this.questionWeight);
		cv.put(quote(FIELD_ANSWER_TEXT), this.answerText);
		cv.put(quote(FIELD_ANSWER_VALUE), this.answerValue);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_QUESTION_ID), this.question_id);
		cv.put(quote(FIELD_ANSWER_ID), this.answer_id);
		cv.put(quote(FIELD_SESSION_ID), this.session_id);
		cv.put(quote(FIELD_TIMESTAMP), this.timestamp);
		cv.put(quote(FIELD_QUESTION_TEXT), this.questionText);
		cv.put(quote(FIELD_QUESTION_WEIGHT), this.questionWeight);
		cv.put(quote(FIELD_ANSWER_TEXT), this.answerText);
		cv.put(quote(FIELD_ANSWER_VALUE), this.answerValue);
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
				quote(FIELD_ANSWER_ID) +" INTEGER," +
				quote(FIELD_SESSION_ID) +" INTEGER," +
				quote(FIELD_TIMESTAMP) +" LONG," +
				quote(FIELD_QUESTION_TEXT) +" TEXT," +
				quote(FIELD_QUESTION_WEIGHT) +" TEXT," +
				quote(FIELD_ANSWER_TEXT) +" TEXT," +
				quote(FIELD_ANSWER_VALUE) +" INTEGER" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}
	
	public Cursor getResponsesCursor(long sessionId) {
		return this.dbAdapter.getDatabase().query(
				quote(TABLE_NAME), 
				null, 
				quote(FIELD_SESSION_ID)+"=?", 
				new String[] {
					sessionId+""
				}, 
				null, 
				null, 
				quote(FIELD_QUESTION_WEIGHT)+""
		);
	}
	
	public Cursor getReportSessionCursor() {
		String rQuery = "select distinct session_id from user_qa_answer";
		return this.dbAdapter.getDatabase().rawQuery(rQuery, null);
	}
	public Cursor getReportDataCursor(String rsession) {
		String rQuery = "select * from user_qa_answer where session_id = " + rsession;
		return this.dbAdapter.getDatabase().rawQuery(rQuery, null);
	}
}
