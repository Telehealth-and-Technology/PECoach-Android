/*
 * 
 * PECoach
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
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

public class InvivoRating extends Table {
	public static final String TABLE_NAME = "invivo_rating";
	public static final String FIELD_INVIVO_ID = "invivo_id";
	public static final String FIELD_RATING_ID = "rating_id";
	
	public long invivo_id;
	public long rating_id;

	public InvivoRating(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.invivo_id = c.getLong(c.getColumnIndex(FIELD_INVIVO_ID));
		this.rating_id = c.getLong(c.getColumnIndex(FIELD_RATING_ID));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_INVIVO_ID), this.invivo_id);
		cv.put(quote(FIELD_RATING_ID), this.rating_id);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_INVIVO_ID), this.invivo_id);
		cv.put(quote(FIELD_RATING_ID), this.rating_id);
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
				quote(FIELD_INVIVO_ID) +" INTEGER," +
				quote(FIELD_RATING_ID) +" INTEGER" +
			")");
		this.dbAdapter.getDatabase().execSQL("CREATE UNIQUE INDEX IF NOT EXISTS map_index ON "+ quote(getTableName()) +" ("+ quote(FIELD_INVIVO_ID) +", "+ quote(FIELD_RATING_ID) +")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

}
