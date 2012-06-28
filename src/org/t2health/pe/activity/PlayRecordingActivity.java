package org.t2health.pe.activity;

import java.io.File;
import java.util.ArrayList;

import org.t2health.pe.Constant;
import org.t2health.pe.ElapsedTimer;
import org.t2health.pe.ListMediaPlayer;
import org.t2health.pe.ListMediaPlayer.OnCompleteListener;
import org.t2health.pe.R;
import org.t2health.pe.tables.Rating;
import org.t2health.pe.tables.Recording;
import org.t2health.pe.tables.RecordingClip;
import org.t2health.pe.tables.TimeLog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PlayRecordingActivity extends ABSSessionNavigationActivity implements OnClickListener, OnSeekBarChangeListener, OnCompleteListener {
	private static final String TAG = PlayRecordingActivity.class.getSimpleName();

	public static final String EXTRA_ELAPSED_LISTEN_TIME = "elapsedListenTime";

	public static final String EXTRA_RIGHT_BUTTON_VISIBILITY = "rightButtonVisibility";
	public static final String EXTRA_RECORDING_ID = "recordingId";
	public static final String EXTRA_START_OFFSET = "startOffset";
	public static final String EXTRA_END_OFFSET = "endOffset";

	private SeekBar seekBar;
	private ListMediaPlayer mediaPlayer;
	private Button pauseButton;
	private Button playButton;
	private ProgressUpdater progressUpdater = new ProgressUpdater();
	protected ElapsedTimer elapsedTimer = new ElapsedTimer();

	//Added for SUDS interrupt - Steveo
	private ArrayList<Recording> imaginalRecordings;
	private Recording imaginalRecording;
	private long imaginalStartTime;
	private long imaginalEndTime;
	private boolean promptedSUDSStart = false;
	private boolean promptedSUDSEnd = false;
	private static final int ACTIVITY_ADD_PRE_RATING = 3098;
	private static final int ACTIVITY_PLAY_RECORDING = 654;
	private static final int ACTIVITY_ADD_POST_AND_PEAK_RATING = 6843;
	private Rating rating;

	private Handler updateProgressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			updateProgress();
		}
	};
	private Chronometer chronometer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Recording recording = new Recording(dbAdapter);
		recording._id = intent.getLongExtra(EXTRA_RECORDING_ID, 0);

		if(!recording.load()) {
			this.finish();
			return;
		}
		
		// get the recordings and the total duration.
		ArrayList<RecordingClip> recordingClips = recording.getClips();
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for(int i = 0; i < recordingClips.size(); ++i) {
			RecordingClip rec = recordingClips.get(i);
			uris.add(Uri.fromFile(new File(rec.file_path)));
		}

		//TODO: mediaPlayer.getDuration returns wrong result on some phones, recording.getDuration seems correct  -Steveo
		mediaPlayer = new ListMediaPlayer(this, uris);
		Log.v(TAG, "recording.getDuration():"+recording.getDuration());
		Log.v(TAG, "mediaPlayer.getDuration():"+mediaPlayer.getDuration());
		Log.v(TAG, "mediaPlayer.getFullDuration():"+mediaPlayer.getFullDuration());
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setStartOffset((int)intent.getLongExtra(EXTRA_START_OFFSET, 0));
		mediaPlayer.setEndOffset((int)intent.getLongExtra(EXTRA_END_OFFSET, 0));
		Log.v(TAG, "so:"+mediaPlayer.getStartOffset());
		Log.v(TAG, "eo:"+mediaPlayer.getEndOffset());

		// set the view.
		this.setContentView(R.layout.play_recording_activity);

		this.setRightButtonText(getString(R.string.done));
		this.setRightButtonVisibility(intent.getIntExtra(EXTRA_RIGHT_BUTTON_VISIBILITY, View.GONE));
		this.setToolboxButtonVisibility(View.GONE);

		pauseButton = (Button)this.findViewById(R.id.pauseButton);
		pauseButton.setOnClickListener(this);

		playButton = (Button)this.findViewById(R.id.playButton);
		playButton.setOnClickListener(this);

		seekBar = (SeekBar)this.findViewById(R.id.seekBar);
		seekBar.setMax((int) recording.getDuration()); //TODO: changed from mediaplayer.getduration
		seekBar.setOnSeekBarChangeListener(this);

		chronometer = (Chronometer)this.findViewById(R.id.timer);

		if(mediaPlayer.getDuration() <= 0) {
			pauseButton.setEnabled(false);
			playButton.setEnabled(false);
			seekBar.setEnabled(false);
		}

		//Get imaginal markings - steveo
		imaginalRecordings = session.getRecordings();
		imaginalRecording = imaginalRecordings.get(0);
		ArrayList<Rating> ratings = recording.getRatings();
		rating = new Rating(dbAdapter);
		if(ratings.size() > 0) {
			rating = ratings.get(ratings.size() - 1);
		}
		imaginalStartTime = recording.getMarkersMinStartTime(Constant.MARKER_TYPE_IMAGINAL_EXPOSURE);
		imaginalEndTime = recording.getMarkersMaxEndTime(Constant.MARKER_TYPE_IMAGINAL_EXPOSURE) ;
	}

	@Override
	protected void onRightButtonPressed() {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_ELAPSED_LISTEN_TIME, elapsedTimer.getElapsedTime());
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	protected void onBackButtonPressed() {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_ELAPSED_LISTEN_TIME, elapsedTimer.getElapsedTime());
		this.setResult(RESULT_CANCELED, intent);
		this.enabledSlideBackAnimation();
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.pauseButton:
			pauseButtonPressed();
			break;

		case R.id.playButton:
			playButtonPressed();
			break;
		}
	}

	protected void pauseButtonPressed() {
		elapsedTimer.stop();

		progressUpdater.setRunning(false);

		mediaPlayer.pause();
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
	}

	protected void playButtonPressed() {
		elapsedTimer.start();

		mediaPlayer.start();

		playButton.setEnabled(false);
		pauseButton.setEnabled(true);

		progressUpdater = new ProgressUpdater();
		progressUpdater.start();
	}

	@Override
	protected void onPause() {
		super.onPause();

		//pauseButtonPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		progressUpdater.setRunning(false);

		mediaPlayer.stop();
		mediaPlayer.release();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if(seekBar == this.seekBar) {
			chronometer.setBase(SystemClock.elapsedRealtime() - progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		if(seekBar == this.seekBar) {
			mediaPlayer.stop();
			progressUpdater.setRunning(false);

			playButton.setEnabled(false);
			pauseButton.setEnabled(false);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(seekBar == this.seekBar) {
			mediaPlayer.stop();
			mediaPlayer.seekTo(seekBar.getProgress());
			mediaPlayer.start();

			playButton.setEnabled(false);
			pauseButton.setEnabled(true);

			progressUpdater = new ProgressUpdater();
			progressUpdater.start();
		}
	}

	protected void updateProgress() {
		seekBar.setProgress(mediaPlayer.getCurrentPosition());

		//TODO:check if within imaginalStartTime & imaginalEndTime
		if(imaginalStartTime >0)
		{
			if(mediaPlayer.getCurrentPosition() >= imaginalStartTime)
			{
				enteredImaginalStart();
			}

			if(mediaPlayer.getCurrentPosition() >= imaginalEndTime)
			{
				enteredImaginalEnd();
			}
		}
	}

	private void enteredImaginalStart()
	{
		if(!promptedSUDSStart)
		{
			pauseButtonPressed();
			promptedSUDSStart = true;
			new AlertDialog.Builder(this)
			.setTitle("Playback Paused")
			.setMessage("Press OK to enter your SUDS before listening to the Imaginal Exposure portion of the recording.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startPreActivity();
				}
			})

			.create()
			.show();
		}
	}

	private void enteredImaginalEnd()
	{
		if(!promptedSUDSEnd)
		{
			pauseButtonPressed();
			promptedSUDSEnd = true;
			new AlertDialog.Builder(this)
			.setTitle("Playback Paused")
			.setMessage("Press OK to enter your SUDS before continuing the rest of the recording.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startPostActivity();
				}
			})

			.create()
			.show();
		}
	}

	private void startPreActivity() 
	{
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
			imaginalRecording.linkRating(rating, imaginalStartTime, imaginalEndTime);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ACTIVITY_ADD_PRE_RATING) {
			if(resultCode == RESULT_OK) {
				this.linkRatingData(data);
				return;

			} 

		} 
		else if(requestCode == ACTIVITY_ADD_POST_AND_PEAK_RATING) {
			if(resultCode == RESULT_OK) {
				linkRatingData(data);
			} 
		}
	}

	private class ProgressUpdater extends Thread {
		private boolean running = true;

		@Override
		public void run() {
			while(true) {
				updateProgressHandler.sendEmptyMessage(0);

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if(!running) {
					break;
				}
			}
		}

		public void setRunning(boolean r) {
			this.running = r;
		}
	}

	@Override
	public void onCompletion(ListMediaPlayer player) {
		if(player == mediaPlayer) {
			mediaPlayer.seekTo(0);
			playButton.setEnabled(true);
			pauseButton.setEnabled(false);
		}
	}
}
