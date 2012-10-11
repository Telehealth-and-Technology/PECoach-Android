package org.t2health.pe.activity;

import org.t2health.pe.R;
import org.t2health.pe.SharedPref;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;


public class UnlockActivity extends ABSCustomTitleActivity implements OnKeyListener, OnClickListener {
	private static final int FORGOT_PIN_ACTIVITY = 235;
	private EditText pinEditText;
	private String lockPin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.unlock_activity);
		pinEditText = (EditText)this.findViewById(R.id.pin);
		pinEditText.setOnKeyListener(this);
		this.lockPin = SharedPref.Security.getPin(sharedPref);

		this.findViewById(R.id.forgotPinButton).setOnClickListener(this);
		
		pinEditText.requestFocus();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == FORGOT_PIN_ACTIVITY && resultCode == RESULT_OK) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.moveTaskToBack(true);
			return true;
		}

		if(keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}

		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(v.getId() == R.id.pin) {
			String enteredPin = pinEditText.getText().toString().trim();
			if(enteredPin.length() > 0 && enteredPin.equals(lockPin)) {
				
				if (getParent() == null) {
				    setResult(Activity.RESULT_OK, this.getIntent());
				}
				else {
				    getParent().setResult(Activity.RESULT_OK, this.getIntent());
				}
				
				//this.setResult(Activity.RESULT_OK);
				this.finish();
			}
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void onClick(View v) {
		Intent i;
		switch(v.getId()){
			case R.id.forgotPinButton:
				i = new Intent(this, ForgotPinActivity.class);
				//i.putExtra(ForgotPin.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, FORGOT_PIN_ACTIVITY);
				break;
		}
	}
}
