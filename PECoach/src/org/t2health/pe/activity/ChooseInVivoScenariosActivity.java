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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.t2health.pe.R;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.InvivoHomework;
import org.t2health.pe.tables.Rating;
import org.t2health.pe.tables.Session;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleAdapter;

public class ChooseInVivoScenariosActivity extends ABSListLayoutAcitivty implements OnItemClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = ChooseInVivoScenariosActivity.class.getSimpleName();
	private HashMap<Long,Boolean> checkedIds = new HashMap<Long,Boolean>();
	private ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();

	private static final String KEY_INVIVO_ID = "_id";
	private static final String KEY_INVIVO_TITLE = "title";
	private static final String KEY_RATING_ID = "ratingId";
	private static final String KEY_RATING_VALUE = "preValue";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("chooseinvivoscenarioactivity", "create");
		this.setTitle(R.string.choose_homework_scenarios);
		this.setDescription(getString(R.string.choose_scenarios));
		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonText(getString(R.string.save));

		items = getItems();
		checkedIds = getCheckedIds();

		// By default, check the previous session's items too.
		if(session.getInvivoHomeworkItems().size() == 0) {
			Session prevSession = session.getPreviousSession(SharedPref.getSplitSessionTwo(sharedPref));
			if(prevSession != null) {
				ArrayList<Invivo> homeworkItems = prevSession.getInvivoHomeworkItems();
				for(int i = 0; i < homeworkItems.size(); ++i) {
					checkedIds.put(homeworkItems.get(i)._id, true);
				}
			}
		}

		this.setAdapter(new SimpleAdapter(
				this, 
				items, 
				R.layout.list_item_2_checked_inline, 
				new String[] {
						KEY_INVIVO_ID,
						KEY_INVIVO_TITLE,
						KEY_RATING_VALUE,
				},
				new int[] {
						R.id.checkbox,
						R.id.text1,
						R.id.text2,
				}
				));
		((SimpleAdapter)adapter).setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {

				view.setFocusable(false);
				int viewId = view.getId();

				if(viewId == R.id.checkbox) {
					final long itemId = (Long)data;
					boolean checked = checkedIds.get(itemId)==null?false:checkedIds.get(itemId);

					CheckBox cb = (CheckBox)view;
					cb.setOnCheckedChangeListener(null);
					cb.setChecked(checked);
					cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							itemChecked(itemId, isChecked);
						}
					});
					return true;
				}
				return false;
			}
		});
		this.setAdapter(adapter);

		this.listView.setOnItemClickListener(this);

	}

	private HashMap<Long,Boolean> getCheckedIds() {
		HashMap<Long,Boolean> checkedIds = new HashMap<Long,Boolean>();
		ArrayList<Invivo> homeworkItems = session.getInvivoHomeworkItems();

		ArrayList<Invivo> invivoItems = session.getSessionGroup().getInVivos();
		for(int i = 0; i < invivoItems.size(); ++i) {
			checkedIds.put(invivoItems.get(i)._id, false);
		}

		for(int i = 0; i < homeworkItems.size(); ++i) {
			checkedIds.put(homeworkItems.get(i)._id, true);
		}
		return checkedIds;
	}

	private ArrayList<HashMap<String,Object>> getItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();

		// build the list of items.
		ArrayList<Invivo> invivoItems = session.getSessionGroup().getInVivos();
		
		//Sort by rating
		Collections.sort(invivoItems, new Comparator<Invivo>(){

			public int compare(Invivo o1, Invivo o2) {
				int p1 = o1.getRatings().get(0).preValue;
				int p2 = o2.getRatings().get(0).preValue;
				return (p1>p2 ? -1 : (p1==p2 ? 0 : 1));
			}

		});

		for(int i = 0; i < invivoItems.size(); ++i) {
			Invivo inv = invivoItems.get(i);
			ArrayList<Rating> ratings = inv.getRatings();

			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put(KEY_INVIVO_ID, inv._id);
			item.put(KEY_INVIVO_TITLE, inv.title);
			item.put(KEY_RATING_VALUE, "");

			if(ratings.size() > 0) {
				Rating rating = ratings.get(0);
				item.put(KEY_RATING_VALUE, rating.preValue);
				item.put(KEY_RATING_ID, rating._id);
			}

			items.add(item);
		}

		return items;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2 > 0) {
			HashMap<String,Object> item = (HashMap<String, Object>) adapter.getItem(arg2-1);
			long itemid = (Long) item.get(KEY_INVIVO_ID);
			String itemtitle = (String) item.get(KEY_INVIVO_TITLE);
			itemChecked(itemid, !checkedIds.get(itemid));
			accFeedback(itemtitle, !checkedIds.get(itemid));
			arg0.setSelection(arg2);

		}
	}

	public void accFeedback(String title, boolean isChecked)
	{
		// build the accessibility event.
		AccessibilityManager aManager = (AccessibilityManager) this.getSystemService(Context.ACCESSIBILITY_SERVICE);
		if(aManager.isEnabled()) {
			AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_FOCUSED);
			//event.setPackageName(Toast.class.getPackage().toString());
			//event.setClassName(Toast.class.getSimpleName());

			if(isChecked)
				event.setContentDescription("Not Checked " + title);
			else
				event.setContentDescription("Checked " + title);
			event.setEventTime(System.currentTimeMillis());

			// send the event.
			aManager.sendAccessibilityEvent(event);
		}
	}

	private void itemChecked(long id, boolean isChecked) {
		checkedIds.put(id, isChecked);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	protected void onRightButtonPressed() {
		session.deleteInvivoHomework();
		for(Long invivoId: checkedIds.keySet()) {
			if(checkedIds.get(invivoId)) {
				InvivoHomework ih = new InvivoHomework(dbAdapter);
				ih.session_id = session._id;
				ih.invivo_id = invivoId;
				ih.save();
			}
		}
		this.finish();
	}
}
