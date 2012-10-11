package org.t2health.pe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.t2health.pe.R;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Rating;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class RateInvivo extends ABSListLayoutAcitivty implements OnItemClickListener {
	private static final String KEY_ID = "_id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_POST_RATING = "postRating";
	private static final String KEY_RATING_ID = "ratingId";
	private ArrayList<HashMap<String, Object>> items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setRightButtonVisibility(View.GONE);
		this.setToolboxButtonVisibility(View.GONE);

		items = new ArrayList<HashMap<String,Object>>();
		ArrayList<Invivo> invivos = session.getSessionGroup().getInVivos();

		for(int i = 0; i < invivos.size(); ++i) {
			Invivo invivo = invivos.get(i);
			ArrayList<Rating> ratings = invivo.getRatings();
			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put(KEY_ID, invivo._id);
			item.put(KEY_TITLE, invivo.title);

			if(ratings.size() > 0) {
				Rating rating = ratings.get(0);
				item.put(KEY_RATING_ID, rating._id);
				if(rating.postValue >=0 && rating.postValue <= 100) {
					item.put(KEY_POST_RATING, rating.postValue);
				}
			}

			items.add(item);
		}

		this.listView.setOnItemClickListener(this);
		this.setAdapter(new SimpleAdapter(
				this,
				items,
				android.R.layout.simple_list_item_1,
				new String[] {
						KEY_TITLE,
						KEY_POST_RATING,
				},
				new int[] {
						android.R.id.text1,
						android.R.id.text2,
				}
		));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HashMap<String,Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
		if(item == null) {
			return;
		}
		
		long ratingId = (Long)item.get(KEY_RATING_ID);
		Intent intent = new Intent(this, AddEditRatingActivity.class);
		intent.putExtra(AddEditRatingActivity.EXTRA_RATING_ID, ratingId);
		intent.putExtra(AddEditRatingActivity.EXTRA_PRE_VISIBLE, false);
		intent.putExtra(AddEditRatingActivity.EXTRA_POST_ENABLED, true);
		intent.putExtra(AddEditRatingActivity.EXTRA_PEAK_VISIBLE, false);
		intent.putExtra(AddEditRatingActivity.EXTRA_POST_LABEL, getString(R.string.new_suds_rating));
		intent.putExtra(AddEditRatingActivity.EXTRA_TITLE, getString(R.string.enter_final_suds_rating));
		startActivity(intent);
	}
}
