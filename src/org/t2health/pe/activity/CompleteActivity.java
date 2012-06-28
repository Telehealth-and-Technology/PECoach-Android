package org.t2health.pe.activity;

import org.t2health.pe.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class CompleteActivity extends ABSCustomTitleActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.complete_activity);
		this.findViewById(R.id.manageApplicationButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.manageApplicationButton:
			manageApplicationButtonPressed();
			break;
		}
	}

	public void manageApplicationButtonPressed() {
		Intent intent = new Intent();
		String packageName = this.getPackageName();
	    int apiLevel = Build.VERSION.SDK_INT;
	    if (apiLevel >= 9) {
	        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
	        Uri uri = Uri.fromParts("package", packageName, null);
	        intent.setData(uri);

	    } else if(apiLevel == 8) {
	    	intent.setAction(Intent.ACTION_VIEW);
	    	intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
	        intent.putExtra("pkg", packageName);

	    } else {
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
	        intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
	    }

	    this.startActivity(intent);
	}

}
