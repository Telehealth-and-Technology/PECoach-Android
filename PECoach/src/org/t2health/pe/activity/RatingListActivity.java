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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.t2health.pe.Constant;
import org.t2health.pe.R;
import org.t2health.pe.tables.Rating;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;

public class RatingListActivity extends ABSListLayoutAcitivty {
	public static final String EXTRA_RATING_IDS = "ratingIds";
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(Constant.DATE_FORMAT);
	private SimpleDateFormat timeFormatter = new SimpleDateFormat(Constant.TIME_FORMAT);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		long[] ratingIds = intent.getLongArrayExtra(EXTRA_RATING_IDS);
		if(ratingIds == null) {
			this.finish();
			return;
		}
		
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		for(int i = 0; i < ratingIds.length; ++i) {
			Rating rating = new Rating(dbAdapter);
			rating._id = ratingIds[i];
			rating.load();
			
			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put("_id", rating._id);
			item.put("dateString", dateFormatter.format(new Date(rating.timestamp)));
			item.put("timeString", timeFormatter.format(new Date(rating.timestamp)));
			item.put("preValue", rating.preValue<0?"":rating.preValue+"");
			item.put("postValue", rating.postValue<0?"":rating.postValue+"");
			item.put("peakValue", rating.peakValue<0?"":rating.peakValue+"");
			items.add(item);
		}
		
		//this.setContentView(R.layout.rating_list_activity);
		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonVisibility(View.GONE);
		listView.addHeaderView(
				getLayoutInflater().inflate(R.layout.rating_list_activity_list_header, null)
		);
		listView.setAdapter(new SimpleAdapter(
				this, 
				items, 
				R.layout.rating_list_activity_list_item, 
				new String[]{
						"dateString",
						"timeString",
						"preValue",
						"postValue",
						"peakValue",
				}, 
				new int[] {
						R.id.date,
						R.id.time,
						R.id.pre,
						R.id.post,
						R.id.peak,
				}
		));
	}

}
