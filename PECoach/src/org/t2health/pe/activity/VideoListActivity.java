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

import java.util.ArrayList;
import java.util.List;

import org.t2health.pe.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VideoListActivity extends ABSSessionNavigationActivity implements OnItemClickListener {

	public static final String EXTRA_VIDEOS = "videoArray";
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Parcelable[] parcels = this.getIntent().getParcelableArrayExtra(EXTRA_VIDEOS);
		if(parcels == null || parcels.length == 0) {
			this.finish();
			return;
		}
		
		ArrayList<Video> videos = new ArrayList<Video>();
		for(int i = 0; i < parcels.length; ++i) {
			videos.add((Video)parcels[i]);
		}
		
		if(videos.size() == 1) {
			this.lauchVideoId(videos.get(0).getVideoId());
			this.finish();
			return;
		}
		
		this.setContentView(R.layout.list_layout);
		
		this.listView = (ListView)this.findViewById(R.id.list);
		this.listView.setAdapter(new VideoAdapter(
				this,
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				videos
		));
		this.listView.setOnItemClickListener(this);
		
		this.setRightButtonVisibility(View.GONE);
		this.setToolboxButtonVisibility(View.GONE);
	}
	
	private void lauchVideoId(String videoId) {
		if(videoId != null) {
			Uri youtubeAppUri = Uri.parse("vnd.youtube:"+videoId);
			Uri youtubeWebUri = Uri.parse("http://m.youtube.com/watch?v="+videoId);
			
			Intent intent = new Intent(Intent.ACTION_VIEW, youtubeAppUri);
			if(isCallable(intent)) {
				startActivity(intent);
			} else {
				startActivity(new Intent(Intent.ACTION_VIEW, youtubeWebUri));
			}
//			Intent i = new Intent(this, VideoActivity.class);
//			i.putExtra(VideoActivity.EXTRA_VIDEO_ID, videoId);
//			startActivity(i);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Video video = (Video)arg0.getItemAtPosition(arg2);
		this.lauchVideoId(video.getVideoId());
	}
	
	private class VideoAdapter extends ArrayAdapter<Video> {
		
		private int textResourceId;
		private int resourceId;
		private LayoutInflater layoutInflater;

		public VideoAdapter(Context context, int resource,
				int textViewResourceId, List<Video> objects) {
			super(context, resource, textViewResourceId, objects);
			
			this.layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
			this.resourceId = resource;
			this.textResourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View newView;
			if(convertView != null) {
				newView = convertView;
			} else {
				newView = layoutInflater.inflate(this.resourceId, null);
			}
			
			Video video = this.getItem(position);
			
			((TextView)newView.findViewById(this.textResourceId)).setText(video.getTitle());
			
			return newView;
		}
		
		
	}
	
	public static class Video implements Parcelable {
		public static final Parcelable.Creator<Video> CREATOR
			= new Parcelable.Creator<Video>() {
				public Video createFromParcel(Parcel in) {
					return new Video(in);
				}
				
				public Video[] newArray(int size) {
					return new Video[size];
				}
		};
		
		private String title;
		private String videoId;

		public Video(String title, String videoId) {
			this.setTitle(title);
			this.setVideoId(videoId);
		}
		
		private Video(Parcel in) {
			this.setTitle(in.readString());
			this.setVideoId(in.readString());
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(this.getTitle());
			dest.writeString(this.getVideoId());
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setVideoId(String videoId) {
			this.videoId = videoId;
		}

		public String getVideoId() {
			return videoId;
		}
	}
}
