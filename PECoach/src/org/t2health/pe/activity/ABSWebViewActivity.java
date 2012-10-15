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
import org.t2health.pe.WebViewUtil;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class ABSWebViewActivity extends ABSNavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.webview_layout);

		this.setBackButtonVisibity(View.GONE);
		this.setRightButtonVisibility(View.VISIBLE);
		this.setRightButtonText("Done");
		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonContentDescription("Done");
	}
	
	protected void setContent(String contentString) {
		WebView wv = (WebView)this.findViewById(R.id.webview);
		WebViewUtil.formatWebViewText(this, wv, contentString, android.R.color.primary_text_dark);
		//wv.setBackgroundColor(0); // make the bg transparent
		//wv.loadDataWithBaseURL("fake:/blah", contentString, "text/html", "utf-8", null);
	}
	
	@Override
	protected void onRightButtonPressed() {
	 this.finish();
	}
}
