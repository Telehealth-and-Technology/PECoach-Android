package org.t2health.pe.activity;

import java.util.ArrayList;

import org.t2health.pe.Constant;
import org.t2health.pe.R;
import org.t2health.pe.tables.Rating;
import org.t2health.pe.tables.Recording;
import org.t2health.pe.tables.TimeLog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PlayImaginalExposureActivity extends ABSSessionNavigationActivity {
	private static final String TAG = PlayImaginalExposureActivity.class.getSimpleName();
	
	private static final int ACTIVITY_ADD_PRE_RATING = 3098;
	private static final int ACTIVITY_PLAY_RECORDING = 654;
	private static final int ACTIVITY_ADD_POST_AND_PEAK_RATING = 6843;
	private ArrayList<Recording> recordings;
	private Recording recording;
	private long recordingStartTime;
	private long recordingEndTime;
	private long recordingEndOffset;
	private Rating rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recordings = session.getRecordings();
		if(recordings.size() <= 0) {
			this.finish();
			return;
		}

		recording = recordings.get(0);

		ArrayList<Rating> ratings = recording.getRatings();
		rating = new Rating(dbAdapter);
		if(ratings.size() > 0) {
			rating = ratings.get(ratings.size() - 1);
		}

		recordingStartTime = recording.getMarkersMinStartTime(Constant.MARKER_TYPE_IMAGINAL_EXPOSURE);
		recordingEndTime = recording.getMarkersMaxEndTime(Constant.MARKER_TYPE_IMAGINAL_EXPOSURE) ;
		recordingEndOffset = recording.getDuration() - recordingEndTime;

		if(Constant.isExternalStorageDirReady(this)) {
			// start add/edit rating screen
			this.startPreActivity();
		} else {
			new AlertDialog.Builder(this)
				.setMessage(R.string.cannot_play_sdcard_not_ready)
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				})
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.create()
				.show();
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ACTIVITY_ADD_PRE_RATING) {
			if(resultCode == RESULT_OK) {
				this.linkRatingData(data);
				this.startRecordingActivity();
		    	return;

			} else {
				this.finish();
			}

		} else if(requestCode == ACTIVITY_PLAY_RECORDING) {
			if(resultCode == RESULT_OK) {
				// Log the activity and save if necessary
				if(data != null) {
					new TimeLog(dbAdapter).setDuration(
							session._id,
							TimeLog.TAGS.PLAY_IMAGINAL_EXPOSURE_RECORDING,
							data.getLongExtra(PlayRecordingActivity.EXTRA_ELAPSED_LISTEN_TIME, 0)
					);
				}
				this.startPostActivity();
				return;
			} else {
				this.startPreActivity();
				this.enabledSlideBackAnimation();
				return;
			}

		} else if(requestCode == ACTIVITY_ADD_POST_AND_PEAK_RATING) {
			if(resultCode == RESULT_OK) {
				linkRatingData(data);
				this.finish();
			} else {
				this.startRecordingActivity();
				this.enabledSlideBackAnimation();
				return;
			}
		}

		this.finish();
	}

	private void startPreActivity() {
		if(rating.preValue >= 0 && rating.postValue >= 0 && rating.peakValue >= 0) {
			rating = new Rating(dbAdapter);
		}

		// load the rating
		Intent intent = new Intent(this, AddEditRatingActivity.class);
		intent.putExtra(AddEditRatingActivity.EXTRA_TITLE, getString(R.string.imaginal_exposure_suds));
		intent.putExtra(AddEditRatingActivity.EXTRA_RIGHT_BUTTON_TEXT, getString(R.string.next));

		intent.putExtra(AddEditRatingActivity.EXTRA_RATING_ID, rating._id);

		intent.putExtra(AddEditRatingActivity.EXTRA_PRE_ENABLED, true);
		intent.putExtra(AddEditRatingActivity.EXTRA_POST_ENABLED, false);
		intent.putExtra(AddEditRatingActivity.EXTRA_PEAK_ENABLED, false);

		this.startActivityForResult(intent, ACTIVITY_ADD_PRE_RATING);
	}

	private void startRecordingActivity() {
		Intent i = new Intent(this, PlayRecordingActivity.class);
		i.putExtra(
				PlayRecordingActivity.EXTRA_TITLE,
				getString(R.string.listen_imaginal_exp)
		);
		i.putExtra(
				PlayRecordingActivity.EXTRA_RECORDING_ID,
				recording._id
		);
		i.putExtra(
				PlayRecordingActivity.EXTRA_START_OFFSET,
				recordingStartTime
		);
		i.putExtra(
				PlayRecordingActivity.EXTRA_END_OFFSET,
				recordingEndOffset
		);
		i.putExtra(
				PlayRecordingActivity.EXTRA_RIGHT_BUTTON_VISIBILITY,
				View.VISIBLE
		);
		Log.v(TAG, "starting Play Activity");
    	startActivityForResult(i, ACTIVITY_PLAY_RECORDING);
	}

	private void startPostActivity() {
		Intent intent = new Intent(this, AddEditRatingActivity.class);
		intent.putExtra(AddEditRatingActivity.EXTRA_TITLE, getString(R.string.imaginal_exposure_suds));
		intent.putExtra(AddEditRatingActivity.EXTRA_RIGHT_BUTTON_TEXT, getString(R.string.finish));

		intent.putExtra(AddEditRatingActivity.EXTRA_RATING_ID, rating._id);

		intent.putExtra(AddEditRatingActivity.EXTRA_PRE_ENABLED, false);
		intent.putExtra(AddEditRatingActivity.EXTRA_POST_ENABLED, true);
		intent.putExtra(AddEditRatingActivity.EXTRA_PEAK_ENABLED, true);

		this.startActivityForResult(intent, ACTIVITY_ADD_POST_AND_PEAK_RATING);
	}

	private void linkRatingData(Intent data) {
		if(data != null) {
			rating._id = data.getLongExtra(AddEditRatingActivity.EXTRA_RATING_ID, 0);
			rating.load();
			recording.linkRating(rating, recordingStartTime, recordingEndTime);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);
	    
	}
}
