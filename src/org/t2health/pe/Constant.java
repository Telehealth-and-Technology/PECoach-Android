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


	public static final File getExternalStorageDir(Context c) {
		return c.getExternalCacheDir();
	}

	public static final boolean isExternalStorageDirReady(Context c) {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	public static class DB {
		public static final String NAME = "pe.db";
		public static final int VERSION = 7;
	}
}
