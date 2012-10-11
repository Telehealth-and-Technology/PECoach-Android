package org.t2health.pe.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.t2health.pe.Constant;
import org.t2health.pe.R;
import org.t2health.pe.RecordService;
import org.t2health.pe.ServiceRecording;
import org.t2health.pe.tables.Recording;
import org.t2health.pe.tables.RecordingClip;
import org.t2health.pe.tables.RecordingMarker;
import org.t2health.pe.tables.Session;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class RecordSessionActivity extends ABSSessionNavigationActivity implements OnClickListener, OnCheckedChangeListener, OnCancelListener, RecordService.OnActionListener {
	private static final String TAG = RecordSessionActivity.class.getSimpleName();

	private static final int START_SERVICE = 39582040;
	private static final int SERVICE_CREATED = 23343454;
	private static final int UPDATE_CHRONOMETER = 983402;

	private Button recordButton;
	private Button pauseButton;
	private ToggleButton markButton;
	private Chronometer chronometer;
	private RecordService recordService;
	private ProgressDialog startingDialog;
	private Context thisContext;
	private Intent notificationIntent;
	private UpdateRecordingTimeThread updateRecordTimeThread;
	private Recording sessionRecording;
	
	private Handler initHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case START_SERVICE:
				onEvent("RecordSessionActivity.Start_service");
				RecordService.startService(
						thisContext,
						notificationIntent,
						getText(R.string.recording_title).toString(),
						getText(R.string.recording_desc).toString()
				);
				break;

			case SERVICE_CREATED:
				onServiceCreated();
				break;
				
			case UPDATE_CHRONOMETER:
				updateChronometerTime();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//deletePreviousRecordings();

		Constant.recordingSession = ""+session.index+session.section;
		
		// get the session recording.
		ArrayList<Recording> recordings = session.getRecordings();
		if(recordings.size() > 0) {
			this.sessionRecording = recordings.get(0);
		} else {
			this.sessionRecording = new Recording(dbAdapter);
			this.sessionRecording.session_id = session._id;
			this.sessionRecording.save();
		}
		
		thisContext = this;
		notificationIntent = new Intent(this, org.t2health.pe.activity.RecordSessionActivity.class);
		notificationIntent.putExtra(SessionActivity.EXTRA_SESSION_ID, session._id);
		notificationIntent.putExtra(SessionActivity.EXTRA_START_RECORD_ACTIVITY, true);

		startingDialog = new ProgressDialog(this);
		startingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		startingDialog.setMessage(getString(R.string.loading));
		startingDialog.setOnCancelListener(this);

		this.setContentView(R.layout.record_session_activity);
		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonVisibility(View.GONE);

		this.chronometer = (Chronometer)this.findViewById(R.id.timer);

		this.recordButton = (Button)this.findViewById(R.id.recordButton);
		this.recordButton.setOnClickListener(this);

		this.pauseButton = (Button)this.findViewById(R.id.pauseButton);
		this.pauseButton.setOnClickListener(this);

		this.markButton = (ToggleButton)this.findViewById(R.id.markimgexp);
		this.markButton.setOnCheckedChangeListener(this);
		this.markButton.setEnabled(false);
		

		if(session.index <= 1) {
			this.findViewById(R.id.imaginal_exposure_button_wrappper).setVisibility(View.GONE);
		}
		
		startingDialog.show();
	}

	private void deletePreviousRecordingsDISABLED() {
		onEvent("RecordSessionActivity.deletePreviousRecordings");
		final ArrayList<Session> sessions = session.getSessionGroup().getSessions();

		new Thread(new Runnable() {
			@Override
			public void run() {
				int deleteCount = 0;

				// for all the previous recordings up to the last two.
				for(int i = 0; i < sessions.size() - 2; ++i) {

					// get the recordings for the selected session.
					ArrayList<Recording> recordings = sessions.get(i).getRecordings();
					for(int j = 0; j < recordings.size(); ++j) {

						// get the clips for the recording.
						ArrayList<RecordingClip> clips = recordings.get(j).getClips();
						for(int k = 0; k < clips.size(); ++k) {

							// delete the clip.
							File clipFile = new File(clips.get(k).file_path);
							if(clipFile.exists() && clipFile.canWrite()) {
								clipFile.delete();
								++deleteCount;
							}
						}
					}
				}

				Log.v(TAG, "deleted old recordings:"+deleteCount);
			}}).start();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.v(TAG, "postCreate");

		if(!Constant.isExternalStorageDirReady(this)) {
			new AlertDialog.Builder(this)
				.setMessage(R.string.cannot_record_sdcard_not_ready)
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

		// Load the recording service, dismiss the loading dialog when complete.
		new Thread(new Runnable(){
			@Override
			public void run() {
				Log.v(TAG, "start Thread");
				initHandler.sendEmptyMessage(START_SERVICE);

				while(true) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					recordService = RecordService.getService();
					if(recordService != null) {
						initHandler.sendEmptyMessage(SERVICE_CREATED);
						break;
					}
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.recordButton:
			recordButtonPressed();
			break;

		case R.id.pauseButton:
			pauseButtonPressed();
			break;
		}
	}

	private void onServiceCreated() {
		startingDialog.dismiss();
		recordService.setOnActionListener(this);

		updateChronometerTime();

		if(recordService.isRecording()) {
			onRecordStarted();
		}
	}

	private void recordButtonPressed() {
		recordButton.setEnabled(false);
		pauseButton.setEnabled(false);
		markButton.setEnabled(false);

		Recording recording = this.sessionRecording;
		File outputFile = new File(
				Constant.getExternalStorageDir(this),
				"session_"+ session._id +"_"+ recording._id +"_"+ recording.getClipsCount() +".pea"
		);

//		if(outputFile.exists()) {
//			outputFile.delete();
//		}

		recordService.startRecording(outputFile);
	}

	private void pauseButtonPressed() {
		recordButton.setEnabled(false);
		pauseButton.setEnabled(false);
		markButton.setEnabled(false);
		recordService.stopRecording();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == this.markButton) {
			if(isChecked) {
				recordService.startMarking();
			} else {
				recordService.stopMarking();
			}
		}
	}

	
	
	@Override
	protected void onPause() {
		super.onPause();
		updateRecordTimeThread.setRunning(false);
		
		
		
		showStillRecordingToast();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
		
		updateRecordTimeThread = new UpdateRecordingTimeThread();
		updateRecordTimeThread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		onEvent("RecordSessionActivity.onDestroy()");
		// Recording is done. pull the markers times and save them.
		if(recordService != null && !recordService.isRecording()) {
			recordService.stopMarking();

			Recording recording = this.sessionRecording;
			ArrayList<Long> startTimes = recordService.getStartMarkerTimes();
			ArrayList<Long> endTimes = recordService.getStopMarkerTimes();
			RecordService.stopService(this);

			// build and save the markers for this recording.
			for(int i = 0; i < startTimes.size(); ++i) {
				long startTime = startTimes.get(i);
				long endTime = -1;

				if(endTimes.size() > i) {
					endTime = endTimes.get(i);
				}

				RecordingMarker mark = new RecordingMarker(dbAdapter);
				mark.recording_id = recording._id;
				mark.time_start = startTime;
				mark.time_end = endTime;
				mark.type = Constant.MARKER_TYPE_IMAGINAL_EXPOSURE;
				mark.save();
				Log.v(TAG, "save mark:"+ mark._id +" "+ mark.time_start +" "+ mark.time_end);
			}
		}
	}

	private void saveRecording(ServiceRecording rec) {
		Recording recording = this.sessionRecording;

		// Determine the duration of the recording.
		long duration = rec.getDuration();
		RecordingClip dbRecClip = new RecordingClip(dbAdapter);
		MediaPlayer mp = new MediaPlayer();
		try {
			mp.setDataSource(rec.file.getAbsolutePath());
			mp.prepare();
			duration = mp.getDuration();
			Log.v(TAG, "DUR:"+duration);

		} catch (IllegalArgumentException e) {
			onError("RecordSessionActivity", "saveRecording", e.toString());
		} catch (IllegalStateException e) {
			onError("RecordSessionActivity", "saveRecording", e.toString());
		} catch (IOException e) {
			onError("RecordSessionActivity", "saveRecording", e.toString());
		}
		mp.release();

		// build and save the recording.
		dbRecClip.duration = rec.getDuration();
		dbRecClip.file_path = rec.file.getAbsolutePath();
		dbRecClip.recording_id = recording._id;
		dbRecClip.save();
	}

	private void updateChronometerTime() {
		if(recordService == null) {
			return;
		}
		List<RecordingClip> recordings = this.sessionRecording.getClips();
		long recordServiceDuration = recordService.getDuration();
		long duration = recordServiceDuration;

		for(int i = 0; i < recordings.size(); ++i) {
			duration += recordings.get(i).duration;
		}
		//Log.v(TAG, "Cum Dur:"+duration);

		this.chronometer.setBase(SystemClock.elapsedRealtime() - duration);
	}

	/*private Recording getSessionRecording() {
		ArrayList<Recording> recordings = session.getRecordings();
		Recording recording;
		if(recordings.size() > 0) {
			recording = recordings.get(0);
		} else {
			recording = new Recording(dbAdapter);
			recording.session_id = session._id;
			recording.save();
		}
		return recording;
	}*/

	@Override
	public void onCancel(DialogInterface dialog) {
		if(dialog == startingDialog) {
			if(recordService != null) {
				RecordService.stopService(this);
			}
			this.finish();
		}
	}

	@Override
	public void onRecordStarted() {
		//updateChronometerTime();

		this.recordButton.setEnabled(false);
		this.pauseButton.setEnabled(true);
		this.markButton.setEnabled(true);
		//this.chronometer.start();
	}

	@Override
	public void onStopped() {
		Log.v(TAG, "pauseButtonPressed -> onStopped");
		this.recordButton.setEnabled(true);
		this.pauseButton.setEnabled(false);
		//this.chronometer.stop();
		saveRecording(recordService.getRecording());
		recordService.reset();

		//updateChronometerTime();
	}
	
	
	private class UpdateRecordingTimeThread extends Thread {
		private boolean running = true; 
		
		public void setRunning(boolean b) {
			this.running = b;
		}
		
		@Override
		public void run() {
			while(this.running) {
				initHandler.sendEmptyMessage(UPDATE_CHRONOMETER);
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);
	    
	}
	    
	    
	
}
