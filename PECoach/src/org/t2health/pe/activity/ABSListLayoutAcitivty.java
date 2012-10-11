package org.t2health.pe.activity;

import org.t2health.pe.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class ABSListLayoutAcitivty extends ABSSessionNavigationActivity {
	protected ListView listView;
	protected BaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.abs_list_layout_activity);
		
		listView = (ListView)this.findViewById(R.id.list);
		listView.setEmptyView(this.findViewById(R.id.emptyText));
		
		// put the title into the header of the list.
		View v = this.findViewById(R.id.listHeader);
		if(v != null) {
			ViewGroup parent = (ViewGroup)v.getParent();
			parent.removeView(v);
			v.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.FILL_PARENT, 
					ListView.LayoutParams.WRAP_CONTENT
			));
			listView.addHeaderView(v);
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		TextView tv = (TextView)findViewById(R.id.listHeader).findViewById(R.id.activityDesc);
		if(tv.getText().toString().length() == 0) {
			tv.setVisibility(View.GONE);
		}
	}


	protected void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		listView.setAdapter(adapter);
	}
	
	protected void setDescription(String desc) {
		TextView tv = (TextView)this.findViewById(R.id.listHeader).findViewById(R.id.activityDesc);
		tv.setText(desc);
	}
	
	protected void setEmptyText(int res) {
		((TextView)this.findViewById(R.id.emptyText)).setText(res);
	}
}
