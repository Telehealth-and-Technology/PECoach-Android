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

public class Rating extends Table {
	public static final String TABLE_NAME = "rating";
	public static final String FIELD_PRE_VALUE = "pre_value";
	public static final String FIELD_POST_VALUE = "post_value";
	public static final String FIELD_PEAK_VALUE = "peak_value";
	public static final String FIELD_PRE_TIMESTAMP = "pre_timestamp";
	public static final String FIELD_POST_TIMESTAMP = "post_timestamp";
	public static final String FIELD_PEAK_TIMESTAMP = "peak_timestamp";
	public static final String FIELD_TIMESTAMP = "timestamp";

	public static final int GROUP_SESSION_ID = 0;
	
	public int preValue = -1;
	public int postValue = -1;
	public int peakValue = -1;
	
	public long preTimestamp = -1;
	public long postTimestamp = -1;
	public long peakTimestamp = -1;
	
	public long timestamp;

	public Rating(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.preValue = c.getInt(c.getColumnIndex(FIELD_PRE_VALUE));
		this.postValue = c.getInt(c.getColumnIndex(FIELD_POST_VALUE));
		this.peakValue = c.getInt(c.getColumnIndex(FIELD_PEAK_VALUE));
		this.preTimestamp = c.getLong(c.getColumnIndex(FIELD_PRE_TIMESTAMP));
		this.postTimestamp = c.getLong(c.getColumnIndex(FIELD_POST_TIMESTAMP));
		this.peakTimestamp = c.getLong(c.getColumnIndex(FIELD_PEAK_TIMESTAMP));
		this.timestamp = c.getLong(c.getColumnIndex(FIELD_TIMESTAMP));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_PRE_VALUE), this.preValue);
		cv.put(quote(FIELD_POST_VALUE), this.postValue);
		cv.put(quote(FIELD_PEAK_VALUE), this.peakValue);
		cv.put(quote(FIELD_PRE_TIMESTAMP), this.preTimestamp);
		cv.put(quote(FIELD_POST_TIMESTAMP), this.postTimestamp);
		cv.put(quote(FIELD_PEAK_TIMESTAMP), this.peakTimestamp);
		cv.put(quote(FIELD_TIMESTAMP), this.timestamp);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_PRE_VALUE), this.preValue);
		cv.put(quote(FIELD_POST_VALUE), this.postValue);
		cv.put(quote(FIELD_PEAK_VALUE), this.peakValue);
		cv.put(quote(FIELD_PRE_TIMESTAMP), this.preTimestamp);
		cv.put(quote(FIELD_POST_TIMESTAMP), this.postTimestamp);
		cv.put(quote(FIELD_PEAK_TIMESTAMP), this.peakTimestamp);
		cv.put(quote(FIELD_TIMESTAMP), this.timestamp);
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
				quote(FIELD_PRE_VALUE) +" INTEGER," +
				quote(FIELD_POST_VALUE) +" INTEGER," +
				quote(FIELD_PEAK_VALUE) +" INTEGER," +
				quote(FIELD_PRE_TIMESTAMP) +" INTEGER," +
				quote(FIELD_POST_TIMESTAMP) +" INTEGER," +
				quote(FIELD_PEAK_TIMESTAMP) +" INTEGER," +
				quote(FIELD_TIMESTAMP) +" INTEGER" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

}
