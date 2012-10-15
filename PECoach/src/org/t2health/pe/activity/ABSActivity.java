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

import java.util.List;

import org.t2health.pe.Constant;
import org.t2health.pe.DBInstallData;
import org.t2health.pe.SharedPref;
import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.DBAdapter.OnDatabaseCreatedListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.nullwire.trace.ExceptionHandler;

public abstract class ABSActivity extends FlurryActivity implements OnDatabaseCreatedListener {

	protected SharedPreferences sharedPref;
	protected DBAdapter dbAdapter;
	@SuppressWarnings("unused")
	private boolean isContentViewSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		dbAdapter = new DBAdapter(this, Constant.DB.NAME, Constant.DB.VERSION);
		dbAdapter.setOnCreateListener(this);
		dbAdapter.open();

		if(!Constant.DEV_MODE) {
			if(SharedPref.getSendAnnonData(sharedPref) &&
					Constant.REMOTE_STACK_TRACE_URL != null &&
					Constant.REMOTE_STACK_TRACE_URL.length() > 0) {
				//ExceptionHandler.register(this, Constant.REMOTE_STACK_TRACE_URL);
			}
		}

		setEnabled(SharedPref.getSendAnnonData(sharedPref));
		onPageView();

	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		this.isContentViewSet = true;
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		this.isContentViewSet = true;
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		this.isContentViewSet = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if(!this.dbAdapter.isOpen()) {
			this.dbAdapter.open();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.dbAdapter.close();
	}

	@Override
	public void onDatabaseCreated() {
		DBInstallData.install(this, dbAdapter);
	}

	protected boolean isCallable(Intent intent) {
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
}
