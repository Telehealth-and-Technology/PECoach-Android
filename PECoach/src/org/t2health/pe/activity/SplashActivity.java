package org.t2health.pe.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.t2health.pe.ActivityFactory;
import org.t2health.pe.R;
import org.t2health.pe.SharedPref;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

public class SplashActivity extends ABSCustomTitleActivity implements OnClickListener/*, OnDismissListener*/ {
	private static final int EULA_ACTIVITY = 1537;
	private static final int GROUP_LIST_ACTIVITY = 8563;
	private static final int ABOUT_ACTIVITY = 2508;
	private Timer timeoutTimer;
	private int startActivity = EULA_ACTIVITY;

	private int accessibilityEnabled = 0;

	private Handler startHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			stopTimer();
			startStartActivity();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.splash_activity);
		this.findViewById(R.id.splashWrapper).setOnClickListener(this);

		if(SharedPref.getIsEulaAccepted(sharedPref)) {
			this.startActivity = GROUP_LIST_ACTIVITY;
		} else {
			this.startActivity = EULA_ACTIVITY;
		}

		//Check accessibility settings and show dialog if enabled.
		try 
		{
			accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			if(accessibilityEnabled == 1)
			{
				new AlertDialog.Builder(this)
				.setMessage("This Android device uses a four direction D-Pad for navigation.")
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						timeoutTimer = new Timer();
						timeoutTimer.schedule(new TimerTask(){
							@Override
							public void run() {
								startHandler.sendEmptyMessage(0);
							}
						}, 2000);
					}
				})
				.create()
				.show();
			}
			else
			{
				timeoutTimer = new Timer();
				timeoutTimer.schedule(new TimerTask(){
					@Override
					public void run() {
						startHandler.sendEmptyMessage(0);
					}
				}, 3000);
			}

		} catch (Exception e) {}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == EulaActivity.EULA_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				this.startAboutActivity();
				return;

			} else {
				this.finish();
				return;
			}

		} else if(requestCode == ABOUT_ACTIVITY) {
			startGroupListActivityAndFinish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startStartActivity() {
		switch(startActivity) {
		case EULA_ACTIVITY:
			startEulaActivity();
			break;

		case GROUP_LIST_ACTIVITY:
			startGroupListActivityAndFinish();
			break;

		case ABOUT_ACTIVITY:
			startAboutActivity();
			break;
		}
	}

	private void startEulaActivity() {
		this.stopTimer();
		EulaActivity.startEULAActivity(this);
	}

	private void startAboutActivity() {
		this.startActivityForResult(ActivityFactory.getAboutActivity(this), ABOUT_ACTIVITY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.stopTimer();
	}

	private void startGroupListActivityAndFinish() {
		this.stopTimer();

		this.startActivity(new Intent(this, GroupListActivity.class));
		this.finish();
		return;
	}

	private void stopTimer() {
		if(timeoutTimer != null) {
			timeoutTimer.cancel();
			timeoutTimer = null;
		}
	}

	@Override
	public void onClick(View v) {
		startStartActivity();
	}
}
