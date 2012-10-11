package org.t2health.pe.activity;

import java.util.Calendar;

import org.t2health.pe.Accessibility;
import org.t2health.pe.InputFilterMinMax;
import org.t2health.pe.R;

import com.flurry.android.FlurryAgent;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.Toast;

public class AddCalendarEventActivity extends ABSSessionNavigationActivity {

	EditText etActivityDesc;

	EditText etMo;
	EditText etDa;
	EditText etYr;

	EditText etHr;
	EditText etMi;

	boolean savedEvent = false;

	Calendar sc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onEvent("AddCalendarEventActivity.onCreate()");
		this.setContentView(R.layout.add_calendar_event);

		this.setRightButtonText(getString(R.string.save));
		this.setRightButtonVisibility(View.VISIBLE);
		this.setToolboxButtonVisibility(View.GONE);

		sc = Calendar.getInstance();

		etActivityDesc = (EditText) findViewById(R.id.activityDesc);
		etActivityDesc.setContentDescription("Activity Title: " + etActivityDesc.getText().toString());
		etActivityDesc.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				try {
					
					etActivityDesc.setContentDescription("Activity Title: " + etActivityDesc.getText().toString());
				} catch (Exception ex) {}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
		
		etMo = (EditText) findViewById(R.id.etMo);
		etMo.setFilters(new InputFilter[] { new InputFilterMinMax("1", "12") });
		etMo.setText("" + (sc.get(Calendar.MONTH) + 1));
		etMo.setContentDescription("Month " + sc.get(Calendar.MONTH) + 1);
		etMo.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				try {
					AppointmentChanged("Mo",
							Integer.parseInt(etMo.getText().toString()) - 1);
					etMo.setContentDescription("Month " + sc.get(Calendar.MONTH) + 1);
				} catch (Exception ex) {}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		etDa = (EditText) findViewById(R.id.etDa);
		etDa.setFilters(new InputFilter[] { new InputFilterMinMax("1", "31") });
		etDa.setText("" + sc.get(Calendar.DATE));
		etDa.setContentDescription("Day " + sc.get(Calendar.DATE));
		etDa.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				try {
					AppointmentChanged("Da",
							Integer.parseInt(etDa.getText().toString()));
					etDa.setContentDescription("Day " + sc.get(Calendar.DATE));
				} catch (Exception ex) {}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		etYr = (EditText) findViewById(R.id.etYr);
		etYr.setFilters(new InputFilter[] { new InputFilterMinMax("0", "2999") });
		etYr.setText("" + sc.get(Calendar.YEAR));
		etYr.setContentDescription("Year " + sc.get(Calendar.YEAR));
		etYr.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				try {
					AppointmentChanged("Yr",
							Integer.parseInt(etYr.getText().toString()));
					etYr.setContentDescription("Year " + sc.get(Calendar.YEAR));
				} catch (Exception ex) {}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		etHr = (EditText) findViewById(R.id.etHr);
		etHr.setFilters(new InputFilter[] { new InputFilterMinMax("0", "24") });
		etHr.setText("" + sc.get(Calendar.HOUR_OF_DAY));
		etHr.setContentDescription("Hour " + sc.get(Calendar.HOUR_OF_DAY));
		etHr.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				try {
					AppointmentChanged("Hr",
							Integer.parseInt(etHr.getText().toString()));
					etHr.setContentDescription("Hour " + sc.get(Calendar.HOUR_OF_DAY));
				} catch (Exception ex) {}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		etMi = (EditText) findViewById(R.id.etMi);
		etMi.setFilters(new InputFilter[] { new InputFilterMinMax("0", "59") });
		etMi.setText("00");
		etMi.setContentDescription("Minute 00");
		etMi.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				try {
					AppointmentChanged("Mi",
							Integer.parseInt(etMi.getText().toString()));
					etMi.setContentDescription("Minute " + sc.get(Calendar.MINUTE));
				} catch (Exception ex) {}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

	}

	public void AppointmentChanged(String action, int value) {
		try {
			if (action.equals("Mo"))
				sc.set(Calendar.MONTH, value);
			else if (action.equals("Da"))
				sc.set(Calendar.DAY_OF_MONTH, value);
			else if (action.equals("Yr"))
				sc.set(Calendar.YEAR, value);
			else if (action.equals("Hr"))
				sc.set(Calendar.HOUR_OF_DAY, value);
			else if (action.equals("Mi"))
				sc.set(Calendar.MINUTE, value);

		} catch (Exception ex) {
			FlurryAgent.onError("AddCalendarEventActivity", "AppointmentChanged", ex.toString());
		}
	}

	private void addToCalendar(Context ctx, final String title) {
		FlurryAgent.onEvent("AddCalendarEventActivity.addToCalendar()");
		final ContentResolver cr = ctx.getContentResolver();
		Cursor cursor;
		if (Integer.parseInt(Build.VERSION.SDK) >= 8)
			cursor = cr.query(
					Uri.parse("content://com.android.calendar/calendars"),
					new String[] { "_id", "displayname" }, null, null, null);
		else
			cursor = cr.query(Uri.parse("content://calendar/calendars"),
					new String[] { "_id", "displayname" }, null, null, null);
		if (cursor.moveToFirst()) {
			final String[] calNames = new String[cursor.getCount()];
			final int[] calIds = new int[cursor.getCount()];
			for (int i = 0; i < calNames.length; i++) {
				calIds[i] = cursor.getInt(0);
				calNames[i] = cursor.getString(1);
				cursor.moveToNext();
			}

			AccessibilityManager aManager = (AccessibilityManager) this.getSystemService(Context.ACCESSIBILITY_SERVICE);
			if(aManager.isEnabled()) {
				
				//If accessibility is enabled, put event into first calendar result
				ContentValues cv = new ContentValues();
				cv.put("calendar_id", calIds[0]);
				cv.put("title", title);
				cv.put("dtstart", "" + sc.getTimeInMillis());
				cv.put("hasAlarm", 1);

				Uri newEvent;
				if (Integer.parseInt(Build.VERSION.SDK) >= 8)
					newEvent = cr.insert(
							Uri.parse("content://com.android.calendar/events"),
							cv);
				else
					newEvent = cr.insert(
							Uri.parse("content://com.android.calendar/events"),
							cv);

				if (newEvent != null) {
					long id = Long.parseLong(newEvent
							.getLastPathSegment());
					ContentValues values = new ContentValues();
					values.put("event_id", id);
					values.put("method", 1);
					values.put("minutes", 15);
					if (Integer.parseInt(Build.VERSION.SDK) >= 8)
						cr.insert(
								Uri.parse("content://com.android.calendar/reminders"),
								values);
					else
						cr.insert(
								Uri.parse("content://calendar/reminders"),
								values);

					Runnable qRunnable = new Runnable() {
						public void run() {
							QuitActivity();
						}
					};
					savedEvent = true;
					runOnUiThread(qRunnable);

				}


			}
			else
			{
				//Accessibility not enabled, ask user which calendar to use.
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setSingleChoiceItems(calNames, -1,
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContentValues cv = new ContentValues();
						cv.put("calendar_id", calIds[which]);
						cv.put("title", title);
						cv.put("dtstart", "" + sc.getTimeInMillis());
						cv.put("hasAlarm", 1);

						Uri newEvent;
						if (Integer.parseInt(Build.VERSION.SDK) >= 8)
							newEvent = cr.insert(
									Uri.parse("content://com.android.calendar/events"),
									cv);
						else
							newEvent = cr.insert(
									Uri.parse("content://com.android.calendar/events"),
									cv);

						if (newEvent != null) {
							long id = Long.parseLong(newEvent
									.getLastPathSegment());
							ContentValues values = new ContentValues();
							values.put("event_id", id);
							values.put("method", 1);
							values.put("minutes", 15);
							if (Integer.parseInt(Build.VERSION.SDK) >= 8)
								cr.insert(
										Uri.parse("content://com.android.calendar/reminders"),
										values);
							else
								cr.insert(
										Uri.parse("content://calendar/reminders"),
										values);

							Runnable qRunnable = new Runnable() {
								public void run() {
									QuitActivity();
								}
							};
							savedEvent = true;
							runOnUiThread(qRunnable);

						}
						dialog.cancel();
					}

				});

				builder.create().show();
			}
		}
		cursor.close();
	}

	public void QuitActivity() {
		FlurryAgent.onEvent("AddCalendarEventActivity.QuitActivity()");
		if (savedEvent)
			Accessibility.show(Toast.makeText(this, "Appointment Saved...",
					Toast.LENGTH_SHORT));

		this.finish();

	}

	@Override
	protected void onRightButtonPressed() {
		addToCalendar(this, etActivityDesc.getText().toString());

	}

}
