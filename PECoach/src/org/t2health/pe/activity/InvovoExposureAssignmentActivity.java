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
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class InvovoExposureAssignmentActivity extends ABSSessionNavigationActivity {
	public static final int SELECTOR_ACTIVITY = 92837;
	public static final int RATE_ACTIVITY = 5890;
	private Invivo selectedInvivo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("invivoexposureassignmentactivity", "create");
		startSelectorActivity();
	}

	private void startSelectorActivity() {
		Intent intent = new Intent(this, InvivoExposureSelectorActivity.class);
		intent.putExtra(InvivoExposureSelectorActivity.EXTRA_TITLE, getString(R.string.vivo_exp_assignment));
		this.startActivityForResult(
				intent, 
				SELECTOR_ACTIVITY
				);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case SELECTOR_ACTIVITY:
			if(resultCode == RESULT_OK) {
				onSelectorActivityFinished(requestCode, resultCode, data);
			} else {
				this.finish();
			}
			break;

		case RATE_ACTIVITY:
				if(resultCode == RESULT_OK && data != null) {
					onRateActivityFinished(requestCode, resultCode, data);
				}
			startSelectorActivity();
			break;
		}
	}

	public void onSelectorActivityFinished(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && data != null) {
			long selectedInvivoId = data.getLongExtra(InvivoExposureSelectorActivity.EXTRA_SELECTED_ID, 0);
			selectedInvivo = new Invivo(dbAdapter);
			selectedInvivo._id = selectedInvivoId;
			selectedInvivo.load();

			ArrayList<Rating> ratings = session.getInvivoHomeworkRatings(selectedInvivoId);
			long ratingId = 0;
			if(ratings.size() > 0) {
				Rating rating = ratings.get(ratings.size()-1);
				if(rating.preValue < 0 || rating.postValue < 0 || rating.peakValue < 0) {
					ratingId = rating._id;
				}
			}

			Intent intent = new Intent(this, AddEditRatingActivity.class);
			intent.putExtra(AddEditRatingActivity.EXTRA_TITLE, selectedInvivo.title);
			intent.putExtra(AddEditRatingActivity.EXTRA_PRE_ENABLED, true);
			intent.putExtra(AddEditRatingActivity.EXTRA_PEAK_ENABLED, true);
			intent.putExtra(AddEditRatingActivity.EXTRA_POST_ENABLED, true);
			intent.putExtra(AddEditRatingActivity.EXTRA_RATING_ID, ratingId);
			this.startActivityForResult(intent, RATE_ACTIVITY);
		}
	}

	public void onRateActivityFinished(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && data != null) {
			long ratingId = data.getLongExtra(AddEditRatingActivity.EXTRA_RATING_ID, 0);
			Rating rating = new Rating(dbAdapter);
			rating._id = ratingId;
			rating.load();

			if(rating.preValue >= 0 && rating.postValue >= 0 && rating.peakValue >= 0) 
			{
				session.linkInvivoHomeworkRating(rating, selectedInvivo);
			}
			else
			{
				//alert that data was not saved.
				Accessibility.show(Toast.makeText(this, "Incomplete data was not saved.", Toast.LENGTH_SHORT));
			}
		}
	}
}
