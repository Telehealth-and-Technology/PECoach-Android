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

		this.setRightButtonVisibility(View.GONE);
		this.setToolboxButtonVisibility(View.GONE);
	}
	
	protected void setContent(String contentString) {
		WebView wv = (WebView)this.findViewById(R.id.webview);
		WebViewUtil.formatWebViewText(this, wv, contentString, android.R.color.primary_text_dark);
		//wv.setBackgroundColor(0); // make the bg transparent
		//wv.loadDataWithBaseURL("fake:/blah", contentString, "text/html", "utf-8", null);
	}
}
