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
import org.t2health.pe.ReadableElapsedTime;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;
import org.t2health.pe.tables.Recording;
import org.t2health.pe.tables.Session;
import org.t2health.pe.tables.TimeLog;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReviewHomeworkActivity extends ABSListLayoutAcitivty implements OnItemClickListener {
	private static final int INVIVO_SELECTOR_ACTIVITY = 2508;
	private static final int INVIVO_RATING_LIST_ACTIVITY = 24508;

	private ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();

	private Session previousSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		previousSession = session.getPreviousSession(SharedPref.getSplitSessionTwo(sharedPref));

		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonVisibility(View.GONE);
		this.setTitle(
				getString(R.string.review_homework_from_previous_n).replace("{0}", (previousSession.index+1)+"")
		);

		reloadItems();

		SimpleAdapter adapter = new SimpleAdapter(
				this,
				items,
				R.layout.simple_list_item_2_indicator,
				new String[] {
					"title",
					"desc",
					//"showIndicator",
				},
				new int[] {
					R.id.text1,
					R.id.text2,
					//R.id.indicator1,
				}
		);
		adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				int viewId = view.getId();

				if(viewId == R.id.text2) {
					((TextView)view).setText(data.toString());
				}
				/*if(viewId == R.id.text2) {
					long duration = (Long)data;
					TextView tv = (TextView)view;

					tv.setText("");
					if(duration > -1) {
						tv.setText(ReadableElapsedTime.millisToLongDHMS(duration));
					}
					return true;

				} else if(viewId == R.id.indicator1) {
					boolean isVisible = (Boolean)data;
					if(isVisible) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}

					return true;
				}*/

				return false;
			}
		});

		this.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	private void reloadItems() {
		HashMap<String,HashMap<String,Object>> availableItems = new HashMap<String,HashMap<String,Object>>();

		Session prevSession = session.getPreviousSession(SharedPref.getSplitSessionTwo(sharedPref));
		TimeLog timeLog = new TimeLog(dbAdapter);
		HashMap<String,Object> item;

		item = new HashMap<String,Object>();
		item.put("id", TimeLog.TAGS.LISTEN_TO_EXPLINATION_OF_PE);
		item.put("title", getString(R.string.review_pe_therapy));
		item.put("desc", ReadableElapsedTime.millisToLongDHMS(timeLog.getDuration(
				prevSession._id,
				TimeLog.TAGS.LISTEN_TO_EXPLINATION_OF_PE
		)));
		availableItems.put(item.get("id").toString(), item);

		item = new HashMap<String,Object>();
		item.put("id", TimeLog.TAGS.LISTEN_TO_COMMON_REACTIONS);
		item.put("title", getString(R.string.review_common));
		item.put("desc", ReadableElapsedTime.millisToLongDHMS(timeLog.getDuration(
				prevSession._id,
				TimeLog.TAGS.LISTEN_TO_COMMON_REACTIONS
		)));
		availableItems.put(item.get("id").toString(), item);

		item = new HashMap<String,Object>();
		item.put("id", "review_invivo_items");
		item.put("title", getString(R.string.review_invivo));
		item.put("desc", getString(R.string.select_for_more_details));
		availableItems.put(item.get("id").toString(), item);

		item = new HashMap<String,Object>();
		item.put("id", "review_invivo_exposure");
		item.put("title", getString(R.string.review_vivo_homework));
		item.put("desc", getString(R.string.select_for_more_details));
		availableItems.put(item.get("id").toString(), item);

		item = new HashMap<String,Object>();
		item.put("id", TimeLog.TAGS.PRACTICE_BREATHING_RETRAINER);
		item.put("title", getString(R.string.review_breathing));
		item.put("desc", ReadableElapsedTime.millisToLongDHMS(timeLog.getDuration(
				prevSession._id,
				TimeLog.TAGS.PRACTICE_BREATHING_RETRAINER
		)));
		availableItems.put(item.get("id").toString(), item);

		ArrayList<Recording> recordings = previousSession.getRecordings();
		if(recordings.size() > 0) {
			item = new HashMap<String,Object>();
			item.put("id", TimeLog.TAGS.PLAY_SESSION_RECORDING);
			item.put("title", getString(R.string.review_session));
			item.put("desc", ReadableElapsedTime.millisToLongDHMS(timeLog.getDuration(
					prevSession._id,
					TimeLog.TAGS.PLAY_SESSION_RECORDING
			)));
			availableItems.put(item.get("id").toString(), item);

			if(recordings.get(0).getClipsCount() > 0) {
				item = new HashMap<String,Object>();
				item.put("id", TimeLog.TAGS.PLAY_IMAGINAL_EXPOSURE_RECORDING);
				item.put("title", getString(R.string.review_previe));
				item.put("desc", ReadableElapsedTime.millisToLongDHMS(timeLog.getDuration(
						prevSession._id,
						TimeLog.TAGS.PLAY_IMAGINAL_EXPOSURE_RECORDING
				)));
				availableItems.put(item.get("id").toString(), item);
			}
		}

		items.clear();
		if(previousSession.index == 0) {
			addItem(items, availableItems, TimeLog.TAGS.LISTEN_TO_EXPLINATION_OF_PE);
			addItem(items, availableItems, TimeLog.TAGS.PRACTICE_BREATHING_RETRAINER);
			addItem(items, availableItems, TimeLog.TAGS.PLAY_SESSION_RECORDING);

		} else if(previousSession.index == 1) {
			addItem(items, availableItems, TimeLog.TAGS.LISTEN_TO_COMMON_REACTIONS);
			addItem(items, availableItems, "review_invivo_items");
			addItem(items, availableItems, "review_invivo_exposure");
			addItem(items, availableItems, TimeLog.TAGS.PRACTICE_BREATHING_RETRAINER);
			addItem(items, availableItems, TimeLog.TAGS.PLAY_SESSION_RECORDING);

		} else if(previousSession.index > 1) {
			addItem(items, availableItems, "review_invivo_items");
			addItem(items, availableItems, "review_invivo_exposure");
			addItem(items, availableItems, TimeLog.TAGS.PRACTICE_BREATHING_RETRAINER);
			addItem(items, availableItems, TimeLog.TAGS.PLAY_SESSION_RECORDING);
			addItem(items, availableItems, TimeLog.TAGS.PLAY_IMAGINAL_EXPOSURE_RECORDING);
		}
	}

	private static void addItem(ArrayList<HashMap<String,Object>> items, HashMap<String,HashMap<String,Object>> availableItems, String itemKey) {
		HashMap<String,Object> item = availableItems.get(itemKey);
		if(item != null) {
			items.add(item);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HashMap<String,Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
		if(item == null) {
			return;
		}
		
		String id = (String) item.get("id");

		if(id.equals("review_invivo_items")) {
			Intent intent = new Intent(this, AddEditInVivoAcitivty.class);
			intent.putExtra(AddEditInVivoAcitivty.EXTRA_TITLE, getString(R.string.review_invivo));
			this.startActivity(intent);

		} else if(id.equals("review_invivo_exposure")) {
			startInvivoSelctor();

		} else if(id.equals(TimeLog.TAGS.PLAY_IMAGINAL_EXPOSURE_RECORDING)) {
			ArrayList<Recording> recordings = previousSession.getRecordings();
			if(recordings.size() > 0) {
				Cursor c = recordings.get(0).getRatingsCursor();
				long[] ratingIds = new long[c.getCount()];
				while(c.moveToNext()) {
					ratingIds[c.getPosition()] = c.getLong(c.getColumnIndex(Rating.FIELD_ID));
				}

				Intent intent = new Intent(this, RatingListActivity.class);
				intent.putExtra(RatingListActivity.EXTRA_TITLE, getString(R.string.review_previe));
				intent.putExtra(RatingListActivity.EXTRA_RATING_IDS, ratingIds);
				this.startActivity(intent);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == INVIVO_SELECTOR_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				long selectedId = data.getLongExtra(InvivoExposureSelectorActivity.EXTRA_SELECTED_ID, 0);
				Invivo invivo = new Invivo(dbAdapter);
				invivo._id = selectedId;
				invivo.load();

				Cursor c = previousSession.getInvivoHomeworkRatingsCursor(selectedId);
				long[] ratingIds = new long[c.getCount()];
				while(c.moveToNext()) {
					ratingIds[c.getPosition()] = c.getLong(c.getColumnIndex(Rating.FIELD_ID));
				}

				Intent intent = new Intent(this, RatingListActivity.class);
				intent.putExtra(RatingListActivity.EXTRA_TITLE, invivo.title);
				intent.putExtra(RatingListActivity.EXTRA_RATING_IDS, ratingIds);
				this.startActivityForResult(intent, INVIVO_RATING_LIST_ACTIVITY);
			}

		} else if(requestCode == INVIVO_RATING_LIST_ACTIVITY) {
			startInvivoSelctor();
		}
	}

	private void startInvivoSelctor() {
		Intent intent = new Intent(this, InvivoExposureSelectorActivity.class);
		intent.putExtra(InvivoExposureSelectorActivity.EXTRA_TITLE, getString(R.string.review_vivo_homework));
		intent.putExtra(InvivoExposureSelectorActivity.EXTRA_SESSION_ID, previousSession._id);
		this.startActivityForResult(intent, INVIVO_SELECTOR_ACTIVITY);
	}
}
