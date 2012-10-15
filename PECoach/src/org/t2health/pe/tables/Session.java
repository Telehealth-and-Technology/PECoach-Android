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

import org.t2health.pe.Constant;
import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class Session extends Table {
	public static final String TABLE_NAME = "session";
	public static final String FIELD_GROUP_ID = "group_id";
	public static final String FIELD_INDEX = "index";
	public static final String FIELD_SECTION = "section"; //Used for 2A/2B split - steveo
	public static final String FIELD_IS_FINAL = "is_final";

	public long group_id = 0;
	public int index = -1;
	public int section = 0;
	public boolean is_final = false;

	public Session(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.group_id = c.getLong(c.getColumnIndex(FIELD_GROUP_ID));
		this.index = c.getInt(c.getColumnIndex(FIELD_INDEX));
		this.section = c.getInt(c.getColumnIndex(FIELD_SECTION));
		this.is_final = c.getInt(c.getColumnIndex(FIELD_IS_FINAL)) > 0;
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_GROUP_ID), this.group_id);
		cv.put(quote(FIELD_INDEX), this.index);
		cv.put(quote(FIELD_SECTION), this.section);
		cv.put(quote(FIELD_IS_FINAL), this.is_final?1:0);
		return this.getDBAdapter().getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_GROUP_ID), this.group_id);
		cv.put(quote(FIELD_INDEX), this.index);
		cv.put(quote(FIELD_SECTION), this.section);
		cv.put(quote(FIELD_IS_FINAL), this.is_final?1:0);
		return this.dbAdapter.getDatabase().update(
				quote(getTableName()),
				cv,
				quote(FIELD_ID) +"=?",
				new String[]{
					this._id+""
				}
				) > 0;
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE IF NOT EXISTS "+ quote(getTableName()) +" (" +
				quote(FIELD_ID) +" INTEGER PRIMARY KEY AUTOINCREMENT," +
				quote(FIELD_GROUP_ID) +" INTEGER," +
				quote(FIELD_INDEX) +" INTEGER," +
				quote(FIELD_SECTION) +" INTEGER DEFAULT 0," +
				quote(FIELD_IS_FINAL) +" INTEGER" +
				")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {
		try
		{
			if(oldVersion < newVersion)
			{
				this.dbAdapter.getWritableDatabase().execSQL("ALTER TABLE "+ quote(getTableName()) +" ADD " + quote(FIELD_SECTION) +" INTEGER DEFAULT 0");
				this.dbAdapter.getWritableDatabase().execSQL("UPDATE "+ quote(getTableName()) +" SET " + quote(FIELD_SECTION) +" = 0 ");
			}
		}
		catch(Exception ex){}
	}

	public Session getPreviousSession(boolean split) { 
		if(split)
		{
			if(index == 2) //if at 3, prev is 2B
				return getSessionAt(group_id, index-1, 1);
			else if((index == 1) && (section == 1)) //if 2B, prev is 2A
				return getSessionAt(group_id, index, 0);
			else
				return getSessionAt(group_id, index-1, 0);
		}
		else
			return getSessionAt(group_id, index-1, 0);
	}

	public Session getNextSession(boolean split) {
		if(split)
		{
			if((index == 1) && (section == 0)) 
			{
				Session s = getSessionAt(group_id, index, 1);
				if(s != null) return s;
				else 
					return createNextSession(index, 1);

			}
			else
			{
				Session s = getSessionAt(group_id, index+1, 0);
				if(s != null) return s;
				else 
					return createNextSession(index+1, 0);
			}
		}
		else
		{
			Session s = getSessionAt(group_id, index+1, 0);
			if(s != null) return s;
			else 
				return createNextSession(index+1, 0);
		}
	}

	public Session getSessionAt(long groupId, int index, int section) {
		
		Cursor c = null;
		if(section == 1)
		{
			c = dbAdapter.getDatabase().query(
					quote(getTableName()),
					null,
					quote(Session.FIELD_GROUP_ID) +"=? AND "+ quote(Session.FIELD_INDEX) +"=? AND "+ quote(Session.FIELD_SECTION) +"=?",
					new String[] {
						groupId+"",
						index+"",
						section+"",
					},
					null,
					null,
					null
					);
			if(c.getCount() == 0){
				c.close();
				return null;
			}
		}
		else
		{
			c = dbAdapter.getDatabase().query(
					quote(getTableName()),
					null,
					quote(Session.FIELD_GROUP_ID) +"=? AND "+ quote(Session.FIELD_INDEX) +"=?",
					new String[] {
						groupId+"",
						index+"",
					},
					null,
					null,
					null
					);
			if(c.getCount() == 0){
				c.close();
				return null;
			}
		}
		

		Session s = new Session(dbAdapter);
		c.moveToFirst();
		s.load(c);
		c.close();
		return s;
	}

	public Session createNextSession(int inIndex, int inSection) {
		Session s = new Session(dbAdapter);
		s.index = inIndex;
		s.section = inSection;
		s.group_id = this.group_id;
		s.save();
		return s;
	}

	/*public Session createFinalSession() {
		Session s = new Session(dbAdapter);
		s.index = this.index + 1;
		s.group_id = this.group_id;
		s.is_final = true;
		s.save();
		return s;
	}*/
	public Session createFinalSession() {
		Cursor c = dbAdapter.getDatabase().query(
				quote(TABLE_NAME),
				new String[] {
					"MAX("+quote(FIELD_INDEX)+")",
				},
				quote(FIELD_GROUP_ID)+"=?",
				new String[] {
					group_id+""
				},
				null,
				null,
				null
				);
		int lastIndex = 0;
		if(c.moveToFirst()) {
			lastIndex = c.getInt(0);
			c.close();
		} else {
			c.close();
		}

		Session session = new Session(dbAdapter);
		session.index = lastIndex+1;
		session.section = 0;
		session.group_id = this.group_id;
		session.is_final = true;
		session.save();
		return session;
	}

	public boolean isFirstSession() {
		return this.index == 0;
	}

	public void deleteUserQAAnswers() {
		dbAdapter.getDatabase().delete(
				quote(UserQAAnswer.TABLE_NAME),
				quote(UserQAAnswer.FIELD_SESSION_ID) +"=?",
				new String[] {
					this._id+"",
				}
				);
	}

	public int getRecordingsCount() {
		Cursor c = dbAdapter.getDatabase().query(
				quote(Recording.TABLE_NAME),
				new String[] {
					"COUNT(*) cnt",
				},
				quote(Recording.FIELD_SESSION_ID) +"=?",
				new String[] {
					this._id+""
				},
				null,
				null,
				null
				);

		c.moveToFirst();
		int cnt = c.getInt(0);
		c.close();
		return cnt;
	}

	public Cursor getRecordingsCursor() {
		return dbAdapter.getDatabase().query(
				quote(Recording.TABLE_NAME),
				null,
				quote(Recording.FIELD_SESSION_ID) +"=?",
				new String[] {
					this._id+""
				},
				null,
				null,
				null
				);
	}

	public ArrayList<Recording> getRecordings() {
		ArrayList<Recording> recordings = new ArrayList<Recording>();
		Cursor c = getRecordingsCursor();
		while(c.moveToNext()) {
			Recording r = new Recording(dbAdapter);
			r.load(c);
			recordings.add(r);
		}
		c.close();
		return recordings;
	}

	public SessionGroup getSessionGroup() {
		SessionGroup g = new SessionGroup(dbAdapter);
		g._id = this.group_id;
		if(g.load()) {
			return g;
		}
		return null;
	}

	/*public Cursor getSelectedInVivosCursorWithRatings() {
		return dbAdapter.getDatabase().query(
				quote(Invivo.TABLE_NAME) +" t1 "+
				"INNER JOIN "+ quote(InvivoRating.TABLE_NAME) +" ir ON ir."+ quote(InvivoRating.FIELD_INVIVO_ID)+"=t1."+quote(Invivo.FIELD_ID) +" "+
				"LEFT JOIN "+ quote(Rating.TABLE_NAME) +" pre ON pre."+ quote(Rating.FIELD_ID)+"=ir."+quote(InvivoRating.FIELD_INVIVO_ID) +" AND pre."+ quote(Rating.FIELD_SESSION_ID) +"=? AND pre."+ quote(Rating.FIELD_TYPE) +"=? " +
				"LEFT JOIN "+ quote(Rating.TABLE_NAME) +" post ON post."+ quote(Rating.FIELD_ID)+"=ir."+quote(InvivoRating.FIELD_INVIVO_ID) +" AND post."+ quote(Rating.FIELD_SESSION_ID) +"=? AND post."+ quote(Rating.FIELD_TYPE) +"=? " +
				"LEFT JOIN "+ quote(Rating.TABLE_NAME) +" peak ON peak."+ quote(Rating.FIELD_ID)+"=ir."+quote(InvivoRating.FIELD_INVIVO_ID) +" AND peak."+ quote(Rating.FIELD_SESSION_ID) +"=? AND peak."+ quote(Rating.FIELD_TYPE) +"=? " +

				"LEFT JOIN "+ quote(InvivoHomework.TABLE_NAME) +" hw ON hw."+ quote(InvivoHomework.FIELD_INVIVO_ID)+"=t1."+quote(Invivo.FIELD_ID) +" AND hw."+ quote(InvivoHomework.FIELD_SESSION_ID) +"=? " +
				"",
				new String[]{
					"t1."+quote(Invivo.FIELD_ID),
					"t1."+quote(Invivo.FIELD_TITLE),
					"t1."+quote(Invivo.FIELD_SESSION_GROUP_ID),
					"IFNULL(hw."+quote(InvivoHomework.FIELD_SESSION_ID)+", 0) selected",
					"pre."+quote(Rating.FIELD_ID)+" preId",
					"pre."+quote(Rating.FIELD_VALUE)+" preValue",
					"post."+ quote(Rating.FIELD_ID)+" postId",
					"post."+ quote(Rating.FIELD_VALUE)+" postValue",
					"peak."+ quote(Rating.FIELD_ID)+" peakId",
					"peak."+ quote(Rating.FIELD_VALUE)+" peakValue",
				},
				null,
				new String[] {
					this._id+"", Rating.TYPE_PRE,
					this._id+"", Rating.TYPE_POST,
					this._id+"", Rating.TYPE_PEAK,
					this._id+"",
				},
				null,
				null,
				null
		);
	}*/

	public void deleteInvivoHomework() {
		dbAdapter.getDatabase().delete(
				quote(InvivoHomework.TABLE_NAME),
				quote(InvivoHomework.FIELD_SESSION_ID)+"=?",
				new String[] {
					this._id+""
				}
				);
	}

	public Cursor getInvivoHomeworkRatingsCursor(long invivoId) {
		return dbAdapter.getDatabase().query(
				quote(Rating.TABLE_NAME),
				null,
				quote(Rating.FIELD_ID)+" IN(SELECT"+ quote(InvivoHomework.FIELD_RATING_ID) +" FROM "+ quote(InvivoHomework.TABLE_NAME) +" WHERE "+ quote(InvivoHomework.FIELD_INVIVO_ID)+"=? AND "+ quote(InvivoHomework.FIELD_SESSION_ID)+"=? )",
				new String[] {
					invivoId+"",
					this._id+"",
				},
				null,
				null,
				null
				);
	}

	public ArrayList<Rating> getInvivoHomeworkRatings(long invivoId) {
		ArrayList<Rating> items = new ArrayList<Rating>();
		Cursor c = getInvivoHomeworkRatingsCursor(invivoId);
		while(c.moveToNext()) {
			Rating r = new Rating(dbAdapter);
			r.load(c);
			items.add(r);
		}
		c.close();
		return items;
	}

	public Cursor getInivoHomeworkItemsCursor() {
		return dbAdapter.getDatabase().query(
				quote(Invivo.TABLE_NAME),
				null,
				quote(Invivo.FIELD_ID)+" IN(SELECT "+ quote(InvivoHomework.FIELD_INVIVO_ID) +" FROM "+ quote(InvivoHomework.TABLE_NAME) +" WHERE "+ quote(InvivoHomework.FIELD_SESSION_ID)+"=?)",
				new String[] {
					this._id+""
				},
				null,
				null,
				null
				);
	}

	public ArrayList<Invivo> getInvivoHomeworkItems() {
		ArrayList<Invivo> items = new ArrayList<Invivo>();
		Cursor c = getInivoHomeworkItemsCursor();
		while(c.moveToNext()) {
			Invivo r = new Invivo(dbAdapter);
			r.load(c);
			items.add(r);
		}
		c.close();
		return items;
	}

	public void linkInvivoHomeworkRating(Rating r, Invivo invivo) {
		dbAdapter.getDatabase().delete(
				quote(InvivoHomework.TABLE_NAME),
				quote(InvivoHomework.FIELD_INVIVO_ID)+"=? AND "+ quote(InvivoHomework.FIELD_RATING_ID)+"=? AND "+ quote(InvivoHomework.FIELD_SESSION_ID)+"=?",
				new String[] {
					invivo._id+"",
					r._id+"",
					this._id+"",
				}
				);

		InvivoHomework hw = new InvivoHomework(dbAdapter);
		hw.invivo_id = invivo._id;
		hw.rating_id = r._id;
		hw.session_id = this._id;
		//Log.v("link InvivoHomework", "i:"+invivo._id+" r:"+r._id+" s:"+this._id);
		hw.save();
	}

	public boolean isSessionInteracted() {
		UserQAAnswer ua = new UserQAAnswer(dbAdapter);
		Cursor cursor = ua.getResponsesCursor(this._id);
		int responseCount = cursor.getCount();
		cursor.close();

		if(responseCount > 0) {
			return true;
		}

		if(this.getRecordings().size() > 0) {
			return true;
		}

		if(this.getInvivoHomeworkItems().size() > 0) {
			return true;
		}

		return false;
	}

	public boolean isSessionHomeworkInteracted() {
		TimeLog timeLog = new TimeLog(dbAdapter);
		if(timeLog.getDuration(this._id, TimeLog.TAGS.PLAY_SESSION_RECORDING) > 0) {
			return true;
		}
		if(timeLog.getDuration(this._id, TimeLog.TAGS.PLAY_IMAGINAL_EXPOSURE_RECORDING) > 0) {
			return true;
		}
		if(timeLog.getDuration(this._id, TimeLog.TAGS.REVIEW_INVIVO_HOMEWORK) > 0) {
			return true;
		}
		if(timeLog.getDuration(this._id, TimeLog.TAGS.REVIEW_ADDED_INVIVO_ITEMS) > 0) {
			return true;
		}
		if(timeLog.getDuration(this._id, TimeLog.TAGS.PRACTICE_BREATHING_RETRAINER) > 0) {
			return true;
		}

		ArrayList<Invivo> homeworkItems = this.getInvivoHomeworkItems();
		for(int i = 0; i < homeworkItems.size(); ++i) {
			if(this.getInvivoHomeworkRatings(homeworkItems.get(i)._id).size() > 0) {
				return true;
			}
		}

		return false;
	}

	public Session getFinalSession() {
		Cursor c = dbAdapter.getDatabase().query(
				quote(TABLE_NAME),
				new String[] {
					"MAX("+quote(FIELD_ID)+")"
				},
				quote(FIELD_GROUP_ID)+"=? AND "+ quote(FIELD_IS_FINAL)+">0",
				new String[]{
					this.group_id+""
				},
				null,
				null,
				null
				);
		if(c.moveToFirst()) {
			long id = c.getLong(0);
			c.close();
			Session finalSession = new Session(dbAdapter);
			finalSession._id = id;
			finalSession.load();
			return finalSession;
		}
		c.close();
		return null;
	}
}
