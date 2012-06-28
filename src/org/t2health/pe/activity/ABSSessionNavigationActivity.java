package org.t2health.pe.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import org.t2health.pe.Accessibility;
import org.t2health.pe.ActivityFactory;
import org.t2health.pe.ElapsedTimer;
import org.t2health.pe.R;
import org.t2health.pe.RecordService;
import org.t2health.pe.tables.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public abstract class ABSSessionNavigationActivity extends ABSNavigationActivity {
	private static final String TAG = ABSSessionNavigationActivity.class.getSimpleName();
	public static final String EXTRA_SESSION_ID = "sessionId";
	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_USE_DURATION = "useDuration";

	private static String[] toolboxItemsArr;
	private static String[] toolboxValuesArr;
	protected ElapsedTimer elapsedTimer = new ElapsedTimer();

	protected Session session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		session = new Session(dbAdapter);
		session._id = getIntent().getLongExtra(EXTRA_SESSION_ID, 0);
		if(session._id <= 0 || !session.load()) {
			this.finish();
			return;
		}

		onEvent(this.getClass().getSimpleName(), "SessingIndex", session.index+"");
	}

	@Override
	public void startActivity(Intent intent) {
		if(intent.getLongExtra(EXTRA_SESSION_ID, 0) <= 0) {
			intent.putExtra(EXTRA_SESSION_ID, session._id);
		}
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if(intent.getLongExtra(EXTRA_SESSION_ID, 0) <= 0) {
			intent.putExtra(EXTRA_SESSION_ID, session._id);
		}
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode) {
		if(intent.getLongExtra(EXTRA_SESSION_ID, 0) <= 0) {
			intent.putExtra(EXTRA_SESSION_ID, session._id);
		}
		super.startActivityFromChild(child, intent, requestCode);
	}

	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode) {
		if(intent.getLongExtra(EXTRA_SESSION_ID, 0) <= 0) {
			intent.putExtra(EXTRA_SESSION_ID, session._id);
		}
		return super.startActivityIfNeeded(intent, requestCode);
	}

	protected boolean showStillRecordingToast() {
		if(RecordService.getService() != null && RecordService.getService().isRecording() && !RecordService.callFlag) {
			Accessibility.show(Toast.makeText(
					this, 
					R.string.session_still_recording, 
					Toast.LENGTH_LONG
					));
			return true;
		}
		return false;
	}

	@Override
	public void finish() {
		//this.showStillRecordingToast();
		super.finish();
	}

	protected void onBackButtonPressed() {
		elapsedTimer.stop();

		Intent intent = new Intent();
		intent.putExtra(EXTRA_USE_DURATION, elapsedTimer.getElapsedTime());

		this.setResult(Activity.RESULT_CANCELED, intent);
		this.enabledSlideBackAnimation();
		this.finish();
	}

	protected void onToolboxButtonPressed() {
		// Load the arrays
		if(toolboxItemsArr == null || toolboxValuesArr == null) {
			Resources res = this.getResources();
			toolboxItemsArr = res.getStringArray(R.array.toolbox_items);
			toolboxValuesArr = res.getStringArray(R.array.toolbox_values);
		}

		// Load the items.
		final LinkedHashMap<String, String> toolboxItems = new LinkedHashMap<String,String>();
		for(int i = 0; i < toolboxItemsArr.length; ++i) {
			toolboxItems.put(toolboxValuesArr[i], toolboxItemsArr[i]);
		}

		// Remove the context specific items.
		if(session.index < 1) {
			toolboxItems.remove("vivo_hier");
		}
		if(session.index < 3 || session.is_final) {
			toolboxItems.remove("final_session");
		}

		// Put the items into a list.
		final ArrayList<String> itemsList = new ArrayList<String>();
		for(String key: toolboxItems.keySet()) {
			itemsList.add(toolboxItems.get(key));
		}

		// Show the dialog.
		Accessibility.show(new AlertDialog.Builder(this)
		.setTitle(R.string.toolbox)
		.setNegativeButton(R.string.cancel, null)
		.setItems(itemsList.toArray(
				new String[itemsList.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Set<String> keys = toolboxItems.keySet();
				String[] keysArr = keys.toArray(new String[keys.size()]);
				onItemToolboxItemSelected(keysArr[which]);
			}
		}
				)
				.create()
				);
	}

	private void onItemToolboxItemSelected(String val) {
		if(val.equals("vivo_hier")) {
			Intent i = new Intent(this, AddEditInVivoAcitivty.class);
			i.putExtra(AddEditInVivoAcitivty.EXTRA_TITLE, getString(R.string.vivo_hier));
			startActivityForResult(i, 0);

		} else if(val.equals("exp_pet")) {
			startActivityForResult(ActivityFactory.getExplinationOfPETherapy(this, this.session), 0);

		} else if(val.equals("reactions")) {
			startActivityForResult(ActivityFactory.getCommonReactionsToTrauma(this, this.session), 0);

		} else if(val.equals("breath_retrain")) {
			startActivityForResult(
					new Intent(this, org.t2health.pe.activity.BreathingMenuActivity.class),
					0
					);

		} else if(val.equals("final_session")) {
			Session finalSession = session.getFinalSession();
			Log.v(TAG, "finalSession:"+finalSession);

			// if a final session does not exist yet.
			if(finalSession == null || finalSession._id <= 0) {
				// if this session has data, create a new session.
				if(session.isSessionInteracted() || session.isSessionHomeworkInteracted()) {
					finalSession = session.createFinalSession();

					// if this session doesn't have data, use this session.
				} else {
					finalSession = session;
					finalSession.is_final = true;
					finalSession.save();
				}
			}

			startActivity(ActivityFactory.getSessionActivity(this, finalSession));
			this.finish();
			return;

		} else if(val.equals("contact_info")) {
			startActivityForResult(
					new Intent(this, ContactManagerActivity.class),
					0
					);

		} else if(val.equals("about")) {
			startActivityForResult(
					ActivityFactory.getAboutActivity(this),
					0
					);

		} else if(val.equals("settings")) {
			startActivityForResult(
					new Intent(this, org.t2health.pe.activity.MainPreferenceActivity.class),
					0 
					);
		} else if(val.equals("guide")) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(getString(R.string.clinicians_guide_url) ), "text/html");
			startActivity(intent);
		} else if(val.equals("eula")) {
			EulaActivity.startEULAActivity(this);
		} 
		else if(val.equals("pcl_assessment")) {
			Intent i = new Intent(this, PCLAssessmentsActivity.class);
			startActivityForResult(i, 0);
		}
		else if(val.equals("suds_anchors")) {
			Intent i = new Intent(this, SUDSAnchorsActivity.class);
			startActivityForResult(i, 0);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		elapsedTimer.stop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		elapsedTimer.start();
	}
}
