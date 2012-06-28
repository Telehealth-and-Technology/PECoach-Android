package org.t2health.pe.activity;

import java.util.List;

import org.t2health.pe.Constant;
import org.t2health.pe.DBInstallData;
import org.t2health.pe.SharedPref;
import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.db.DBAdapter.OnDatabaseCreatedListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.nullwire.trace.ExceptionHandler;

public abstract class ABSActivity extends FlurryActivity implements OnDatabaseCreatedListener {

	protected SharedPreferences sharedPref;
	protected DBAdapter dbAdapter;
	@SuppressWarnings("unused")
	private boolean isContentViewSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		dbAdapter = new DBAdapter(this, Constant.DB.NAME, Constant.DB.VERSION);
		dbAdapter.setOnCreateListener(this);
		dbAdapter.open();

		if(!Constant.DEV_MODE) {
			if(SharedPref.getSendAnnonData(sharedPref) &&
					Constant.REMOTE_STACK_TRACE_URL != null &&
					Constant.REMOTE_STACK_TRACE_URL.length() > 0) {
				//ExceptionHandler.register(this, Constant.REMOTE_STACK_TRACE_URL);
			}
		}

		setEnabled(SharedPref.getSendAnnonData(sharedPref));
		onPageView();

	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		this.isContentViewSet = true;
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		this.isContentViewSet = true;
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		this.isContentViewSet = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if(!this.dbAdapter.isOpen()) {
			this.dbAdapter.open();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.dbAdapter.close();
	}

	@Override
	public void onDatabaseCreated() {
		DBInstallData.install(this, dbAdapter);
	}

	protected boolean isCallable(Intent intent) {
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
}
