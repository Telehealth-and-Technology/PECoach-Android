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
import android.widget.Toast;

public class EditSUDSAnchorsActivity extends ABSSessionNavigationActivity {
	
	private EditText anchor0;
	private EditText anchor25;
	private EditText anchor50;
	private EditText anchor75;
	private EditText anchor100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edit_suds_anchors);

		this.setRightButtonText(getString(R.string.save));
		this.setRightButtonVisibility(View.VISIBLE);
		this.setToolboxButtonVisibility(View.GONE);
		
		anchor0 = (EditText) this.findViewById(R.id.anchor0);
		anchor0.setText(SharedPref.Anchors.Get0(sharedPref));

		anchor25 = (EditText) this.findViewById(R.id.anchor25);
		anchor25.setText(SharedPref.Anchors.Get25(sharedPref));

		anchor50 = (EditText) this.findViewById(R.id.anchor50);
		anchor50.setText(SharedPref.Anchors.Get50(sharedPref));

		anchor75 = (EditText) this.findViewById(R.id.anchor75);
		anchor75.setText(SharedPref.Anchors.Get75(sharedPref));

		anchor100 = (EditText) this.findViewById(R.id.anchor100);
		anchor100.setText(SharedPref.Anchors.Get100(sharedPref));

		//SharedPref.setLastSessionId(sharedPref, session._id);
	}
	
	@Override
	protected void onRightButtonPressed() 
	{
		SharedPref.Anchors.Set0(sharedPref, anchor0.getText().toString());
		SharedPref.Anchors.Set25(sharedPref, anchor25.getText().toString());
		SharedPref.Anchors.Set50(sharedPref, anchor50.getText().toString());
		SharedPref.Anchors.Set75(sharedPref, anchor75.getText().toString());
		SharedPref.Anchors.Set100(sharedPref, anchor100.getText().toString());
		
		Accessibility.show(Toast.makeText(
				this, 
				R.string.anchors_saved, 
				Toast.LENGTH_LONG
		));
		
		this.finish();
	}

}
