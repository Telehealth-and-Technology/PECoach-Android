package org.t2health.pe.tables;

import java.util.ArrayList;

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.Table;

import android.content.ContentValues;
import android.database.Cursor;

public class Recording extends Table {
	public static final String TABLE_NAME = "recording";
	public static final String FIELD_SESSION_ID = "session_id";

	public long session_id;
	
	public Recording(DBAdapter d) {
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
		return true;
	}

	@Override
	public long insert() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SESSION_ID), this.session_id);
		return this.dbAdapter.getDatabase().insert(quote(getTableName()), null, cv);
	}

	@Override
	public boolean update() {
		ContentValues cv = new ContentValues();
		cv.put(quote(FIELD_SESSION_ID), this.session_id);
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
				quote(FIELD_SESSION_ID) +" INTEGER" +
			")");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}
	
	public int getClipsCount() {
		Cursor c = getClipsCursor();
		int cnt = c.getCount();
		c.close();
		return cnt;
	}

	public Cursor getClipsCursor() {
		return dbAdapter.getDatabase().query(
				quote(RecordingClip.TABLE_NAME),
				null,
				quote(RecordingClip.FIELD_RECORDING_ID) +"=?",
				new String[] {
						this._id+""
				},
				null,
				null,
				null
		);
	}

	public ArrayList<RecordingClip> getClips() {
		ArrayList<RecordingClip> recordings = new ArrayList<RecordingClip>();
		Cursor c = getClipsCursor();
		while(c.moveToNext()) {
			RecordingClip r = new RecordingClip(dbAdapter);
			r.load(c);
			recordings.add(r);
		}
		c.close();
		return recordings;
	}
	
	public int getMarkersCount(String type) {
		Cursor c = getMarkersCursor(type);
		int cnt = c.getCount();
		c.close();
		return cnt;
	}
	
	public Cursor getMarkersCursor(String type) {
		String whereSt = "";
		ArrayList<String> whereValues = new ArrayList<String>();
		
		whereSt += quote(RecordingMarker.FIELD_RECORDING_ID) +"=?";
		whereValues.add(this._id+"");
		
		if(type != null) {
			whereSt += " AND "+ quote(RecordingMarker.FIELD_TYPE) +"=?";
			whereValues.add(type);
		}
		
		return dbAdapter.getDatabase().query(
				quote(RecordingMarker.TABLE_NAME),
				null,
				whereSt,
				whereValues.toArray(new String[whereValues.size()]),
				null,
				null,
				null
		);
	}
	
	public ArrayList<RecordingMarker> getMarkers(String type) {
		ArrayList<RecordingMarker> markers = new ArrayList<RecordingMarker>();
		Cursor c = getMarkersCursor(type);
		while(c.moveToNext()) {
			RecordingMarker rm = new RecordingMarker(dbAdapter);
			rm.load(c);
			markers.add(rm);
		}
		c.close();
		return markers;
	}
	
	public long getDuration() {
		long duration = 0;
		ArrayList<RecordingClip> clips = getClips();
		for(int i = 0; i < clips.size(); ++i) {
			duration += clips.get(i).duration;
		}
		return duration;
	}
	
	public long getMarkersMinStartTime(String type) {
		long time = -1;
		ArrayList<RecordingMarker> markers = getMarkers(type);
		for(int i = 0; i < markers.size(); ++i) {
			long tmpTime = markers.get(i).time_start;
			if(tmpTime < time || time == -1) {
				time = tmpTime;
			}
		}
		
		return time;
	}
	
	public long getMarkersMaxEndTime(String type) {
		long time = -1;
		ArrayList<RecordingMarker> markers = getMarkers(type);
		for(int i = 0; i < markers.size(); ++i) {
			long tmpTime = markers.get(i).time_end;
			if(tmpTime > time || time == -1) {
				time = tmpTime;
			}
		}
		
		return time;
	}
	
	public void linkRating(Rating r, long startTime, long endTime) {
		RecordingRating rRating = new RecordingRating(dbAdapter);
		Cursor c = dbAdapter.getDatabase().query(
				quote(RecordingRating.TABLE_NAME), 
				new String[] {
					quote(RecordingRating.FIELD_ID),
				}, 
				quote(RecordingRating.FIELD_RATING_ID)+"=? AND "+ quote(RecordingRating.FIELD_RECORDING_ID)+"=?", 
				new String[] {
					r._id+"",
					this._id+"",
				}, 
				null, 
				null, 
				null
		);
		
		if(c.moveToFirst()) {
			rRating._id = c.getLong(c.getColumnIndex(RecordingRating.FIELD_ID));
		}
		c.close();
		
		rRating.rating_id = r._id;
		rRating.recording_id = this._id;
		rRating.startTime = startTime;
		rRating.endTime = endTime;
		rRating.save();
	}
	
	public Cursor getRatingsCursor() {
		return dbAdapter.getDatabase().query(
				quote(Rating.TABLE_NAME), 
				null, 
				quote(Rating.FIELD_ID)+" IN(SELECT "+ quote(RecordingRating.FIELD_RATING_ID) +" FROM "+ quote(RecordingRating.TABLE_NAME) +" WHERE "+ quote(RecordingRating.FIELD_RECORDING_ID)+"=?)",
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
