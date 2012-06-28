package org.t2health.pe.activity;

import org.t2health.pe.tables.TimeLog;

public class ExplinationOfPETheoryActivity extends TextVideoActivity {

	@Override
	protected void onDestroy() {
		new TimeLog(dbAdapter).setDuration(
				session._id,
				TimeLog.TAGS.LISTEN_TO_EXPLINATION_OF_PE,
				elapsedTimer.getElapsedTime()
		);

		super.onDestroy();
	}

}
