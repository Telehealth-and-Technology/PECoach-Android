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

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class Constant {

	//public static final File EXTERNAL_STORAGE_DIR = new File(Environment.getExternalStorageDirectory(), "pecoach");
	public static final String MARKER_TYPE_IMAGINAL_EXPOSURE = "imaginal_exposure";

	public static final String SHORT_DATE_TIME_FORMAT = "MMM d, yyyy h:mm a";
	public static final String DATE_FORMAT = "MMM d, yyy";
	public static final String TIME_FORMAT = "h:mm a";

	public static final boolean DEV_MODE = false;
	public static final String REMOTE_STACK_TRACE_URL = "http://www2.tee2.org/trace/report.php";

	public static final String FLURRY_KEY = "W2EVYTHHF3U4LEBFCTC1";

	public static String recordingSession = "";
	
	public static final File getExternalStorageDir(Context c) {
		return c.getExternalCacheDir();
	}

	public static final boolean isExternalStorageDirReady(Context c) {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	public static class DB {
		public static final String NAME = "pe.db";
		public static final int VERSION = 8;
	}
}
