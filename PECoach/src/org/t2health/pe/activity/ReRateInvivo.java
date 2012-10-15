/*
 * 
 * PECoach
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: PECoach001
 * Government Agency Original Software Title: PECoach
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.pe.activity;

import java.util.ArrayList;

import org.t2health.pe.InputFilterMinMax;
import org.t2health.pe.R;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ReRateInvivo extends ABSSessionNavigationActivity {
	private static final String KEY_ID = "_id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_POST_RATING = "postRating";
	private static final String KEY_RATING_ID = "ratingId";
	ArrayList<Invivo> invivos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.rerate_activity);

		this.setRightButtonText(getString(R.string.save));
		this.setRightButtonVisibility(View.VISIBLE);
		this.setToolboxButtonVisibility(View.GONE);

		invivos = session.getSessionGroup().getInVivos();
		//ArrayList<Rating> ratings = inv.getRatings();

		for(int i = 0; i < invivos.size(); i++) {

			ArrayList<Rating> ratings = invivos.get(i).getRatings();
			
			// Find Tablelayout
			TableLayout tl = (TableLayout)findViewById(R.id.dynamicTable);
			tl.setStretchAllColumns(true);
			
			// Create a new row to be added. 
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			
			
			// Create a textview 
			TextView tv = new TextView(this);
			tv.setText(invivos.get(i).title);
			tv.setTag(i);
			tv.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			tv.setLines(1);
			tv.setEms(10);
			// Add textview to row.
			tr.addView(tv);
			
			//Create Edittext
			EditText et = new EditText(this);
			et.setId(210 + i);
			if(ratings.size() > 0) {
				Rating rating = ratings.get(0);
				et.setText(rating.postValue>=0?rating.postValue+"":"");
			
			}
			
			et.setInputType(InputType.TYPE_CLASS_NUMBER);
			et.setTag(i);
			et.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
			et.setLayoutParams(new LayoutParams(
					30,
					LayoutParams.WRAP_CONTENT));
			
			// Add edittext to row.
						tr.addView(et);
			
			// Add row to TableLayout. 
			tl.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

		}
	}

	@Override
	protected void onRightButtonPressed() {

		for(int i = 0; i < invivos.size(); i++)
		{
			ArrayList<Rating> ratings = invivos.get(i).getRatings();
			if(ratings.size() > 0) {
				Rating rating = ratings.get(0);
				EditText tmp = (EditText)findViewById(210+i);
				rating.postTimestamp = System.currentTimeMillis();
				rating.postValue = Integer.parseInt(tmp.getText().toString());
				rating.save();
			}
			
		}

		this.finish();
	}
}
