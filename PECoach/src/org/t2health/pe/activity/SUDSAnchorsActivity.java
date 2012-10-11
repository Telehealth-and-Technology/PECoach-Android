package org.t2health.pe.activity;

import java.util.ArrayList;

import org.t2health.pe.Accessibility;
import org.t2health.pe.R;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SUDSAnchorsActivity extends ABSSessionNavigationActivity {
	
	private TextView anchor0;
	private TextView anchor25;
	private TextView anchor50;
	private TextView anchor75;
	private TextView anchor100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.suds_anchors);

		//this.setRightButtonText(getString(R.string.save));
		this.setRightButtonVisibility(View.GONE);
		this.setToolboxButtonVisibility(View.GONE);
		
		anchor0 = (TextView) this.findViewById(R.id.anchor0);
		anchor0.setText(SharedPref.Anchors.Get0(sharedPref));

		anchor25 = (TextView) this.findViewById(R.id.anchor25);
		anchor25.setText(SharedPref.Anchors.Get25(sharedPref));

		anchor50 = (TextView) this.findViewById(R.id.anchor50);
		anchor50.setText(SharedPref.Anchors.Get50(sharedPref));

		anchor75 = (TextView) this.findViewById(R.id.anchor75);
		anchor75.setText(SharedPref.Anchors.Get75(sharedPref));

		anchor100 = (TextView) this.findViewById(R.id.anchor100);
		anchor100.setText(SharedPref.Anchors.Get100(sharedPref));

	}
	
}
