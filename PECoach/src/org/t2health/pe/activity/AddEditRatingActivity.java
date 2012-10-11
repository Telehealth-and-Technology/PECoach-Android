package org.t2health.pe.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.t2health.pe.Accessibility;
import org.t2health.pe.Constant;
import org.t2health.pe.InputFilterMinMax;
import org.t2health.pe.R;
import org.t2health.pe.tables.Rating;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class AddEditRatingActivity extends ABSSessionNavigationActivity implements OnEditorActionListener {
	public static final String EXTRA_RATING_ID = "ratingId";

	public static final String EXTRA_PRE_ENABLED = "preEnabled";
	public static final String EXTRA_POST_ENABLED = "postEnabled";
	public static final String EXTRA_PEAK_ENABLED = "peakEnabled";

	public static final String EXTRA_PRE_VISIBLE = "preVisible";
	public static final String EXTRA_POST_VISIBLE = "postVisible";
	public static final String EXTRA_PEAK_VISIBLE = "peakVisible";
	
	public static final String EXTRA_PRE_LABEL = "preLabel";
	public static final String EXTRA_POST_LABEL = "postLabel";
	public static final String EXTRA_PEAK_LABEL = "peakLabel";
	
	
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";

	private EditText preEditText;
	private EditText postEditText;
	private EditText peakEditText;

	private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(Constant.SHORT_DATE_TIME_FORMAT);

	private Rating rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();

		rating = new Rating(dbAdapter);
		rating._id = intent.getLongExtra(EXTRA_RATING_ID, 0);
		if(!rating.load()) {
			rating.timestamp = System.currentTimeMillis();
		}

		this.setContentView(R.layout.add_edit_rating_activity);

		String nextText = intent.getStringExtra(EXTRA_RIGHT_BUTTON_TEXT);
		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonText(nextText!=null?nextText: getString(R.string.save));

		// set the field values
		preEditText = (EditText)this.findViewById(R.id.pre);
		preEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
		preEditText.setText(rating.preValue>=0?rating.preValue+"":"");
		if(rating.preTimestamp > 0) {
			((TextView)this.findViewById(R.id.preTime)).setText(
					dateTimeFormatter.format(new Date(rating.preTimestamp))
			);
		}

		postEditText = (EditText)this.findViewById(R.id.post);
		postEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
		postEditText.setText(rating.postValue>=0?rating.postValue+"":"");
		if(rating.postTimestamp > 0) {
			((TextView)this.findViewById(R.id.postTime)).setText(
					dateTimeFormatter.format(new Date(rating.postTimestamp))
			);
		}

		peakEditText = (EditText)this.findViewById(R.id.peak);
		peakEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
		peakEditText.setText(rating.peakValue>=0? rating.peakValue+"":"");
		if(rating.peakTimestamp > 0) {
			((TextView)this.findViewById(R.id.peakTime)).setText(
					dateTimeFormatter.format(new Date(rating.peakTimestamp))
			);
		}
		
		// set the field labels and edittext's contentDescriptions
		String tmpStr = null;
		if((tmpStr = intent.getStringExtra(EXTRA_PRE_LABEL)) != null) {
			((TextView)this.findViewById(R.id.preLabel)).setText(tmpStr);
			preEditText.setContentDescription(tmpStr);
		}
		if((tmpStr = intent.getStringExtra(EXTRA_POST_LABEL)) != null) {
			((TextView)this.findViewById(R.id.postLabel)).setText(tmpStr);
			postEditText.setContentDescription(tmpStr);
		}
		if((tmpStr = intent.getStringExtra(EXTRA_PEAK_LABEL)) != null) {
			((TextView)this.findViewById(R.id.peakLabel)).setText(tmpStr);
			peakEditText.setContentDescription(tmpStr);
		}

		// set the visible fields.
		if(!intent.getBooleanExtra(EXTRA_PRE_VISIBLE, true)) {
			this.findViewById(R.id.preRow).setVisibility(View.GONE);
		}
		if(!intent.getBooleanExtra(EXTRA_POST_VISIBLE, true)) {
			this.findViewById(R.id.postRow).setVisibility(View.GONE);
		}
		if(!intent.getBooleanExtra(EXTRA_PEAK_VISIBLE, true)) {
			this.findViewById(R.id.peakRow).setVisibility(View.GONE);
		}
		
		// set the enabled fields.
		if(intent.getBooleanExtra(EXTRA_PRE_ENABLED, false) && preEditText.getVisibility() == View.VISIBLE) {
			preEditText.setEnabled(true);
			preEditText.setFocusable(true);
		} else {
			preEditText.setFocusable(false);
		}
		if(intent.getBooleanExtra(EXTRA_POST_ENABLED, false) && postEditText.getVisibility() == View.VISIBLE) {
			postEditText.setEnabled(true);
			postEditText.setFocusable(true);
		} else {
			postEditText.setFocusable(false);
		}
		if(intent.getBooleanExtra(EXTRA_PEAK_ENABLED, false) && peakEditText.getVisibility() == View.VISIBLE) {
			peakEditText.setEnabled(true);
			peakEditText.setFocusable(true);
		} else {
			peakEditText.setFocusable(false);
		}

		// set the ime action done for the last visible and enabled field.
		if(peakEditText.isEnabled() && preEditText.getVisibility() == View.VISIBLE) {
			peakEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		} else if(postEditText.isEnabled() && postEditText.getVisibility() == View.VISIBLE) {
			peakEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		} else if(preEditText.isEnabled() && peakEditText.getVisibility() == View.VISIBLE) {
			preEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		}

		// focus on the first enabled and visible field.
		if(preEditText.isEnabled() && preEditText.getVisibility() == View.VISIBLE) {
			preEditText.requestFocus();
		} else if(postEditText.isEnabled() && postEditText.getVisibility() == View.VISIBLE) {
			postEditText.requestFocus();
		} else if(peakEditText.isEnabled() && peakEditText.getVisibility() == View.VISIBLE) {
			peakEditText.requestFocus();
		}
		
		preEditText.setOnEditorActionListener(this);
		postEditText.setOnEditorActionListener(this);
		peakEditText.setOnEditorActionListener(this);
	}

	@Override
	protected void onRightButtonPressed() {
		String preValueStr = preEditText.getText().toString();
		String postValueStr = postEditText.getText().toString();
		String peakValueStr = peakEditText.getText().toString();
		int preValue = intValue(preValueStr);
		int postValue = intValue(postValueStr);
		int peakValue = intValue(peakValueStr);

		if(preValueStr.length() > 0 && (preValue < 0 || preValue > 100)) {
			Accessibility.show(Toast.makeText(this, R.string.rating_range_error, Toast.LENGTH_LONG));
			return;
		}
		if(postValueStr.length() > 0 && (postValue < 0 || postValue > 100)) {
			Accessibility.show(Toast.makeText(this, R.string.rating_range_error, Toast.LENGTH_LONG));
			return;
		}
		if(peakValueStr.length() > 0 && (peakValue < 0 || peakValue > 100)) {
			Accessibility.show(Toast.makeText(this, R.string.rating_range_error, Toast.LENGTH_LONG));
			return;
		}

		rating.preValue = preValue;
		rating.postValue = postValue;
		rating.peakValue = peakValue;

		if(rating.preValue >=0 && rating.preTimestamp == -1) {
			rating.preValue = preValue;
			rating.preTimestamp = System.currentTimeMillis();
		}

		if(rating.postValue >=0 && rating.postTimestamp == -1) {
			rating.postValue = postValue;
			rating.postTimestamp = System.currentTimeMillis();
		}

		if(rating.peakValue >=0 && rating.peakTimestamp == -1) {
			rating.peakValue = peakValue;
			rating.peakTimestamp = System.currentTimeMillis();
		}

		rating.save();

		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_RATING_ID, rating._id);
		this.setResult(RESULT_OK, resultIntent);
		this.finish();
	}

	private int intValue(String st) {
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_DONE) {
			onRightButtonPressed();
			return true;
		}
		return false;
	}
}
