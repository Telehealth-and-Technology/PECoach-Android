package org.t2health.pe.activity;

import org.t2health.pe.R;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public abstract class ABSCustomTitleActivity extends ABSActivity {
	private boolean initialized = false;
	private String title;
	
	@Override
	public void setContentView(int layoutResID) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view, params);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view);
		this.initCustomTitle();
	}
	
	@Override
	public void setTitle(CharSequence title) {
		if(initialized) {
			super.setTitle(title);
		} else {
			this.title = title+"";
		}
	}

	@Override
	public void setTitle(int titleId) {
		if(initialized) {
			super.setTitle(titleId);
		} else {
			this.title = getString(titleId);
		}
	}
	
	protected void initCustomTitle() {
		initialized = true;
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		if(this.title != null) {
			super.setTitle(this.title);
		}
	}
	
	protected boolean isCustomTitleInitialized() {
		return initialized;
	}
}
