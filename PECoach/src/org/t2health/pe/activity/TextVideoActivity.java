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

import org.t2health.pe.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TextVideoActivity extends ABSSessionNavigationActivity implements OnClickListener {

	public static final String EXTRA_CONTENT_ID = "contentId";
	
	public static final int ID_EXPLINATION_OF_PE_THEORY = 1;
	public static final int ID_BREATHING_RETRAINER_LEARN = 2;
	public static final int ID_BREATHING_RETRAINER_WATCH_DEMO = 3;
	public static final int ID_COMMON_REACTIONS_TO_TRAUMA = 4;

	private int contentId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.contentId = this.getIntent().getIntExtra(EXTRA_CONTENT_ID, 0);
		if(this.contentId <= 0) {
			this.finish();
			return;
		}
		
		this.setContentView(R.layout.text_video_layout);
		this.findViewById(R.id.videoButton).setOnClickListener(this);
		this.findViewById(R.id.textButton).setOnClickListener(this);
		
		this.setRightButtonVisibility(View.GONE);
		this.setToolboxButtonVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.videoButton:
			showVideo(this.contentId);
			break;
			
		case R.id.textButton:
			showText(this.contentId);
			break;
		}
	}
	
	private void showVideo(int id) {
		VideoListActivity.Video[] videos = null;
		switch(id) {
			case ID_BREATHING_RETRAINER_LEARN:
				videos = new VideoListActivity.Video[] {
						new VideoListActivity.Video(this.getString(R.string.breath_retrainer_learn), "iAhxU7lADlc"),
						//new VideoListActivity.Video(this.getString(R.string.breath_retrainer_learn), "1134613111001"),
				};
				break;
				
			case ID_BREATHING_RETRAINER_WATCH_DEMO:
				videos = new VideoListActivity.Video[] {
						new VideoListActivity.Video(this.getString(R.string.breath_retrainer_watch), "2dwJYDV7Usk"),
						//new VideoListActivity.Video(this.getString(R.string.breath_retrainer_watch), "1134607088001"),
				};
				break;
				
			case ID_EXPLINATION_OF_PE_THEORY:
				videos = new VideoListActivity.Video[] {
						new VideoListActivity.Video(this.getString(R.string.exp_pet), "ef3zz9G6-k4"),
						//new VideoListActivity.Video(this.getString(R.string.exp_pet), "1134523001001"),
				};
				break;
				
			case ID_COMMON_REACTIONS_TO_TRAUMA:
//				videos = new VideoListActivity.Video[] {
//						new VideoListActivity.Video(getString(R.string.video_view_all), "1099667098001"),
//						new VideoListActivity.Video(getString(R.string.video_fear_and_anxiety), "1099667133001"),
//						new VideoListActivity.Video(getString(R.string.video_reexperiencing_and_avoidance), "1099623535001"),
//						new VideoListActivity.Video(getString(R.string.video_increased_arousal), "1099660775001"),
//						new VideoListActivity.Video(getString(R.string.video_irritability_and_anger), "1099667100001"),
//						new VideoListActivity.Video(getString(R.string.video_guilt_and_depression), "1099671542001"),
//						new VideoListActivity.Video(getString(R.string.video_trust_and_intimacy), "1099667119001"),
//						new VideoListActivity.Video(getString(R.string.video_alcohol_and_drugs), "1099660757001"),
//						new VideoListActivity.Video(getString(R.string.video_conclusion), "1099671578001"),
//				};
				videos = new VideoListActivity.Video[] {
						new VideoListActivity.Video(this.getString(R.string.video_view_all), "Y2kvpu7j9kE"),
						new VideoListActivity.Video(this.getString(R.string.video_fear_and_anxiety), "qLnL-iRgNDQ"),
						new VideoListActivity.Video(this.getString(R.string.video_reexperiencing_and_avoidance), "3DZCaKW3ozA"),
						new VideoListActivity.Video(this.getString(R.string.video_increased_arousal), "t_oXpNJb5fQ"),
						new VideoListActivity.Video(this.getString(R.string.video_irritability_and_anger), "IOc8MC6MgIE"),
						new VideoListActivity.Video(this.getString(R.string.video_guilt_and_depression), "tUmqAHzqe-Q"),
						new VideoListActivity.Video(this.getString(R.string.video_trust_and_intimacy), "MbbPYxTNwG4"),
						new VideoListActivity.Video(this.getString(R.string.video_alcohol_and_drugs), "P7x68vsmxOo"),
						new VideoListActivity.Video(this.getString(R.string.video_conclusion), "zP1MjDR16ks"),
				};
				break;
		}
		
		if(videos != null) {
			Intent i = new Intent(this, VideoListActivity.class);
			i.putExtra(VideoListActivity.EXTRA_VIDEOS, videos);
			this.startActivity(i);
		}
	}
	
	private void showText(int id) {
		int contentId = 0;
		switch(id) {
		case ID_BREATHING_RETRAINER_LEARN:
			contentId = R.string.breathing_retrainer_learn_content;
			break;
			
		case ID_BREATHING_RETRAINER_WATCH_DEMO:
			contentId = R.string.breathing_retrainer_watch_demo_content;
			break;
			
		case ID_EXPLINATION_OF_PE_THEORY:
			contentId = R.string.explination_of_pe_therapy_content;
			break;
			
		case ID_COMMON_REACTIONS_TO_TRAUMA:
			contentId = R.string.common_reactions_content;
			break;
		}
		
		Intent i = new Intent(this, WebViewActivity.class);
		i.putExtra(WebViewActivity.EXTRA_TITLE, this.getTitle());
		i.putExtra(WebViewActivity.EXTRA_CONTENT_ID, contentId);
		this.startActivity(i);
	}

}
