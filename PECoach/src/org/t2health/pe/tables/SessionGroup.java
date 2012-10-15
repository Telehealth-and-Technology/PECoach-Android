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

import java.util.ArrayList;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class SessionGroup extends Table {
	public static final String TABLE_NAME = "session_group";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_THERISPIST_CONTACT_LOOKUP_KEY = "theripist_lookup_key";

	public String title;
	public String theripist_contact_lookup_key;

	public SessionGroup(DBAdapter d) {
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
		this.theripist_contact_lookup_key = c.getString(c.getColumnIndex(FIELD_THERISPIST_CONTACT_LOOKUP_KEY));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_TITLE), this.title);
		cv.put(quote(FIELD_THERISPIST_CONTACT_LOOKUP_KEY), this.theripist_contact_lookup_key);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_TITLE), this.title);
		cv.put(quote(FIELD_THERISPIST_CONTACT_LOOKUP_KEY), this.theripist_contact_lookup_key);
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
				quote(FIELD_TITLE) +" TEXT," +
				quote(FIELD_THERISPIST_CONTACT_LOOKUP_KEY) +" TEXT" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

	/*public Cursor getInVivosCursorWithRatings() {
		return dbAdapter.getDatabase().query(
				quote(Invivo.TABLE_NAME) +" t1 "+
				"INNER JOIN "+ quote(InvivoRating.TABLE_NAME) +"ir ON ir."+ quote(InvivoRating.FIELD_INVIVO_ID)+"=t1"+quote(Invivo.FIELD_ID) +" "+
				"LEFT JOIN "+ quote(Rating.TABLE_NAME) +" pre ON pre."+ quote(Rating.FIELD_ID)+"=ir."+quote(InvivoRating.FIELD_INVIVO_ID) +" AND pre."+ quote(Rating.FIELD_SESSION_ID) +"=? AND pre."+ quote(Rating.FIELD_TYPE) +"=? " +
				"LEFT JOIN "+ quote(Rating.TABLE_NAME) +" post ON post."+ quote(Rating.FIELD_ID)+"=ir."+quote(InvivoRating.FIELD_INVIVO_ID) +" AND post."+ quote(Rating.FIELD_SESSION_ID) +"=? AND post."+ quote(Rating.FIELD_TYPE) +"=? " +
				"LEFT JOIN "+ quote(Rating.TABLE_NAME) +" peak ON peak."+ quote(Rating.FIELD_ID)+"=ir."+quote(InvivoRating.FIELD_INVIVO_ID) +" AND peak."+ quote(Rating.FIELD_SESSION_ID) +"=? AND peak."+ quote(Rating.FIELD_TYPE) +"=? " +

				//"LEFT JOIN "+ quote(InvivoHomework.TABLE_NAME) +" hw ON hw."+ quote(InvivoHomework.FIELD_INVIVO_ID)+"=t1."+quote(Invivo.FIELD_ID) +" AND hw."+ quote(InvivoHomework.FIELD_SESSION_ID) +"=? " +
				"",
				new String[]{
					"t1."+quote(Invivo.FIELD_ID),
					"t1."+quote(Invivo.FIELD_TITLE),
					"t1."+quote(Invivo.FIELD_SESSION_GROUP_ID),
					//"IFNULL(hw."+quote(InvivoHomework.FIELD_SESSION_ID)+", 0) selected",
					"pre."+quote(Rating.FIELD_ID)+" preId",
					"pre."+quote(Rating.FIELD_VALUE)+" preValue",
					"post."+ quote(Rating.FIELD_ID)+" postId",
					"post."+ quote(Rating.FIELD_VALUE)+" postValue",
					"peak."+ quote(Rating.FIELD_ID)+" peakId",
					"peak."+ quote(Rating.FIELD_VALUE)+" peakValue",
				},
				null,
				new String[] {
					Rating.GROUP_SESSION_ID+"", Rating.TYPE_PRE,
					Rating.GROUP_SESSION_ID+"", Rating.TYPE_POST,
					Rating.GROUP_SESSION_ID+"", Rating.TYPE_PEAK,
					//this._id+"",
				},
				null,
				null,
				null
		);
	}*/

	public Cursor getInVivosCursor() {
		return dbAdapter.getDatabase().query(
				quote(Invivo.TABLE_NAME),
				null,
				quote(Invivo.FIELD_SESSION_GROUP_ID) +"=?",
				new String[] {
						this._id+""
				},
				null,
				null,
				null
		);
	}

	public ArrayList<Invivo> getInVivos() {
		ArrayList<Invivo> items = new ArrayList<Invivo>();
		Cursor c = getInVivosCursor();
		while(c.moveToNext()) {
			Invivo i = new Invivo(dbAdapter);
			i.load(c);
			items.add(i);
		}
		c.close();
		return items;
	}

	public Cursor getSessionsCursor() {
		return dbAdapter.getDatabase().query(
				quote(Session.TABLE_NAME),
				null,
				quote(Session.FIELD_GROUP_ID)+"=?",
				new String[] {
					this._id+""
				},
				null,
				null,
				quote(Session.FIELD_INDEX)
		);
	}

	public ArrayList<Session> getSessions() {
		ArrayList<Session> items = new ArrayList<Session>();
		Cursor c = getSessionsCursor();
		while(c.moveToNext()) {
			Session s = new Session(dbAdapter);
			s.load(c);
			items.add(s);
		}
		c.close();
		return items;
	}
}
