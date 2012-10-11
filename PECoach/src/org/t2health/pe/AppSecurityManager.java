package org.t2health.pe;

import org.t2health.pe.activity.UnlockActivity;

import android.app.Activity;
import android.content.Intent;

public class AppSecurityManager {
	private static final int UNLOCK_ACTIVITY = 9834;

	private static AppSecurityManager secMan;

	//Steveo: commented out the auto-locking because the process didnt work on all phones.
	//Only requests pin on first run, and again only after exiting the app via back button.
	
	public boolean unlocked = false;
	private int statusCount = 0;
	private boolean hasFocus = false;

	public static AppSecurityManager getInstance() {
		if(secMan == null) {
			secMan = new AppSecurityManager();
		}
		return secMan;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == UNLOCK_ACTIVITY && resultCode == Activity.RESULT_OK) {
			unlocked = true;
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		this.hasFocus = hasFocus;

		if(hasFocus) {
			++statusCount;
		} else {
			--statusCount;
		}

//		Log.d(TAG, "focus "+ statusCount +","+ hasFocus);

		// If the app was completley shut off, force the unlock screen.
//		if(statusCount == 0) {
//			unlocked = false;
//		}
	}

	public void onResume(Activity activity, boolean isEnabled) {
//		Log.d(TAG, "resume "+ statusCount +","+ hasFocus);

		// if the app is not unlocked, then load the unlock activity.
		if(!unlocked) {
			startUnlockActivity(activity, isEnabled);
		}

		++statusCount;
	}

	public void onPause(Activity activity, boolean isEnabled) {
//		Log.d(TAG, "pause "+ statusCount +","+hasFocus);

		// An app has come over the top of this app. Lock the app.
//		if(statusCount == 1 && !hasFocus) {
//			unlocked = false;
//		}

		--statusCount;
	}

	public void setUnlocked(boolean b) {
		unlocked = b;
	}

	private void startUnlockActivity(Activity activity, boolean isEnabled) {
		if(isEnabled) {
			Intent i = new Intent(activity, UnlockActivity.class);
			activity.startActivityForResult(i, UNLOCK_ACTIVITY);
		}
	}
}
