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

import java.util.HashMap;
import java.util.Map;
import org.t2health.pe.Constant;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class FlurryActivity extends Activity {

	private static final String TAG = FlurryActivity.class.getName();

	private static boolean enabled = true;
	private static boolean debugModeEnabled = false;
	private static boolean sessionStarted = false;

	public static void setEnabled(boolean en) {
		enabled = en;

		if(debugModeEnabled) {
			Log.v(TAG, "Analytics setEnabled:"+en);
		}
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setDebugEnabled(boolean b) {
		debugModeEnabled = b;
	}

	public static boolean analyticsEnabled() {
		return enabled;
	}
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Constant.FLURRY_KEY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
    
    public static void onEvent(String event, String key, String value) {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put(key, value);
		onEvent(event, params);
	}

	public static void onEvent(String event, Bundle parameters) {
		HashMap<String,String> params = new HashMap<String,String>();
		for(String key: parameters.keySet()) {
			Object val = parameters.get(key);
			params.put(key, val+"");
		}

		onEvent(event, params);
	}

	public static void onEvent(String event) {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onEvent:"+event);
			}

			FlurryAgent.logEvent(event);
		}
	}

	public static void onEvent(String event, Map<String,String> parameters) {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onEvent:"+event);
			}
			FlurryAgent.logEvent(event, parameters);
		}
	}

	public static void onError(String arg0, String arg1, String arg2)
	{
		if(analyticsEnabled()) {
			FlurryAgent.onError(arg0, arg1, arg2);
		}
	}

	public static void onPageView() {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onPageView");
			}
			FlurryAgent.onPageView();
		}
	}
}
