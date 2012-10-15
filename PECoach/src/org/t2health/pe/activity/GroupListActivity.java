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
