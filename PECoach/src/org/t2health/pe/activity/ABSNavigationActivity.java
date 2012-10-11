package org.t2health.pe.activity;

import java.util.ArrayList;

import org.t2health.pe.ElapsedTimer;
import org.t2health.pe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ABSNavigationActivity extends ABSSecurityActivity {
	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_USE_DURATION = "useDuration";
	
	private navActionListener navActionListener;
	protected ElapsedTimer elapsedTimer = new ElapsedTimer();

	private String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		title = getIntent().getStringExtra(EXTRA_TITLE);

		navActionListener = new navActionListener();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(R.layout.abs_session_navigation_activity);
		((LinearLayout)this.findViewById(R.id.navigationContent)).addView(
				this.getLayoutInflater().inflate(layoutResID, null),
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
		);
		initialize();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(R.layout.abs_session_navigation_activity);
		((LinearLayout)this.findViewById(R.id.navigationContent)).addView(view, params);
		initialize();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(R.layout.abs_session_navigation_activity);
		((LinearLayout)this.findViewById(R.id.navigationContent)).addView(
				view,
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
		);
		initialize();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Make sure the hardware back button is mapped to the navigation back button.
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.onBackButtonPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initialize() {
		this.findViewById(R.id.navigationBackButton).setOnClickListener(navActionListener);
		this.findViewById(R.id.navigationRightButton).setOnClickListener(navActionListener);
		this.findViewById(R.id.navigationExtraButton).setOnClickListener(navActionListener);
		this.findViewById(R.id.navigationToolboxButton).setOnClickListener(navActionListener);

		// get the activity's title and set it.
		try {
			int resId = this.getPackageManager().getActivityInfo(this.getComponentName(), -1).labelRes;
			if(resId != 0) {
				this.setTitle(resId);
			}
		} catch (NameNotFoundException e) {
			// ignore
		}
		
		if(title != null) {
			setTitle(title);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		
		View titleView = this.findViewById(R.id.activityTitle);
		if(titleView != null && TextView.class.isInstance(titleView)) {
			((TextView)titleView).setText(title);
		}
	}

	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		
		View titleView = this.findViewById(R.id.activityTitle);
		if(titleView != null && TextView.class.isInstance(titleView)) {
			((TextView)titleView).setText(getString(titleId));
		}
	}

	protected void onBackButtonPressed() {
		elapsedTimer.stop();

		Intent intent = new Intent();
		intent.putExtra(EXTRA_USE_DURATION, elapsedTimer.getElapsedTime());

		this.setResult(Activity.RESULT_CANCELED, intent);
		this.enabledSlideBackAnimation();
		this.finish();
	}

	protected void enabledSlideBackAnimation() {
		this.overridePendingTransition(
				R.anim.slide_in_left, // slide in from left
				R.anim.slide_out_right // slide out to right
		);
	}

	protected void onRightButtonPressed() {

	}

	protected void onExtraButtonPressed() {

	}

	protected void setBackButtonVisibity(int visibility) {
		this.findViewById(R.id.navigationBackButton).setVisibility(visibility);
		reconfigureFocusDirections();
	}

	protected void setToolboxButtonVisibility(int visibility) {
		this.findViewById(R.id.navigationToolboxButton).setVisibility(visibility);
		reconfigureFocusDirections();
	}

	protected void setRightButtonVisibility(int visibility) {
		this.findViewById(R.id.navigationRightButton).setVisibility(visibility);
		reconfigureFocusDirections();
	}

	protected void setExtraButtonVisibility(int visibility) {
		this.findViewById(R.id.navigationExtraButton).setVisibility(visibility);
		reconfigureFocusDirections();
	}

	protected void setRightButtonText(String text) {
		((TextView)this.findViewById(R.id.navigationRightButton)).setText(text);
	}

	protected void setExtraButtonText(String text) {
		((TextView)this.findViewById(R.id.navigationExtraButton)).setText(text);
	}
	
	protected void setRightButtonContentDescription(String text) {
		((TextView)this.findViewById(R.id.navigationRightButton)).setContentDescription(text);
	}

	protected void setRightButtonEnabled(boolean b) {
		this.findViewById(R.id.navigationRightButton).setEnabled(b);
	}

	protected boolean isRightButtonEnabled() {
		return this.findViewById(R.id.navigationRightButton).isEnabled();
	}

	protected void setRightButtonClickable(boolean b) {
		this.findViewById(R.id.navigationRightButton).setClickable(b);
	}

	protected void setExtraButtonEnabled(boolean b) {
		this.findViewById(R.id.navigationExtraButton).setEnabled(b);
	}

	protected void onToolboxButtonPressed() {

	}
	
	private void reconfigureFocusDirections() {
		ViewGroup nav = (ViewGroup)this.findViewById(R.id.navigationToolbar);
		ArrayList<View> childList = new ArrayList<View>();
		for(int i = 0; i < nav.getChildCount(); ++i) {
			View currentChild = nav.getChildAt(i);
			if(currentChild.getId() != View.NO_ID) {
				currentChild.setNextFocusDownId(View.NO_ID);
				currentChild.setNextFocusUpId(View.NO_ID);
				currentChild.setNextFocusLeftId(View.NO_ID);
				currentChild.setNextFocusRightId(View.NO_ID);
				if(currentChild.getVisibility() == View.VISIBLE) {
					childList.add(currentChild);
				}
			}
		}
		
		int childListSize = childList.size();
		if(childListSize > 0) {
			for(int i = 0; i < childListSize; ++i) {
				View currentChild = childList.get(i);
				if(i-1 >= 0) {
					currentChild.setNextFocusUpId(childList.get(i-1).getId());
				}
				if(i+1 < childListSize) {
					currentChild.setNextFocusDownId(childList.get(i+1).getId());
				}
			}
			childList.get(0).setNextFocusUpId(R.id.navigationToolbar);

			/*Log.v("TESTME", "--------");
			for(int i = 0; i < childListSize; ++i) {
				View currentChild = childList.get(i);
				
				Log.v("TESTME", "ID:"+this.getResources().getResourceEntryName(currentChild.getId()));
				
				int dnId = currentChild.getNextFocusDownId();
				int upId = currentChild.getNextFocusUpId();
				
				this.getResources().getResourceEntryName(currentChild.getId());
				if(upId != View.NO_ID) {
					Log.v("TESTME", "  up:"+this.getResources().getResourceEntryName(upId));
				}
				if(dnId != View.NO_ID) {
					Log.v("TESTME", "  dn:"+this.getResources().getResourceEntryName(dnId));
				}
			}*/
		}
	}

	private class navActionListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.navigationBackButton:
				onBackButtonPressed();
				break;

			case R.id.navigationRightButton:
				onRightButtonPressed();
				break;

			case R.id.navigationToolboxButton:
				onToolboxButtonPressed();
				break;

			case R.id.navigationExtraButton:
				onExtraButtonPressed();
				break;
			}
		}
	}
/*
	@Override
	protected void onPause() {
		super.onPause();
		elapsedTimer.stop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		elapsedTimer.start();
	}*/
}
