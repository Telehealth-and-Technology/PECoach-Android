/*
 * 
 * PECoach
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: PECoach001
 * Government Agency Original Software Title: PECoach
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.pe.tables.qa;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class QAQuestion extends Table {
	public static final String TABLE_NAME = "qa_question";
	public static final String FIELD_SET_ID = "qa_set_id";
	public static final String FIELD_TEXT = "text";
	public static final String FIELD_WEIGHT = "weight";
	public static final String FIELD_CORRECT_ANSWER = "correct_answer_id";

	public long qa_set_id;
	public String text;
	public int weight;
	public long correct_answer_id;

	public QAQuestion(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.qa_set_id = c.getLong(c.getColumnIndex(FIELD_SET_ID));
		this.text = c.getString(c.getColumnIndex(FIELD_TEXT));
		this.weight = c.getInt(c.getColumnIndex(FIELD_WEIGHT));
		this.correct_answer_id = c.getLong(c.getColumnIndex(FIELD_CORRECT_ANSWER));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SET_ID), this.qa_set_id);
		cv.put(quote(FIELD_TEXT), this.text);
		cv.put(quote(FIELD_WEIGHT), this.weight);
		cv.put(quote(FIELD_CORRECT_ANSWER), this.correct_answer_id);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SET_ID), this.qa_set_id);
		cv.put(quote(FIELD_TEXT), this.text);
		cv.put(quote(FIELD_WEIGHT), this.weight);
		cv.put(quote(FIELD_CORRECT_ANSWER), this.correct_answer_id);
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
				quote(FIELD_SET_ID) +" INTEGER," +
				quote(FIELD_TEXT) +" TEXT," +
				quote(FIELD_WEIGHT) +" INTEGER," +
				quote(FIELD_CORRECT_ANSWER) +" INTEGER" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

	public Cursor getAnswersCursor() {
		return dbAdapter.getDatabase().query(
				quote(QAAnswer.TABLE_NAME), 
				null, 
				quote(QAAnswer.FIELD_ID)+ " IN(SELECT "+ quote(QALink.FIELD_ANSWER_ID) +" FROM "+ quote(QALink.TABLE_NAME) +" WHERE "+ quote(QALink.FIELD_QUESTION_ID) +"=?)", 
				new String[] {
					this._id+""
				}, 
				null, 
				null, 
				null
		);
	}

}
