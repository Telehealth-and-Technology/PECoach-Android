package org.t2health.pe.activity;

import org.t2health.pe.tables.TimeLog;

public class PlaySessionRecordingActivity extends PlayRecordingActivity {

	@Override
	protected void onDestroy() {
		new TimeLog(dbAdapter).setDuration(
				session._id,
				TimeLog.TAGS.PLAY_SESSION_RECORDING,
				elapsedTimer.getElapsedTime()
		);

		super.onDestroy();
	}

}
