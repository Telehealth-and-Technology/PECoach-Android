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

import org.t2health.pe.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class CompleteActivity extends ABSCustomTitleActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.complete_activity);
		this.findViewById(R.id.manageApplicationButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.manageApplicationButton:
			manageApplicationButtonPressed();
			break;
		}
	}

	public void manageApplicationButtonPressed() {
		Intent intent = new Intent();
		String packageName = this.getPackageName();
	    int apiLevel = Build.VERSION.SDK_INT;
	    if (apiLevel >= 9) {
	        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
	        Uri uri = Uri.fromParts("package", packageName, null);
	        intent.setData(uri);

	    } else if(apiLevel == 8) {
	    	intent.setAction(Intent.ACTION_VIEW);
	    	intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
	        intent.putExtra("pkg", packageName);

	    } else {
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
	        intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
	    }

	    this.startActivity(intent);
	}

}
