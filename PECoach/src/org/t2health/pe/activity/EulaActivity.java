package org.t2health.pe.activity;

import org.t2health.pe.R;
import org.t2health.pe.SharedPref;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

public class EulaActivity extends ABSWebViewActivity {
	public static final int EULA_ACTIVITY = 232230354;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setBackButtonVisibity(View.VISIBLE);
		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonVisibility(View.VISIBLE);
		
		this.setRightButtonText(getString(R.string.eula_accept));
		this.setTitle(getString(R.string.eula_title));
		this.setContent(getString(R.string.eula_content));
	}

	@Override
	protected void onRightButtonPressed() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		SharedPref.setIsEulaAccepted(sharedPref, true);
		
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	public static void startEULAActivity(Activity activity) {
		activity.startActivityForResult(
				new Intent(activity, EulaActivity.class), 
				EULA_ACTIVITY
		);
	}
}
