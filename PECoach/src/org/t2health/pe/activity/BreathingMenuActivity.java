package org.t2health.pe.activity;

import org.t2health.pe.ActivityFactory;
import org.t2health.pe.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class BreathingMenuActivity extends ABSSessionNavigationActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.breathing_menu_layout);

		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonVisibility(View.GONE);

		findViewById(R.id.practice).setOnClickListener(this);
		findViewById(R.id.learn).setOnClickListener(this);
		findViewById(R.id.watch).setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent;
		if( v != null ){
			switch( v.getId() ){
			case R.id.practice:
				intent = new Intent(this, BreathingPracticeActivity.class);
				startActivity(intent);
				break;
			case R.id.learn:
				startActivity(ActivityFactory.getBreatingLearn(this, this.session));
				break;

			case R.id.watch:
				startActivity(ActivityFactory.getBreathingWatchDemo(this, this.session));
				break;
			}
		}

	}

}
