package org.t2health.pe.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.t2health.pe.Accessibility;
import org.t2health.pe.ElapsedTimer;
import org.t2health.pe.R;
import org.t2health.pe.tables.TimeLog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BreathingPracticeActivity extends ABSSessionNavigationActivity implements OnTouchListener{

	private static final String tag = "BreathingPracticeActivity";

	private static boolean breatheON = true;
	private ImageView ballImage;
	private Timer updateLabelTimer;
	private TextView inhale_exhale_label;
	private int inhaleCounter = 0;
	private TextView numberView;
	private String[] inhExhArr = {"Inhale", "Exhale", "Hold"};
	private int currentArrVariable;
	Map<Integer, Integer> animImageMap = new HashMap<Integer, Integer>();
	//private TextView countdownTimeTextView;
	public int timeValue = 0;
	public int totalTimeElapsed = 0;
	private MusicHelper musicHelper;
	//private ImageButton pauseButton;
	private Button plusButton;
	private Button minusButton;
	private boolean fromPause = false;
	private boolean isPlaying = false;
	private int cntr = 0;
	@SuppressWarnings("unused")
	private int cycleCnt = 0;
	private boolean requestToExit = false;
	private int DEFAULT_TIME = 1500;
	private int delta = 0;
	private int MAX_TIME = 2000;
	private int MIN_TIME = 1000;

	private ElapsedTimer elapsedTimer = new ElapsedTimer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.practice_layout);

		this.setRightButtonText(getString(R.string.pause));
		this.setToolboxButtonVisibility(View.GONE);

		//Steveo: volume controls adjust media only
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);  

		//Steveo: added portrait orientation to manifest

		populateMap();

		ballImage = (ImageView)findViewById(R.id.ball);
		inhale_exhale_label = (TextView)findViewById(R.id.inhale_exhale);
		numberView = (TextView)findViewById(R.id.numbers);
		//countdownTimeTextView = (TextView)findViewById(R.id.countdownTime);
		//countdownTimeTextView.setVisibility(View.GONE);
		//pauseButton = (ImageButton)findViewById(R.id.pause);
		//pauseButton.setVisibility(View.GONE);
		plusButton = (Button)findViewById(R.id.plusPace);
		minusButton = (Button)findViewById(R.id.minusPace);

		timeValue = DEFAULT_TIME;

		plusButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				if(breatheON)
				{
					minusButton.setText("Slow Down");
					minusButton.setEnabled(true);
					plusButton.setText("Speed Up");
					plusButton.setEnabled(true);
					delta = 1000/4;

					if(timeValue>MIN_TIME){
						timeValue -= delta;

						//					Toast.makeText(getBaseContext(), " Paced Up:  " +
						//			                (timeValue*4)/1000+ " seconds ",
						//			                Toast.LENGTH_SHORT).show();
						updateLabelTimer.cancel();
						updateTimer(timeValue);
					}else{
						//					Accessibility.show(Toast.makeText(getBaseContext(), 
						//	                "MINIMUM time reached",
						//	                Toast.LENGTH_SHORT));

						minusButton.setText("Slow Down");
						minusButton.setEnabled(true);
						plusButton.setText("   Max   ");
						plusButton.setEnabled(false);
					}
				}
			}
		});

		minusButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				if(breatheON)
				{
					minusButton.setText("Slow Down");
					minusButton.setEnabled(true);
					plusButton.setText("Speed Up");
					plusButton.setEnabled(true);

					delta = 1000/4;

					if(timeValue<MAX_TIME){
						timeValue += delta;
						//					Toast.makeText(getBaseContext(), " Paced Down:  " +
						//			                (timeValue*4)/1000+ " seconds ",
						//			                Toast.LENGTH_SHORT).show();
						updateLabelTimer.cancel();
						updateTimer(timeValue);
					}else{

						//					Accessibility.show(Toast.makeText(getBaseContext(), 
						//			                "MINIMUM time reached",
						//			                Toast.LENGTH_SHORT));

						minusButton.setText("   Min   ");
						minusButton.setEnabled(false);
						plusButton.setText("Speed Up");
						plusButton.setEnabled(true);
					}
				}
			}
		});

		Log.i(tag, "timeValue..."+timeValue);

		numberView.setText("1");

		displayToast();


		//musicHelper = new MusicHelper(BreathingPracticeActivity.this,0);
		//musicHelper.play(0);

		CountDown countdown = new CountDown(600000, 1000);
		countdown.start();

		handleAnimation(ballImage, currentArrVariable);
		handleAnimation(inhale_exhale_label, currentArrVariable);

		if(!fromPause&&!isPlaying)
			updateTimer(timeValue);

		elapsedTimer.start();
		/*pauseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Log.i(tag, "onClick of pauseButton");
				if(!fromPause){
					Log.i(tag, "fromPause false.."+fromPause);
					pauseButton.setImageResource(17301540);
					requestToExit = true;

					//If paused, the practice should resume from inhale
					Log.i(tag, "old cntr value..."+cntr);
					cntr = cntr - (cntr%12);
					Log.i(tag, "new cntr value..."+cntr);
					inhaleCounter = 0;
					currentArrVariable=0;

					musicHelper.pause();
				}
				else{
					Log.i(tag, "fromPause true..."+fromPause);
					handleAnimationFadeOut(inhale_exhale_label);
					handleAnimationFadeOut(ballImage);
					requestToExit = false;
					pauseButton.setImageResource(17301539);
					fromPause = false;
				}
			}
		});*/


	}




	@Override
	protected void onRightButtonPressed() {
		if(!fromPause){
			elapsedTimer.stop();
			breatheON = false;
			Log.i(tag, "fromPause false.."+fromPause);
			//pauseButton.setImageResource(17301540);
			requestToExit = true;

			//If paused, the practice should resume from inhale
			Log.i(tag, "old cntr value..."+cntr);
			cntr = cntr - (cntr%12);
			Log.i(tag, "new cntr value..."+cntr);
			inhaleCounter = 0;
			currentArrVariable=0;

			musicHelper.pause();
			this.setRightButtonText(getString(R.string.play));
		}
		else{
			elapsedTimer.start();
			breatheON = true;
			Log.i(tag, "fromPause true..."+fromPause);
			handleAnimationFadeOut(inhale_exhale_label);
			handleAnimationFadeOut(ballImage);
			requestToExit = false;
			//pauseButton.setImageResource(17301539);
			fromPause = false;
			this.setRightButtonText(getString(R.string.pause));
		}
	}




	private void updateTimer(int timeVal)
	{
		//updateMusicTimer = new Timer();
		Log.i(tag, "timeVal..."+timeVal);
		if(!fromPause&&!isPlaying){

			/*updateMusicTimer.schedule(new TimerTask(){
			public void run(){
				Log.i(tag, "in outer run method");

				TimerMethod1();

			}//end of outer run
			},0,4800);//end of outer Schedule */

			updateLabelTimer = new Timer();
			requestToExit = false;
			updateLabelTimer.scheduleAtFixedRate(new TimerTask() {
				// @Override
				public void run() {

					if(!fromPause){
						Log.i(tag,"in run method");
						// Log.i(tag, "TIMEVALUE..."+timeVal);
						//pauseButton.setImageResource(17301539);
						// totalTimeElapsed +=timeValue;
						//if(totalTimeElapsed % timeValue == )

						Log.i(tag,"cntr..."+cntr);
						Log.i(tag,"cnt % 12"+cntr%12);
						if(cntr%4 ==0)
							TimerMethod1();


						//This is to count up to 15 cycles
						if(cntr%12==0)
							cycleCnt++;

						musicHelper = new MusicHelper(BreathingPracticeActivity.this,cntr%12);
						TimerMethod();
						cntr++;
					}//end of fromPause if statement
					else{
						Log.i(tag, "in else block...fromPause true");
						//pauseButton.setImageResource(17301540);
					}
				}
			}, 0, timeVal);

		}
		else{
			Log.i(tag, "outer else..");
			musicHelper.pause();
		}
	}



	private void displayToast()
	{
		Accessibility.show(Toast.makeText(getBaseContext(), 
				"Click the pause button at the right bottom of the screen at anytime to pause...",
				Toast.LENGTH_SHORT));
	}


	private void TimerMethod()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.

		this.runOnUiThread(Timer_Tick);
	}

	private void TimerMethod1()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.

		this.runOnUiThread(Timer_Tick1);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			//This method runs in the same thread as the UI.    	       

			//Do something to the UI thread here
			if(!requestToExit){
				Log.i(tag, "requestToExit..."+requestToExit);
				myloop();
			}
		}
	};

	private Runnable Timer_Tick1 = new Runnable() {
		public void run() {
			//This method runs in the same thread as the UI.    	       

			//Do something to the UI thread here
			if(!requestToExit){
				Log.i(tag, "requestToExit..."+requestToExit);
				myloop1();
			}
		}
	};

	public void myloop()
	{
		Log.i(tag, "in myloop");
		if(!requestToExit){
			inhaleCounter++;
			if (inhaleCounter > 4) inhaleCounter = 1;

			if(fromPause){

			}

			Log.i(tag,"inhaleCounter..."+inhaleCounter);
			numberView.setText(new Integer(inhaleCounter).toString());
		}
	}

	public void myloop1()
	{
		Log.i(tag, "in myloop1");
		if(!requestToExit){
			if(currentArrVariable>2) currentArrVariable=0;
			Log.i(tag, "inhExhArr[currentArrVariable]"+inhExhArr[currentArrVariable]);
			inhale_exhale_label.setText(inhExhArr[currentArrVariable]);
			//ballImage.setImageResource(R.drawable.glass_yellow);

			if(currentArrVariable==0)
				ballImage.setImageResource(R.drawable.glass_green);
			else if(currentArrVariable==1)
				ballImage.setImageResource(R.drawable.glass_red);
			else
				ballImage.setImageResource(R.drawable.glass_yellow);

			handleAnimation(inhale_exhale_label, currentArrVariable);
			handleAnimation(ballImage,currentArrVariable);
			currentArrVariable++;
		}
	}

	public void handleAnimationFadeOut(View v){
		Log.i(tag, "animation fade out");
		v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
	}

	public void handleAnimation(View v, int position) {
		Log.i(tag, "animImageMap.get(position).intValue()"+animImageMap.get(position).intValue());
		if(!fromPause && !requestToExit){
			v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
			v.startAnimation(AnimationUtils.loadAnimation(this, animImageMap.get(position).intValue()));
		}
	}

	private void populateMap()
	{
		animImageMap.put(new Integer(0), new Integer(R.anim.scale_up));
		animImageMap.put(new Integer(1), new Integer(R.anim.scale_down));
		animImageMap.put(new Integer(2), new Integer(R.anim.scale));
	}

	@Override
	protected void onDestroy() 
	{
		new TimeLog(dbAdapter).setDuration(
				session._id, 
				TimeLog.TAGS.PRACTICE_BREATHING_RETRAINER, 
				elapsedTimer.getElapsedTime()
				);

		Log.i(tag, "onDestroy method");
		super.onDestroy();
		//terminate the timer
		updateLabelTimer.cancel();
		//updateMusicTimer.cancel();

		try {
			musicHelper.stopPlay();
		} catch (IOException e) {
			e.printStackTrace();
		}
		requestToExit = true;

		//finish();
		//Log.i(tag, "Finish called");
		//stopService(new Intent(BreathingPracticeActivity.this, MusicService.class));
		//terminate the timer
	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		Log.i(tag, "TOUCH DETECTED");
		return false;
	}



	public class CountDown extends CountDownTimer{

		public CountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public void onFinish(){
			//countdownTimeTextView.setText("Time Up");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			int seconds = (int) (millisUntilFinished / 1000);
			//int min = seconds/60;
			seconds = seconds%60;
			//countdownTimeTextView.setText("Time: " + min + ":"+ seconds);

		}
	}



	public class MusicHelper{

		private MediaPlayer player = new MediaPlayer();
		private static final String tag = "MusicHelper";
		IBinder mBinder;
		private List<Integer> songs = new ArrayList<Integer>();
		//private int currentPos = 0;
		private Context context;
		//int[] playList = new int[]{R.raw.inhale1,R.raw.hold2,R.raw.hold3,R.raw.hold4,R.raw.hold1,R.raw.hold2,R.raw.hold3,R.raw.hold4,R.raw.exhale1,R.raw.hold2,R.raw.hold3,R.raw.hold4};

		public MusicHelper(Context ctx, int fileNum) {
			Log.i(tag, "Contructor...");

			this.context = ctx;
			//Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
			songs.add(new Integer(R.raw.inhale1));
			songs.add(new Integer(R.raw.hold2));
			songs.add(new Integer(R.raw.hold3));
			songs.add(new Integer(R.raw.hold4));
			songs.add(new Integer(R.raw.exhale1));
			songs.add(new Integer(R.raw.hold2));
			songs.add(new Integer(R.raw.hold3));
			songs.add(new Integer(R.raw.hold4));
			songs.add(new Integer(R.raw.hold1));
			songs.add(new Integer(R.raw.hold2));
			songs.add(new Integer(R.raw.hold3));
			songs.add(new Integer(R.raw.hold4));

			//player = new MediaPlayer.create(MusicService.this, R.raw.f2_inhale);
			play(fileNum);
		}


		private void play(int currentPosition)
		{
			Log.i(tag, "in play method...");

			if(!fromPause){
				player = MediaPlayer.create(this.context, songs.get(currentPosition));
			}

			player.start();

			fromPause = false;

			player.setOnCompletionListener(new OnCompletionListener() {

				public void onCompletion(MediaPlayer arg0) {
					//Log.i(tag, "before calling next song..."+currentPos);
					//player.reset();

					player.release();
					//requestedToExit = true;
					//nextSong();
				}

			});

		}


		/*private void nextSong()
				{
					Log.i(tag, "in nextsong method...");
					currentPos++;
					if (currentPos > (songs.size()-1)) currentPos = 0;
					player.stop();
					player.reset();
					try {
						player.prepare();
						fromPause = false;
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					play(currentPos);

				}*/


		/*public void onDestroy() {
					player.stop();
					Log.i(tag, "Service Stopped");
					//Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
					super.onDestroy();
				} */


		public void pause()
		{
			Log.i(tag, "pause play...");
			//player.pause();
			player.release();
			isPlaying = false;
			fromPause = true;
		}

		private void stopPlay() throws IOException{
			Log.i(tag, "in stop play...");
			// player.stop();
			//player.reset();
			player.release();
		}

	} 
}