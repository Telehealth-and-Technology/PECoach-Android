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
package org.t2health.pe;

import org.t2health.pe.activity.AddCalendarEventActivity;
import org.t2health.pe.activity.CommonReactionsToTraumaActivity;
import org.t2health.pe.activity.ExplinationOfPETheoryActivity;
import org.t2health.pe.activity.FinalSessionActivity;
import org.t2health.pe.activity.HomeworkActivity;
import org.t2health.pe.activity.PlayRecordingActivity;
import org.t2health.pe.activity.PlaySessionRecordingActivity;
import org.t2health.pe.activity.SessionActivity;
import org.t2health.pe.activity.TextVideoActivity;
import org.t2health.pe.activity.WebViewActivity;
import org.t2health.pe.tables.Recording;
import org.t2health.pe.tables.Session;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ActivityFactory {
	public static Intent getAboutActivity(Context c) {
		Intent intent = new Intent(c, WebViewActivity.class);
		intent.putExtra(WebViewActivity.EXTRA_TITLE_ID, R.string.about_title);
		intent.putExtra(WebViewActivity.EXTRA_CONTENT_ID, R.string.about_content);
		return intent;
	}

	//Pulls up OS calendar to add events
//	public static Intent getAddCalendarEvent(Context c, long startTime, long endTime, String title) {
//		Intent intent = new Intent(Intent.ACTION_EDIT);
//		intent.setType("vnd.android.cursor.item/event");
//		intent.putExtra("title", title);
//		intent.putExtra("beginTime", startTime);
//		intent.putExtra("endTime", endTime);
//		intent.putExtra("allDay", false);
//		return intent;
//	}
	
	//An in-app, accessible calendar event activity
	public static Intent getAddCalendarEvent(Context c, long startTime, long endTime, String title) {
		Intent intent = new Intent(c, AddCalendarEventActivity.class);
		//intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("title", title);
		intent.putExtra("beginTime", startTime);
		intent.putExtra("endTime", endTime);
		intent.putExtra("allDay", false);
		return intent;
	}
	
	public static Intent getSessionActivity(Context c, Session s) {
		Intent intent;
		if(s.is_final) {
			intent = new Intent(c, FinalSessionActivity.class);
			intent.putExtra(FinalSessionActivity.EXTRA_SESSION_ID, s._id);
		} else {
			intent = new Intent(c, SessionActivity.class);
			intent.putExtra(SessionActivity.EXTRA_SESSION_ID, s._id);
		}
		
		return intent;
	}

	public static Intent getHomeworkActivity(Context c, Session s) {
		if(s.is_final) {
			return getSessionActivity(c, s);
		}

		Intent intent = new Intent(c, HomeworkActivity.class);
		intent.putExtra(HomeworkActivity.EXTRA_SESSION_ID, s._id);
		return intent;
	}

	public static Intent getPlayRecording(Context c, Recording recording, String title) {
		Intent i = new Intent(c, PlaySessionRecordingActivity.class);
		i.putExtra(
				PlayRecordingActivity.EXTRA_TITLE,
				title
		);
		i.putExtra(
				PlayRecordingActivity.EXTRA_RECORDING_ID,
				recording._id
		);
		return i;
	}
	
	public static Intent getCommonReactionsToTrauma(Context c, Session s) {
		Intent i = new Intent(c, CommonReactionsToTraumaActivity.class);
		i.putExtra(CommonReactionsToTraumaActivity.EXTRA_TITLE, c.getString(R.string.common_reactions_to_trauma_activity_title));
		i.putExtra(CommonReactionsToTraumaActivity.EXTRA_CONTENT_ID, CommonReactionsToTraumaActivity.ID_COMMON_REACTIONS_TO_TRAUMA);
		return i;
	}
	
	public static Intent getExplinationOfPETherapy(Context c, Session s) {
		Log.v("TESTME", "Activity Title:"+c.getString(R.string.explination_of_pe_theory_activity_title));
		Intent i = new Intent(c, ExplinationOfPETheoryActivity.class);
		i.putExtra(ExplinationOfPETheoryActivity.EXTRA_TITLE, c.getString(R.string.explination_of_pe_theory_activity_title));
		i.putExtra(ExplinationOfPETheoryActivity.EXTRA_CONTENT_ID, ExplinationOfPETheoryActivity.ID_EXPLINATION_OF_PE_THEORY);
		return i;
	}
	
	public static Intent getBreatingLearn(Context c, Session s) {
		Intent i = new Intent(c, TextVideoActivity.class);
		i.putExtra(TextVideoActivity.EXTRA_TITLE, c.getString(R.string.breath_retrainer_learn));
		i.putExtra(TextVideoActivity.EXTRA_CONTENT_ID, TextVideoActivity.ID_BREATHING_RETRAINER_LEARN);
		return i;
	}
	
	public static Intent getBreathingWatchDemo(Context c, Session s) {
		Intent i = new Intent(c, TextVideoActivity.class);
		i.putExtra(TextVideoActivity.EXTRA_TITLE, c.getString(R.string.breath_retrainer_watch));
		i.putExtra(TextVideoActivity.EXTRA_CONTENT_ID, TextVideoActivity.ID_BREATHING_RETRAINER_WATCH_DEMO);
		return i;
	}
	
}
