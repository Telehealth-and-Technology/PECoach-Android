package org.t2health.pe.activity;

import org.t2health.pe.ActivityFactory;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Session;
import org.t2health.pe.tables.SessionGroup;

import android.content.Intent;
import android.os.Bundle;

public class GroupListActivity extends ABSCustomTitleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SessionGroup group = new SessionGroup(dbAdapter);
		group._id = 1;
		if(!group.load()) {
			group._id = 0;
			group.save();
		}
		
		startSessionGroup(1);
	}
	
	private void startSessionGroup(long groupId) {
		SessionGroup group = new SessionGroup(dbAdapter);
		group._id = groupId;
		group.load();

		String activityStr = SharedPref.getLastActivity(sharedPref);

		long lastSessionId = SharedPref.getLastSessionId(sharedPref);
		Session session = new Session(dbAdapter);
		session._id = lastSessionId;

		// If the session doesn't exist, create it.
		if(!session.load()) {
			session._id = 0;
			session.index = 0;
			session.group_id = group._id;
			session.save();
		}

		Intent i;
		if(activityStr.equals(SharedPref.HOMEWORK_ACTIVITY)) {
			i = ActivityFactory.getHomeworkActivity(this, session);
		} else {
			i = ActivityFactory.getSessionActivity(this, session);
		}
		this.startActivity(i);
		this.finish();
	}
}
