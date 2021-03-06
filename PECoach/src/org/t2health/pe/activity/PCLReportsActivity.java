/*
 * 
 * PECoach
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
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
import java.util.HashMap;

import org.t2health.pe.R;
import org.t2health.pe.tables.UserQAAnswer;

import zencharts.charts.DateChart;
import zencharts.charts.LineChart;
import zencharts.data.DatePoint;
import zencharts.data.DateSeries;
import zencharts.data.LinePoint;
import zencharts.data.LineSeries;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PCLReportsActivity extends ABSSessionNavigationActivity {

	ArrayList<HashMap<String, String>> reportList = new ArrayList<HashMap<String, String>>(2);
	private ListView lvReport;
	private Cursor sessionCursor;
	private Cursor answerCursor;

	//Graphing vars
	public LineChart lineChart;
	public boolean chartMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Set this content view.
		this.setContentView(R.layout.pcl_report_activity);

		lineChart = (LineChart)this.findViewById(R.id.linechart);
		lineChart.loadFont("Elronmonospace.ttf", 16, 2, 2);
//        dateChart.showGrid = true;
//        dateChart.scrollGrid = false;
//        dateChart.showStars = false;
        //dateChart.setMaxYValue(100);
		lineChart.setVisibility(View.GONE);
        
        LineSeries pclSeries = new LineSeries(this, R.drawable.quadstar);
        pclSeries.dashEffect = new float[] {10,20};
        pclSeries.lineColor = Color.BLUE;
        pclSeries.lineWidth = 5;
		pclSeries.xLabels = false;
		
		lvReport = ((ListView)this.findViewById(R.id.list));
		
		this.setRightButtonText("Chart");
		this.setRightButtonVisibility(View.VISIBLE);
		this.setToolboxButtonVisibility(View.GONE);

		//pclSeries.add(new DatePoint(0, (int)0, ""));
		
		//Loop over the saved sessions
		UserQAAnswer uqaa = new UserQAAnswer(dbAdapter);
		sessionCursor = uqaa.getReportSessionCursor();
		while(sessionCursor.moveToNext()) {
			int curSession = sessionCursor.getInt(sessionCursor.getColumnIndex(UserQAAnswer.FIELD_SESSION_ID));
			String date = null;
			long timeStamp = 0l;
			answerCursor = uqaa.getReportDataCursor(curSession + "");
			
			
			
			//Total the answer values in current session
			int total = 0;
			while(answerCursor.moveToNext()) {
				total += answerCursor.getInt(answerCursor.getColumnIndex(UserQAAnswer.FIELD_ANSWER_VALUE));
				if(date == null)
				{
					timeStamp =  answerCursor.getLong(answerCursor.getColumnIndex(UserQAAnswer.FIELD_TIMESTAMP));
					date = formatDate(answerCursor.getLong(answerCursor.getColumnIndex(UserQAAnswer.FIELD_TIMESTAMP)));
				}
			}
			
			pclSeries.add(new LinePoint((int)total, "" + (int)total, ""));
			
			//Create a report entry line
			HashMap<String, String> map;
			map = new HashMap<String, String>();
			
			map.put("line1", "");
			map.put("line2", date + " - Session " + curSession);
			map.put("line3", total + "");
			reportList.add(map);
		}
		
		lineChart.addSeries(pclSeries);
		
		//Setup the list adapter
		String[] from = { "line1", "line2", "line3" };
		int[] to = { R.id.text1, R.id.text2, R.id.text3 };
		SimpleAdapter adapter = new SimpleAdapter(this, reportList, R.layout.list_item_3_inline, from, to);
		lvReport.setAdapter(adapter);
		
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
	
	protected String formatDate(long timestamp) {
        return DateUtils.formatDateTime(null, timestamp, DateUtils.FORMAT_NUMERIC_DATE);
    }
}
