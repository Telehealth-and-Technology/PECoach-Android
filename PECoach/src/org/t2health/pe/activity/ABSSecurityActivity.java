package org.t2health.pe.activity;

import org.t2health.pe.AppSecurityManager;
import org.t2health.pe.SharedPref;

import android.content.Intent;

public abstract class ABSSecurityActivity extends ABSCustomTitleActivity {
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		AppSecurityManager.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		AppSecurityManager.getInstance().onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppSecurityManager.getInstance().onResume(this, SharedPref.Security.isEnabled(sharedPref));
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppSecurityManager.getInstance().onPause(this, SharedPref.Security.isEnabled(sharedPref));
	}
}
