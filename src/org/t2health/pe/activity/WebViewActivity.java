package org.t2health.pe.activity;


import android.content.Intent;
import android.os.Bundle;

public class WebViewActivity extends ABSWebViewActivity {
	@SuppressWarnings("unused")
	private static final String TAG = WebViewActivity.class.getSimpleName();

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_CONTENT = "content";

	public static final String EXTRA_TITLE_ID = "titleId";
	public static final String EXTRA_CONTENT_ID = "contentId";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();

		int contentId = intent.getIntExtra(EXTRA_CONTENT_ID, -1);

		String contentString = intent.getStringExtra(EXTRA_CONTENT);

		//Log.v(TAG, "contentId:"+contentId);
		if(contentString == null && contentId == -1) {
			this.finish();
			return;
		}

		if(contentId != -1) {
			contentString = getString(contentId);
		}

		this.setContent(contentString);
	}
}
