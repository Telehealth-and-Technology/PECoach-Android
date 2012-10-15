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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import org.t2health.pe.R;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;
import org.t2health.pe.tables.UserQAAnswer;

import zencharts.charts.DateChart;
import zencharts.charts.LineChart;
import zencharts.data.DatePoint;
import zencharts.data.DateSeries;
import zencharts.data.LinePoint;
import zencharts.data.LineSeries;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CompareInvivoRatingsActivity extends ABSSessionNavigationActivity {
	private static final String KEY_TITLE = "title";
	private static final String KEY_POST_RATING = "postRating";
	private static final String KEY_PRE_RATING = "preRating";

	//Graphing vars
	public LineChart lineChart;
	public boolean chartMode = false;
	private ListView lvReport;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set this content view.
		this.setContentView(R.layout.suds_report_activity);
		lvReport = ((ListView)this.findViewById(R.id.list));
		
		lineChart = (LineChart)this.findViewById(R.id.linechart);
		lineChart.loadFont("Elronmonospace.ttf", 16, 2, 2);
//        dateChart.showGrid = true;
//        dateChart.scrollGrid = false;
//        dateChart.showStars = false;
//        dateChart.screenPoints = 4;
        //dateChart.setMinMaxYValue(0, 100);
		lineChart.setVisibility(View.GONE);
        
        this.setRightButtonText("Chart");
		this.setRightButtonVisibility(View.VISIBLE);
		this.setToolboxButtonVisibility(View.GONE);

		Random color = new Random();

		//Color randomColor = new Color(color.nextInt(256),color.nextInt(256),color.nextInt(256));
		
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		ArrayList<Invivo> invivos = session.getSessionGroup().getInVivos();
		for(int i = 0; i < invivos.size(); ++i) {
			Invivo invivo = invivos.get(i);
			
			LineSeries sudsSeries = new LineSeries(this, R.drawable.quadstar);
	        //sudsSeries.dashEffect = new float[] {i,20};
	        sudsSeries.lineColor = Color.BLUE;
	        sudsSeries.lineWidth = 5;
	        sudsSeries.xLabels = false;
			LinePoint tmpPointa = new LinePoint(0, "", "");
			LinePoint tmpPointb = new LinePoint(0, "", "");
			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put(KEY_TITLE, invivo.title);
			
			ArrayList<Rating> ratings = invivo.getRatings();
			if(ratings.size() > 0) {
				Rating rating = ratings.get(0);
				item.put(KEY_PRE_RATING, rating.preValue<0?"":rating.preValue+"");
				item.put(KEY_POST_RATING, rating.postValue<0?"":rating.postValue+"");
				
				tmpPointa.label = invivo.title;
				tmpPointa.value = rating.preValue;
				tmpPointb.label = invivo.title;
				tmpPointb.value = rating.postValue;
			}
			items.add(item);
			sudsSeries.add(tmpPointa);
			sudsSeries.add(tmpPointb);
			lineChart.addSeries(sudsSeries);
		}

		ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.list_item_compare_rating, null);
		((TextView)headerView.findViewById(R.id.text2)).setText("Second");
		((TextView)headerView.findViewById(R.id.text3)).setText("Final");
		lvReport.addHeaderView(headerView);

		lvReport.setAdapter(new SimpleAdapter(
				this,
				items,
				R.layout.list_item_compare_rating,
				new String[] {
						KEY_TITLE,
						KEY_PRE_RATING,
						KEY_POST_RATING,
				},
				new int[] {
						R.id.text1,
						R.id.text2,
						R.id.text3,
				}
				));
	}
	
	@Override
	protected void onRightButtonPressed() {
		if(chartMode)
		{
			chartMode = false;
			lineChart.setVisibility(View.GONE);
			this.setRightButtonText("Chart");
		}
		else
		{
			chartMode = true;
			lineChart.setVisibility(View.VISIBLE);			
			this.setRightButtonText("List");
		}
	}

}
