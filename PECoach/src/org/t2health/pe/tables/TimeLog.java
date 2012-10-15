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
import android.util.Log;

public class TimeLog extends Table {
	private static final String TAG = TimeLog.class.getSimpleName();
	
	public static final String TABLE_NAME = "time_log";
	public static final String FIELD_SESSION_ID = "session_id";
	public static final String FIELD_TAG = "tag";
	public static final String FIELD_DURATION = "duration";
	public static final String FIELD_TIMESTAMP = "timestamp";

	public long session_id;
	public String tag;
	public long duration;
	public long timestamp;
	
	public static class TAGS {
		public static final String LISTEN_TO_EXPLINATION_OF_PE = "listenToExplination";
		public static final String LISTEN_TO_COMMON_REACTIONS = "listenToCommonReactions";
		public static final String REVIEW_ADDED_INVIVO_ITEMS = "reviewAddedInvivoItems";
		public static final String REVIEW_INVIVO_HOMEWORK = "reviewInvivoHomework";
		public static final String PRACTICE_BREATHING_RETRAINER = "practiceBreatingRetrainer";
		public static final String PLAY_SESSION_RECORDING = "playSessionRecording";
		public static final String PLAY_IMAGINAL_EXPOSURE_RECORDING = "playImaginalExposure";
	}
	
	public TimeLog(DBAdapter d) {
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
		this.tag = c.getString(c.getColumnIndex(FIELD_TAG));
		this.duration = c.getLong(c.getColumnIndex(FIELD_DURATION));
		this.timestamp = c.getLong(c.getColumnIndex(FIELD_TIMESTAMP));
		return true;
	}
	
	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(FIELD_SESSION_ID, this.session_id);
		cv.put(FIELD_TAG, this.tag);
		cv.put(FIELD_DURATION, this.duration);
		cv.put(FIELD_TIMESTAMP, this.timestamp);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(FIELD_SESSION_ID, this.session_id);
		cv.put(FIELD_TAG, this.tag);
		cv.put(FIELD_DURATION, this.duration);
		cv.put(FIELD_TIMESTAMP, this.timestamp);
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
				quote(FIELD_SESSION_ID) +" INTEGER," +
				quote(FIELD_TAG) +" TEXT," +
				quote(FIELD_DURATION) +" INTEGER," +
				quote(FIELD_TIMESTAMP) +" INTEGER" +
			")");
		this.dbAdapter.getDatabase().execSQL("CREATE INDEX IF NOT EXISTS name_session_index ON "+ quote(getTableName()) +" ("+ quote(FIELD_SESSION_ID) +", "+ quote(FIELD_TAG)+")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}
	
	public long getDuration(long sessionId, String tag) {
		Cursor c = dbAdapter.getDatabase().query(
				quote(TABLE_NAME), 
				new String[] {
					"SUM(duration) dur",
				}, 
				quote(TimeLog.FIELD_SESSION_ID)+"=? AND "+ quote(TimeLog.FIELD_TAG)+"=?", 
				new String[]{
					sessionId+"",
					tag+"",
				}, 
				null, 
				null, 
				null
		);
		
		long duration = 0;
		if(c.moveToFirst()) {
			duration = c.getLong(0);
		}
		c.close();
		
		Log.v(TAG, "get:"+ duration +", "+ sessionId +", "+tag);
		
		return duration;
	}
	
	public boolean setDuration(long sessionId, String tag, long duration) {
		TimeLog l = new TimeLog(dbAdapter);
		l.duration = duration;
		l.session_id = sessionId;
		l.timestamp = System.currentTimeMillis();
		l.tag = tag;
		if(l.duration > 0) {
			Log.v(TAG, "set:"+ l.duration +", "+ l.session_id +", "+l.timestamp+", "+l.tag);
			return l.save();
		}
		return false;
	}

}
