package org.t2health.pe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.t2health.pe.R;
import org.t2health.pe.tables.UserQAAnswer;

import zencharts.charts.DateChart;
import zencharts.data.DatePoint;
import zencharts.data.DateSeries;

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
	public DateChart dateChart;
	public boolean chartMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Set this content view.
		this.setContentView(R.layout.pcl_report_activity);

		dateChart = (DateChart)this.findViewById(R.id.datechart);
		dateChart.LoadFont("Elronmonospace.ttf", 16, 2, 2);
//        dateChart.showGrid = true;
//        dateChart.scrollGrid = false;
//        dateChart.showStars = false;
        //dateChart.setMaxYValue(100);
        dateChart.setVisibility(View.GONE);
        
        DateSeries pclSeries = new DateSeries(this, R.drawable.quadstar);
        pclSeries.dashEffect = new float[] {10,20};
        pclSeries.lineColor = Color.BLUE;
        pclSeries.lineWidth = 5;
		pclSeries.dateLabels = false;
		
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
			
			pclSeries.add(new DatePoint(timeStamp, (int)total, "" + (int)total));
			
			//Create a report entry line
			HashMap<String, String> map;
			map = new HashMap<String, String>();
			
			map.put("line1", "");
			map.put("line2", date + " - Session " + curSession);
			map.put("line3", total + "");
			reportList.add(map);
		}
		
		dateChart.AddSeries(pclSeries);
		
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
			dateChart.setVisibility(View.GONE);
			this.setRightButtonText("Chart");
		}
		else
		{
			chartMode = true;
			dateChart.setVisibility(View.VISIBLE);			
			this.setRightButtonText("List");
		}
	}
	
	protected String formatDate(long timestamp) {
        return DateUtils.formatDateTime(null, timestamp, DateUtils.FORMAT_NUMERIC_DATE);
    }
}
