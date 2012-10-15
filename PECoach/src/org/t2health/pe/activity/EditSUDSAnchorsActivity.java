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

import java.util.ArrayList;

import org.t2health.pe.Accessibility;
import org.t2health.pe.R;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditSUDSAnchorsActivity extends ABSSessionNavigationActivity {
	
	private EditText anchor0;
	private EditText anchor25;
	private EditText anchor50;
	private EditText anchor75;
	private EditText anchor100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edit_suds_anchors);

		this.setRightButtonText(getString(R.string.save));
		this.setRightButtonVisibility(View.VISIBLE);
		this.setToolboxButtonVisibility(View.GONE);
		
		anchor0 = (EditText) this.findViewById(R.id.anchor0);
		anchor0.setText(SharedPref.Anchors.Get0(sharedPref));

		anchor25 = (EditText) this.findViewById(R.id.anchor25);
		anchor25.setText(SharedPref.Anchors.Get25(sharedPref));

		anchor50 = (EditText) this.findViewById(R.id.anchor50);
		anchor50.setText(SharedPref.Anchors.Get50(sharedPref));

		anchor75 = (EditText) this.findViewById(R.id.anchor75);
		anchor75.setText(SharedPref.Anchors.Get75(sharedPref));

		anchor100 = (EditText) this.findViewById(R.id.anchor100);
		anchor100.setText(SharedPref.Anchors.Get100(sharedPref));

		//SharedPref.setLastSessionId(sharedPref, session._id);
	}
	
	@Override
	protected void onRightButtonPressed() 
	{
		SharedPref.Anchors.Set0(sharedPref, anchor0.getText().toString());
		SharedPref.Anchors.Set25(sharedPref, anchor25.getText().toString());
		SharedPref.Anchors.Set50(sharedPref, anchor50.getText().toString());
		SharedPref.Anchors.Set75(sharedPref, anchor75.getText().toString());
		SharedPref.Anchors.Set100(sharedPref, anchor100.getText().toString());
		
		Accessibility.show(Toast.makeText(
				this, 
				R.string.anchors_saved, 
				Toast.LENGTH_LONG
		));
		
		this.finish();
	}

}
