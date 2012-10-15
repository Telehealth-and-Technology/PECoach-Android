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

import org.t2health.pe.R;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class AddEditInVivoAcitivty extends ABSListLayoutAcitivty implements OnItemClickListener {
	private static final String TAG = AddEditInVivoAcitivty.class.getSimpleName();

	private static final String KEY_INVIVO_ID = "_id";
	private static final String KEY_INVIVO_TITLE = "title";
	private static final String KEY_RATING_ID = "ratingId";
	private static final String KEY_RATING_VALUE = "preValue";

	private ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();

	private SimpleAdapter itemsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("addeditinvivoactivity", "create");
		this.setEmptyText(R.string.add_edit_invivo_empty_list);
		this.setRightButtonText(getString(R.string.add));
		this.setToolboxButtonVisibility(View.GONE);
		this.setExtraButtonVisibility(View.GONE);

		// build the list of items.
		reloadItems();

		Log.v(TAG, "itemsCount:"+ items.size());

		itemsAdapter = new SimpleAdapter(
				this,
				items,
				R.layout.list_item_view_in_vivo_item,
				new String[] {
						KEY_INVIVO_TITLE,
						KEY_RATING_VALUE,
				},
				new int[] {
						R.id.text1,
						R.id.text2,
				}
				);

		listView.setAdapter(itemsAdapter);
		listView.setOnItemClickListener(this);

		if(items.size() == 0) {
			startAddEditActivity(null);
		}
	}

	private void reloadItems() {
		items.clear();

		ArrayList<Invivo> invivoItems = session.getSessionGroup().getInVivos();

		for(int i = 0; i < invivoItems.size(); ++i) {
			Invivo inv = invivoItems.get(i);
			ArrayList<Rating> ratings = inv.getRatings();

			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put(KEY_INVIVO_ID, inv._id);
			item.put(KEY_INVIVO_TITLE, inv.title);

			if(ratings.size() > 0) {
				Rating rating = ratings.get(0);
				item.put(KEY_RATING_VALUE, rating.preValue);
				item.put(KEY_RATING_ID, rating._id);
			}
			items.add(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		reloadItems();
		itemsAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onBackButtonPressed() {
		super.onBackButtonPressed();
	}

	@Override
	protected void onRightButtonPressed() {
		this.startActivityForResult(new Intent(this, AddEditInvivoItemActivity.class), 324098);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HashMap<String,Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
		if(item == null) {
			return;
		}
		long id = (Long)item.get(KEY_INVIVO_ID);

		startAddEditActivity(id);
	}

	private void startAddEditActivity(Long id) {
		Intent intent = new Intent(this, AddEditInvivoItemActivity.class);
		if(id != null) {
			intent.putExtra(AddEditInvivoItemActivity.EXTRA_ID, id);
		}

		this.startActivityForResult(intent, 324098);
	}
}
