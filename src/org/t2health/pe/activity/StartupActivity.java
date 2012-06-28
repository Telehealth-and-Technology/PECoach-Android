package org.t2health.pe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartupActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.startActivity(new Intent(this, SplashActivity.class));
		this.finish();
	}
}
