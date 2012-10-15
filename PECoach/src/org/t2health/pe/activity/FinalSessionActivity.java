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

import org.t2health.pe.ActivityFactory;
import org.t2health.pe.Constant;
import org.t2health.pe.R;
import org.t2health.pe.RecordService;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Recording;
import org.t2health.pe.tables.Session;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FinalSessionActivity extends ABSSessionNavigationActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPref.setLastSessionId(sharedPref, session._id);
		SharedPref.setLastActivity(sharedPref, SharedPref.SESSION_ACTIVITY);

		this.setContentView(R.layout.final_session_activity);
		this.setRightButtonText(getString(R.string.complete));

		this.findViewById(R.id.pcl_final).setOnClickListener(this);
		this.findViewById(R.id.rerate_suds).setOnClickListener(this);
		this.findViewById(R.id.compareSuds).setOnClickListener(this);
		this.findViewById(R.id.pcl_report).setOnClickListener(this);
		this.findViewById(R.id.sessionrecord).setOnClickListener(this);

		this.findViewById(R.id.listen_session).setOnClickListener(this);
		this.findViewById(R.id.listen_imaginal_exp).setOnClickListener(this);

		TextView reviewTextView = (TextView)this.findViewById(R.id.review_homework);
		reviewTextView.setOnClickListener(this);
		
		reviewTextView.setText(
				reviewTextView.getText().toString().replace(
						 "{0}",
						 (session.index)+""
				 )
		 );

		showHideRecordingsBlock();
	}

	private void showHideRecordingsBlock() {
		ArrayList<Recording> recordings = session.getRecordings();
		if(recordings.size() > 0) {
			this.findViewById(R.id.playRecordingsBlock).setVisibility(View.VISIBLE);

			if(recordings.get(0).getMarkersCount(Constant.MARKER_TYPE_IMAGINAL_EXPOSURE) > 0) {
				this.findViewById(R.id.listen_imaginal_exp).setEnabled(true);
			} else {
				this.findViewById(R.id.listen_imaginal_exp).setEnabled(false);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		showHideRecordingsBlock();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.review_homework:
			startActivityForResult(new Intent(this, ReviewHomeworkActivity.class), 0);
			break;
			
		case R.id.pcl_final:
			startActivityForResult(new Intent(this, PCLAssessmentsActivity.class), 0);
			break;
			
		case R.id.pcl_report:
			startActivityForResult(new Intent(this, PCLReportsActivity.class), 0);
			break;
			
		case R.id.rerate_suds:
			intent = new Intent(this, ReRateInvivo.class);
			intent.putExtra(RateInvivo.EXTRA_TITLE, getString(R.string.rerate_suds));
			startActivityForResult(intent, 0);
			break;

		case R.id.compareSuds:
			startActivityForResult(new Intent(this, CompareInvivoRatingsActivity.class), 0);
			break;

		case R.id.sessionrecord:
			//Only allow if not already recording another session
			boolean canRecord = false;
			if(RecordService.getService() != null)
				if(RecordService.getService().isRecording())
					if(Constant.recordingSession.equals(""+session.index+session.section))
						canRecord = true;
					else
						canRecord = false;
				else
					canRecord = true;
			else
				canRecord = true;
			
			if(canRecord)
				startActivityForResult(new Intent(this, RecordSessionActivity.class), 0);
			else
			{
				new AlertDialog.Builder(this)
				.setTitle("Alert!")
				.setMessage("You must stop the current recording before starting a new one.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})

				.create()
				.show();
			}

			break;

		case R.id.listen_session:
			startActivityForResult(
				ActivityFactory.getPlayRecording(
						this,
						session.getRecordings().get(0),
						getString(R.string.listen_session)
				),
				0
			);
			break;

		case R.id.listen_imaginal_exp:

			break;
		}
	}

	@Override
	protected void onRightButtonPressed() {
		startActivity(new Intent(this, CompleteActivity.class));
	}

	@Override
	protected void onBackButtonPressed() {
		Session prevSession = session.getPreviousSession(SharedPref.getSplitSessionTwo(sharedPref));
		if(prevSession == null) {
			super.onBackButtonPressed();
			return;
		}

		Intent i = new Intent(this, HomeworkActivity.class);
		i.putExtra(EXTRA_SESSION_ID, prevSession._id);
		this.startActivity(i);
		super.onBackButtonPressed();
	}


}
