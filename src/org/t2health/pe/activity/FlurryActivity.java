package org.t2health.pe.activity;

import java.util.HashMap;
import java.util.Map;
import org.t2health.pe.Constant;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class FlurryActivity extends Activity {

	private static final String TAG = FlurryActivity.class.getName();

	private static boolean enabled = true;
	private static boolean debugModeEnabled = false;
	private static boolean sessionStarted = false;

	public static void setEnabled(boolean en) {
		enabled = en;

		if(debugModeEnabled) {
			Log.v(TAG, "Analytics setEnabled:"+en);
		}
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setDebugEnabled(boolean b) {
		debugModeEnabled = b;
	}

	public static boolean analyticsEnabled() {
		return enabled;
	}
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Constant.FLURRY_KEY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
    
    public static void onEvent(String event, String key, String value) {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put(key, value);
		onEvent(event, params);
	}

	public static void onEvent(String event, Bundle parameters) {
		HashMap<String,String> params = new HashMap<String,String>();
		for(String key: parameters.keySet()) {
			Object val = parameters.get(key);
			params.put(key, val+"");
		}

		onEvent(event, params);
	}

	public static void onEvent(String event) {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onEvent:"+event);
			}

			FlurryAgent.logEvent(event);
		}
	}

	public static void onEvent(String event, Map<String,String> parameters) {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onEvent:"+event);
			}
			FlurryAgent.logEvent(event, parameters);
		}
	}

	public static void onError(String arg0, String arg1, String arg2)
	{
		if(analyticsEnabled()) {
			FlurryAgent.onError(arg0, arg1, arg2);
		}
	}

	public static void onPageView() {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onPageView");
			}
			FlurryAgent.onPageView();
		}
	}
}
