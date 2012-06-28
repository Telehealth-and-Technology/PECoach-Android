package org.t2health.pe.tables;

import java.util.ArrayList;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class Invivo extends Table {
	public static final String TABLE_NAME = "invivo";
	public static final String FIELD_SESSION_GROUP_ID = "session_group_id";
	public static final String FIELD_TITLE = "title";
	
	public long session_group_id;
	public String title;

	public Invivo(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public boolean load(Cursor c) {
		super.load(c);
		this.session_group_id = c.getLong(c.getColumnIndex(FIELD_SESSION_GROUP_ID));
		this.title = c.getString(c.getColumnIndex(FIELD_TITLE));
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SESSION_GROUP_ID), this.session_group_id);
		cv.put(quote(FIELD_TITLE), this.title);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SESSION_GROUP_ID), this.session_group_id);
		cv.put(quote(FIELD_TITLE), this.title);
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
				quote(FIELD_SESSION_GROUP_ID) +" INTEGER," +
				quote(FIELD_TITLE) +" TEXT" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}
	
	public void linkRating(Rating r) {
		dbAdapter.getDatabase().delete(
				quote(InvivoRating.TABLE_NAME), 
				quote(InvivoRating.FIELD_RATING_ID)+"=? AND "+ quote(InvivoRating.FIELD_INVIVO_ID)+"=?", 
				new String[] {
					r._id+"",
					this._id+"",
				}
		);
		
		InvivoRating iRating = new InvivoRating(dbAdapter);
		iRating.rating_id = r._id;
		iRating.invivo_id = this._id;
		iRating.save();
	}
	
	public Cursor getRatingsCursor() {
		return dbAdapter.getDatabase().query(
				quote(Rating.TABLE_NAME), 
				null, 
				quote(Rating.FIELD_ID)+" IN(SELECT "+ quote(InvivoRating.FIELD_RATING_ID) +" FROM "+ quote(InvivoRating.TABLE_NAME) +" WHERE "+ quote(InvivoRating.FIELD_INVIVO_ID)+"=?)", 
				new String[] {
					this._id+""
				}, 
				null, 
				null, 
				null
		);
	}
	
	public ArrayList<Rating> getRatings() {
		ArrayList<Rating> items = new ArrayList<Rating>();
		Cursor c = getRatingsCursor();
		while(c.moveToNext()) {
			Rating r = new Rating(dbAdapter);
			r.load(c);
			items.add(r);
		}
		c.close();
		return items;
	}
}
