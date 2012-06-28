package org.t2health.pe.activity;

import java.util.ArrayList;

import org.t2health.pe.Accessibility;
import org.t2health.pe.InputFilterMinMax;
import org.t2health.pe.R;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class AddEditInvivoItemActivity extends ABSSessionNavigationActivity implements OnEditorActionListener {
	public static final String EXTRA_ID = "id";
	private Invivo invivo;
	private Rating rating;
	private boolean isNewItem = true;
	private EditText titleEditText;
	private EditText ratingEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("addeditinvivoitemactivity", "create");
		
		invivo = new Invivo(this.dbAdapter);
		rating = new Rating(this.dbAdapter);

		this.setContentView(R.layout.add_edit_invivo_item_activity);

		titleEditText = (EditText)this.findViewById(R.id.text1);
		titleEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

		ratingEditText = (EditText)this.findViewById(R.id.text2);
		ratingEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
		ratingEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

		long id = getIntent().getLongExtra(EXTRA_ID, 0);
		if(id > 0) {
			invivo._id = id;
			if(invivo.load()) {
				isNewItem = false;
				
				//Steveo: Allowed editing/saving of preValue
				//ratingEditText.setEnabled(false);
				titleEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

				ArrayList<Rating> ratings = invivo.getRatings();
				if(ratings.size() > 0) {
					rating = ratings.get(0);
				}
			}
		}

		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonText(getString(R.string.save));

		String ratingValue = (rating.preValue >= 0)?rating.preValue+"":"";


		titleEditText.setText(invivo.title);
		titleEditText.setOnEditorActionListener(this);

		ratingEditText.setText(ratingValue);
		ratingEditText.setOnEditorActionListener(this);
	}


	@Override
	protected void onRightButtonPressed() {

		try
		{
			String ratingStr = ratingEditText.getText().toString();
			if(ratingStr.trim().equals(""))
			{
				Accessibility.show(Toast.makeText(this, "Please enter a SUDS value.", Toast.LENGTH_SHORT));
			}
			else
			{
				int ratingValue = Integer.parseInt(ratingStr);
//
				invivo.title = titleEditText.getText().toString();
				invivo.session_group_id = session.group_id;
				invivo.save();
				
				//Steveo: Allowed editing/saving of preValue
				rating.preValue = ratingValue;
				rating.save();

				if(isNewItem) {
					rating.preValue = ratingValue;
					rating.preTimestamp = System.currentTimeMillis();
					rating.save();
					invivo.linkRating(rating);
				}

				Accessibility.show(Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT));
				this.finish();
			}
		}
		catch(Exception ex)
		{
			Accessibility.show(Toast.makeText(this, "Error saving assignment!", Toast.LENGTH_SHORT));
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
