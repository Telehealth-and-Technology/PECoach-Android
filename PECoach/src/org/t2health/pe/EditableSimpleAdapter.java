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
package org.t2health.pe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;

public class EditableSimpleAdapter  extends SimpleAdapter {
	private static final String TAG = EditableSimpleAdapter.class.getSimpleName();
	
	private int viewLayout;
	private int alternateLayout;
	private List<? extends Map<String, Object>> items;
	private LayoutInflater layoutInflater;
	private String[] from;
	private int[] to;
	private ValueBinder valueBinder;
	private EditableSimpleAdapter thisObject;
	private HashMap<View,String> viewFromMap = new HashMap<View,String>();
	
	public EditableSimpleAdapter(Context context,
			List<? extends Map<String, Object>> data, 
			int viewLayout,
			int alternateLayout,
			String[] from, int[] to,
			ValueBinder valueBinder) {
		
		super(context, data, viewLayout, from, to);
		
		this.setValueBinder(valueBinder);
		this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = data;
		this.viewLayout = viewLayout;
		this.alternateLayout = alternateLayout;
		this.from = from;
		this.to = to;
		this.thisObject = this;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Map<String, Object> item = (Map<String, Object>) items.get(position);
		
		boolean useAlternateLayout = (valueBinder != null && valueBinder.useAlternateLayout(this, item));
		int layoutId = viewLayout;
		if(useAlternateLayout) {
			Log.v(TAG, "use Alternate Layout");
			layoutId = alternateLayout;
		}
		
		ViewGroup newView = (ViewGroup)this.layoutInflater.inflate(layoutId, null);
		newView = (ViewGroup)super.getView(position, newView, parent);
		
		//if(isEditable) {
			for(int i = 0; i < to.length; ++i) {
				String from = this.from[i];
				int toResId = this.to[i];
				
				View v = newView.findViewById(toResId);
				v.setFocusable(true);
				
				viewFromMap.put(v, from);
				
				// if this is an edit text, handle text input.
				try {
					EditText et = (EditText)v;
					KeyListener kl = et.getKeyListener();
					et.setKeyListener(new WrappedKeyListener(kl){
						@Override
						public void clearMetaKeyState(View view, Editable content,
								int states) {
							keyListener.clearMetaKeyState(view, content, states);
						}
	
						@Override
						public int getInputType() {
							return keyListener.getInputType();
						}
	
						@Override
						public boolean onKeyDown(View view, Editable text,
								int keyCode, KeyEvent event) {
							//Log.v(TAG, "keydown");
							return keyListener.onKeyDown(view, text, keyCode, event);
						}
	
						@Override
						public boolean onKeyOther(View view, Editable text,
								KeyEvent event) {
							//Log.v(TAG, "keyother");
							return keyListener.onKeyOther(view, text, event);
						}
	
						@Override
						public boolean onKeyUp(View view, Editable text,
								int keyCode, KeyEvent event) {
							
							//Log.v(TAG, "keyup");
							if(valueBinder != null) {
								String from = viewFromMap.get(view);
								//Log.v(TAG, "keyUp:"+valueBinder.getValue(thisObject, view));
								items.get(position).put(from, valueBinder.getValue(thisObject, view));
								//valueBinder.setValue(thisObject, position, from, valueBinder.getValue(thisObject, v));
							}
							
							return keyListener.onKeyUp(view, text, keyCode, event);
						}
						
					});
				} catch (ClassCastException e) {}
				
				v.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if(valueBinder != null) {
							String from = viewFromMap.get(v);
							items.get(position).put(from, valueBinder.getValue(thisObject, v));
							//valueBinder.setValue(thisObject, position, from, valueBinder.getValue(thisObject, v));
						}
						return false;
					}
				});
				v.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(valueBinder != null) {
							String from = viewFromMap.get(v);
							items.get(position).put(from, valueBinder.getValue(thisObject, v));
							//valueBinder.setValue(thisObject, position, from, valueBinder.getValue(thisObject, v));
						}
					}
				});
			}
		//}
		
		return newView;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	public void setValueBinder(ValueBinder vb) {
		this.valueBinder = vb;
	}
	
	public interface ValueBinder {
		public Object getValue(EditableSimpleAdapter a, View v);
		public boolean useAlternateLayout(EditableSimpleAdapter a, Map<String,Object> item);
	}
	
	private abstract class WrappedKeyListener implements KeyListener {
		protected KeyListener keyListener;
		
		public WrappedKeyListener(KeyListener kl) {
			this.keyListener = kl;
		}
	}
}