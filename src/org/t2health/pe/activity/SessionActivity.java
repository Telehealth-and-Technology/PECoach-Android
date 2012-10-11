/**
 *
 */
package org.t2health.pe.activity;

import java.util.ArrayList;

import org.t2health.pe.Accessibility;
import org.t2health.pe.ActivityFactory;
import org.t2health.pe.AppSecurityManager;
import org.t2health.pe.Constant;
import org.t2health.pe.R;
import org.t2health.pe.RecordService;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Session;

import com.flurry.android.FlurryAgent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * @author uthara.parthasarathy
 *
 */
public class SessionActivity extends ABSSessionItemListActivity {
	public static final String EXTRA_START_RECORD_ACTIVITY = "startRecordActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPref.setLastSessionId(sharedPref, session._id);
		SharedPref.setLastActivity(sharedPref, SharedPref.SESSION_ACTIVITY);

		if(this.getIntent().getBooleanExtra(EXTRA_START_RECORD_ACTIVITY, false)) {
			this.startActivity(ACTIONS.record_session);
		}

		this.setRightButtonText(getString(R.string.goto_session_homework));
		this.setRightButtonContentDescription(getString(R.string.goto_session_homework));

		//if this is session 2
		if(session.index == 1)
		{
			//If not already notified
			if(!SharedPref.getSplitSessionNotify(sharedPref))
			{
				SharedPref.setSplitSessionNotify(sharedPref, true);
				new AlertDialog.Builder(this)
				.setTitle("Attention")
				.setMessage("Ask your clinician if you are completing all of Session 2 or splitting Session 2 into two parts. \r\n\r\nIf you need two parts, press the menu button on your device and select 'Split Session'.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})

				.create()
				.show();
			}
		}
	}

	@Override
	protected ArrayList<SessionAction> getSessionActions() {
		ArrayList<SessionAction> sessionActions = new ArrayList<SessionAction>();
		Log.v("Session", ""+session.index);
		if(session.index == 0) {
			
			sessionActions.add(availableActions.get(ACTIONS.record_session));
			
			sessionActions.add(availableActions.get(ACTIONS.pcl_assessments));

		} else if(session.index == 1) 
		{
			if(SharedPref.getSplitSessionTwo(sharedPref))
			{
				if(session.section == 0)
				{
					sessionActions.add(availableActions.get(ACTIONS.record_session));
					
					sessionActions.add(availableActions.get(ACTIONS.review_homework_from_previous));
				}
				else
				{
					sessionActions.add(availableActions.get(ACTIONS.record_session));
					
					sessionActions.add(availableActions.get(ACTIONS.review_homework_from_previous));
					sessionActions.add(availableActions.get(ACTIONS.suds_anchors));
					sessionActions.add(availableActions.get(ACTIONS.create_invivo_and_suds));
					sessionActions.add(availableActions.get(ACTIONS.choose_homework_scenarios));
				}
			}
			else
			{

				sessionActions.add(availableActions.get(ACTIONS.record_session));
				
				sessionActions.add(availableActions.get(ACTIONS.review_homework_from_previous));
				sessionActions.add(availableActions.get(ACTIONS.suds_anchors));
				sessionActions.add(availableActions.get(ACTIONS.create_invivo_and_suds));
				sessionActions.add(availableActions.get(ACTIONS.choose_homework_scenarios));
			}

		} else if(session.index == 2) {

			sessionActions.add(availableActions.get(ACTIONS.record_session));
			sessionActions.add(availableActions.get(ACTIONS.pcl_assessments));
			sessionActions.add(availableActions.get(ACTIONS.review_homework_from_previous));
			sessionActions.add(availableActions.get(ACTIONS.choose_homework_scenarios));

		} else {
			
			sessionActions.add(availableActions.get(ACTIONS.record_session));
			
			if(session.index % 2 == 0) {
				sessionActions.add(availableActions.get(ACTIONS.pcl_assessments));
			}
			sessionActions.add(availableActions.get(ACTIONS.review_homework_from_previous));
			sessionActions.add(availableActions.get(ACTIONS.choose_homework_scenarios));
		}

		// Add the calendar option if the calendar is enabled.
		SessionAction calendarAction = availableActions.get(ACTIONS.appointments_and_reminders);
		calendarAction.setEnabled(isCallable(ActivityFactory.getAddCalendarEvent(this, 0, 1, "")));
		sessionActions.add(calendarAction);

		sessionActions.add(availableActions.get(ACTIONS.goto_session));


		return sessionActions;
	}

	@SuppressWarnings("unused")
	@Override
	protected void onBackButtonPressed() {
		Session prevSession = session.getPreviousSession(SharedPref.getSplitSessionTwo(sharedPref));
		if(prevSession == null) {
			final Context thisContext = this;
			Accessibility.show(new AlertDialog.Builder(this)
			.setMessage(R.string.confirm_app_exit)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					FlurryAgent.onEndSession(getBaseContext());

					//Set locked when app exited
					AppSecurityManager.getInstance().unlocked = false;
					finish();
				}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			})
			.create()
					);

			return;
		}


		this.startActivity(
				ActivityFactory.getHomeworkActivity(this, prevSession)
				);
		super.onBackButtonPressed();
	}

	@Override
	protected void onRightButtonPressed() {
		
		//Allowed to navigate
		this.showStillRecordingToast();
		//if(!this.showStillRecordingToast()) 
		{
			if(isRightButtonEnabled()) {
				startActivity(
						ActivityFactory.getHomeworkActivity(this, session)
						);
				this.finish();
			} else {
				Accessibility.show(Toast.makeText(
						this, 
						R.string.session_continue_disabled_message, 
						Toast.LENGTH_LONG
						));
			}			
		}
	}



}
