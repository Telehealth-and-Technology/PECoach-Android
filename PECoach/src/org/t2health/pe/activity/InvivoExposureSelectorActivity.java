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
import java.util.HashMap;
import java.util.List;

import org.t2health.pe.R;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;
import org.t2health.pe.tables.Session;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;

public class InvivoExposureSelectorActivity extends ABSListLayoutAcitivty implements OnItemClickListener {
	private static final String TAG = InvivoExposureSelectorActivity.class.getSimpleName();
	public static final String EXTRA_SELECTED_ID = "selectedId";

	private ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

	private static final String KEY_INVIVO_ID = "_id";
	private static final String KEY_INVIVO_TITLE = "title";
	private static final String KEY_RATING_ID = "ratingId";
	private static final String KEY_RATING_VALUE = "preValue";

	//Used to select from full heirarchy -Steveo
	private CharSequence[] fullitems;
	private long[] fullids;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("invivoexposureselectoractivity", "create");
		this.setEmptyText(R.string.add_edit_invivo_empty_list);
		this.setDescription(getString(R.string.vivo_exp_homework_text));
		this.setToolboxButtonVisibility(View.GONE);
		//this.setRightButtonVisibility(View.GONE);
		this.setRightButtonText("Full Hierarchy");

		reloadItems();
		if(items.size() == 0) {
			Session prevSession = session.getPreviousSession(SharedPref.getSplitSessionTwo(sharedPref));
			if(prevSession != null) {
				loadItems(prevSession.getInvivoHomeworkItems());
			}
		}

		//Load items from full hierarchy -Steveo
		ArrayList<Invivo> invivoItems = session.getSessionGroup().getInVivos();
		fullitems = new CharSequence[invivoItems.size()];
		fullids = new long[invivoItems.size()];

		for(int i = 0; i < invivoItems.size(); ++i) {
			Invivo inv = invivoItems.get(i);
			fullitems[i] = inv.title;
			fullids[i] = inv._id;
		}
		
		SimpleAdapter adapter = new SimpleAdapter(
				this,
				items,
				R.layout.list_item_2_inline,
				new String[] {
						KEY_RATING_ID,
						KEY_INVIVO_TITLE,
						KEY_RATING_VALUE,
				},
				new int[] {
						R.id.checkbox,
						R.id.text1,
						R.id.text2,
				}
				);
		adapter.setViewBinder(new SimpleAdapter.ViewBinder(){
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				//view.setFocusable(false);
				if(view.getId() == R.id.checkbox) {
					boolean isChecked = false;

					//Steveo:
					//Changed to never show checked status. Don't want the task to appear "completed" as it should be done many times.

					//					if(data == null) {
					//						isChecked = false;
					//					} else {
					//						isChecked = true;
					//					}

					CheckBox cb = (CheckBox)view;
					cb.setClickable(false);
					cb.setChecked(isChecked);
					view.setVisibility(View.GONE);
					
					return true;
				}
				return false;
			}});

		this.setAdapter(adapter);

		listView.setOnItemClickListener(this);
	}

	private void reloadItems() {
		items.clear();

		// build the list of items.
		loadItems(session.getInvivoHomeworkItems());
	}

	private void loadItems(List<Invivo> invivoItems) {
		for(int i = 0; i < invivoItems.size(); ++i) {
			Invivo inv = invivoItems.get(i);
			ArrayList<Rating> ratings = session.getInvivoHomeworkRatings(inv._id);

			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put(KEY_INVIVO_ID, inv._id);
			item.put(KEY_INVIVO_TITLE, inv.title);
			//item.put(KEY_RATING_VALUE, "");

			if(ratings.size() > 0) {
				Log.v(TAG, inv._id+" has Ratings");

				//Nullified this by not wanting this displayed at all, left code just incase this changes

				//Steveo:
				//Get latest rating rather than first
				//Rating rating = ratings.get(ratings.size() - 1);

				//Steveo:
				//Changed to postValue
				//Added some padding in R.layout.list_item_2_checked_inline also...
				//item.put(KEY_RATING_VALUE, rating.postValue);

				//item.put(KEY_RATING_ID, rating._id);
				
			} else {
				Log.v(TAG, inv._id+" no Ratings");
			}

			items.add(item);
		}
	}

	@Override
	protected void onRightButtonPressed()
	{
		

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Full Invivo Hierarchy");
		builder.setItems(fullitems, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		         //Do selected invivo assignment (from full list)
		    	long selectedId = fullids[item];
				doAssignment(selectedId);
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2 > 0) {
			@SuppressWarnings("unchecked")
			HashMap<String,Object> item = (HashMap<String, Object>) adapter.getItem(arg2-1);
			long selectedId = (Long)item.get(KEY_INVIVO_ID);
			doAssignment(selectedId);
		}
	}
	
	private void doAssignment(Long id)
	{
		long selectedId = id;
		Intent intent = new Intent();
		intent.putExtra(EXTRA_SELECTED_ID, selectedId);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
}
