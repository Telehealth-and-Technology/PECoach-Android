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

import org.t2health.pe.R;
import org.t2health.pe.tables.SessionGroup;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ContactManagerActivity extends ABSSessionNavigationActivity implements OnClickListener {

	private static final int CONTACT_PICK_ACTIVITY = 389;
	private static final int CONTACT_VIEW_ACTIVITY = 384;
	private static final int CONTACT_INSERT_ACTIVITY = 385;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.contact_manager_activity);

		this.setToolboxButtonVisibility(View.GONE);
		this.setRightButtonVisibility(View.GONE);

		this.findViewById(R.id.viewButton).setOnClickListener(this);
		this.findViewById(R.id.chooseExistingButton).setOnClickListener(this);
		this.findViewById(R.id.addNewButton).setOnClickListener(this);

		showHideContactBlock();
	}

	private void showHideContactBlock() {
		Uri contactUri = getContactUri();
		if(contactUri != null) {
			Cursor cursor = this.getContentResolver().query(
					contactUri, null, null, null, null
			);
			this.startManagingCursor(cursor);

			if(cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));

				TextView nameTextView = (TextView)this.findViewById(R.id.contactName);
				nameTextView.setText(name);

				this.findViewById(R.id.contactBlock).setVisibility(View.VISIBLE);
				this.findViewById(R.id.noContactMessage).setVisibility(View.GONE);

			} else {
				this.findViewById(R.id.contactBlock).setVisibility(View.GONE);
				this.findViewById(R.id.noContactMessage).setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// save the contact
		if(requestCode == CONTACT_PICK_ACTIVITY) {
			if(data != null) {
				saveContact(data.getData());
			}

		} else if(requestCode == CONTACT_INSERT_ACTIVITY) {
			if(data != null) {
				saveContact(data.getData());
			}
		}

		showHideContactBlock();
	}

	private void saveContact(Uri queryData) {
		Cursor c = managedQuery(queryData, null, null, null, null);
		if(c.moveToFirst()) {
			String lookupKey = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
			SessionGroup sg = session.getSessionGroup();
			sg.theripist_contact_lookup_key = lookupKey;
			sg.save();
		}
		c.close();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.viewButton:
			viewCurrentContact();
			break;

		case R.id.chooseExistingButton:
			chooseExistingContact();
			break;

		case R.id.addNewButton:
			addNewContact();
			break;
		}
	}

	private Uri getContactUri() {
		String lookupKey = session.getSessionGroup().theripist_contact_lookup_key;
		if(lookupKey != null && lookupKey.length() > 0) {
			Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
			Uri res = ContactsContract.Contacts.lookupContact(getContentResolver(), lookupUri);
			return res;
		}
		return null;
	}

	private void viewCurrentContact() {
		Uri res = getContactUri();
		if(res != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW, res);
			startActivityForResult(intent, CONTACT_VIEW_ACTIVITY);
		}
	}

	private void chooseExistingContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, CONTACT_PICK_ACTIVITY);
	}

	private void addNewContact() {
		Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, CONTACT_INSERT_ACTIVITY);
	}

}
