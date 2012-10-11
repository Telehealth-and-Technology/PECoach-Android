package org.t2health.pe.activity;

import org.t2health.pe.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class MainPreferenceActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
	private static final String EVENT_ANON_DATA_ENABLED = "anonDataEnabled";
	private static final String EVENT_ANON_DATA_DISABLED = "anonDataDisabled";

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_preference_activity);

        PreferenceScreen screen = this.getPreferenceScreen();
        screen.findPreference("security").setOnPreferenceClickListener(this);
        screen.findPreference("send_anon_data").setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		String prefKey = preference.getKey();

		if(prefKey.equals("security")) {
			Intent i = new Intent(this, SecurityActivity.class);
			this.startActivity(i);
			return true;
		}

		return false;
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals("send_anon_data")) {
			Boolean isChecked = (Boolean)newValue;
			if(isChecked) {
				FlurryActivity.setEnabled(true);
				FlurryActivity.onEvent(EVENT_ANON_DATA_ENABLED);
			} else {
				FlurryActivity.onEvent(EVENT_ANON_DATA_DISABLED);
				FlurryActivity.setEnabled(false);
			}
		}

		return true;
	}
}
