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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class ABSListLayoutAcitivty extends ABSSessionNavigationActivity {
	protected ListView listView;
	protected BaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.abs_list_layout_activity);
		
		listView = (ListView)this.findViewById(R.id.list);
		listView.setEmptyView(this.findViewById(R.id.emptyText));
		
		// put the title into the header of the list.
		View v = this.findViewById(R.id.listHeader);
		if(v != null) {
			ViewGroup parent = (ViewGroup)v.getParent();
			parent.removeView(v);
			v.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.FILL_PARENT, 
					ListView.LayoutParams.WRAP_CONTENT
			));
			listView.addHeaderView(v);
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		TextView tv = (TextView)findViewById(R.id.listHeader).findViewById(R.id.activityDesc);
		if(tv.getText().toString().length() == 0) {
			tv.setVisibility(View.GONE);
		}
	}


	protected void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		listView.setAdapter(adapter);
	}
	
	protected void setDescription(String desc) {
		TextView tv = (TextView)this.findViewById(R.id.listHeader).findViewById(R.id.activityDesc);
		tv.setText(desc);
	}
	
	protected void setEmptyText(int res) {
		((TextView)this.findViewById(R.id.emptyText)).setText(res);
	}
}
