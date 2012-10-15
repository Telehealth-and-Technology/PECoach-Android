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
package org.t2health.pe.tables;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class RecordingRating extends Table {
	public static final String TABLE_NAME = "recording_rating";
	public static final String FIELD_RECORDING_ID = "recording_id";
	public static final String FIELD_RATING_ID = "rating_id";
	public static final String FIELD_START_TIME = "start_time";
	public static final String FIELD_END_TIME = "end_time";
	
	public long recording_id;
	public long rating_id;
	public long startTime;
	public long endTime;

	public RecordingRating(DBAdapter d) {
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
		this.rating_id = c.getLong(c.getColumnIndex(FIELD_RATING_ID));
		this.startTime = c.getLong(c.getColumnIndex(FIELD_START_TIME));
		this.endTime = c.getLong(c.getColumnIndex(FIELD_END_TIME));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_RECORDING_ID), this.recording_id);
		cv.put(quote(FIELD_RATING_ID), this.rating_id);
		cv.put(quote(FIELD_START_TIME), this.startTime);
		cv.put(quote(FIELD_END_TIME), this.endTime);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_RECORDING_ID), this.recording_id);
		cv.put(quote(FIELD_RATING_ID), this.rating_id);
		cv.put(quote(FIELD_START_TIME), this.startTime);
		cv.put(quote(FIELD_END_TIME), this.endTime);
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
				quote(FIELD_RATING_ID) +" INTEGER," +
				quote(FIELD_START_TIME) +" INTEGER," +
				quote(FIELD_END_TIME) +" INTEGER" +
			")");
		this.dbAdapter.getDatabase().execSQL("CREATE UNIQUE INDEX IF NOT EXISTS map_index ON "+ quote(getTableName()) +" ("+ quote(FIELD_RECORDING_ID) +", "+ quote(FIELD_RATING_ID) +")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}
}
