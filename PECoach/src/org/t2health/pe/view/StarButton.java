package org.t2health.pe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

public class StarButton extends CompoundButton {

	public StarButton(Context context) {
		super(context);
		this.init();
	}

	public StarButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	public StarButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() {
		this.setButtonDrawable(android.R.drawable.btn_star);
	}

}
