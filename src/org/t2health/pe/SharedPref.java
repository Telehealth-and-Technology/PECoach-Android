package org.t2health.pe;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {
	public static final String HOMEWORK_ACTIVITY = "homeworkActivity";
	public static final String SESSION_ACTIVITY = "sessionActivity";

	public static boolean getSendAnnonData(SharedPreferences sharedPref) {
		return sharedPref.getBoolean("send_anon_data", true);
	}

	public static void setShowStartupTips(SharedPreferences sharedPref, boolean enabled) {
		sharedPref.edit().putBoolean("show_startup_tips", enabled).commit();
	}
	
	public static boolean getSplitSessionTwo(SharedPreferences sharedPref) {
		return sharedPref.getBoolean("split_two", false);
	}

	public static void setSplitSessionTwo(SharedPreferences sharedPref, boolean enabled) {
		sharedPref.edit().putBoolean("split_two", enabled).commit();
	}
	
	public static boolean getSplitSessionNotify(SharedPreferences sharedPref) {
		return sharedPref.getBoolean("split_notify", false);
	}

	public static void setSplitSessionNotify(SharedPreferences sharedPref, boolean enabled) {
		sharedPref.edit().putBoolean("split_notify", enabled).commit();
	}

	public static long getLastSessionId(SharedPreferences sharedPref) {
		return sharedPref.getLong("lastSessionId", 0);
	}

	public static void setLastSessionId(SharedPreferences sharedPref, long id) {
		sharedPref.edit().putLong("lastSessionId", id).commit();
	}

	public static String getLastActivity(SharedPreferences sharedPref) {
		return sharedPref.getString("lastSessionActivity", SESSION_ACTIVITY);
	}

	public static void setLastActivity(SharedPreferences sharedPref, String activity) {
		sharedPref.edit().putString("lastSessionActivity", activity).commit();
	}

	public static boolean getIsEulaAccepted(SharedPreferences sharedPref) {
		return sharedPref.getBoolean("isEulaAccepted", false); 
	}
	
	public static void setIsEulaAccepted(SharedPreferences sharedPref, boolean value) {
		sharedPref.edit().putBoolean("isEulaAccepted", value).commit();
	}
	
	public static class Anchors {
		public static String Get0(SharedPreferences sharedPref) {
			return sharedPref.getString("anchor0", "");
		}
		public static void Set0(SharedPreferences sharedPref, String inValue) {
			sharedPref.edit().putString("anchor0", inValue).commit();
		}
		public static String Get25(SharedPreferences sharedPref) {
			return sharedPref.getString("anchor25", "");
		}
		public static void Set25(SharedPreferences sharedPref, String inValue) {
			sharedPref.edit().putString("anchor25", inValue).commit();
		}
		public static String Get50(SharedPreferences sharedPref) {
			return sharedPref.getString("anchor50", "");
		}
		public static void Set50(SharedPreferences sharedPref, String inValue) {
			sharedPref.edit().putString("anchor50", inValue).commit();
		}
		public static String Get75(SharedPreferences sharedPref) {
			return sharedPref.getString("anchor75", "");
		}
		public static void Set75(SharedPreferences sharedPref, String inValue) {
			sharedPref.edit().putString("anchor75", inValue).commit();
		}
		public static String Get100(SharedPreferences sharedPref) {
			return sharedPref.getString("anchor100", "");
		}
		public static void Set100(SharedPreferences sharedPref, String inValue) {
			sharedPref.edit().putString("anchor100", inValue).commit();
		}
	}
	
	public static class Security {
		public static boolean isEnabled(SharedPreferences sharedPref) {
			return sharedPref.getBoolean("security_enabled", false);
		}

		public static String getPin(SharedPreferences sharedPref) {
			return sharedPref.getString("security_pin", "");
		}

		public static String getQuestion1(SharedPreferences sharedPref) {
			return getQuestion(1, sharedPref);
		}

		public static String getQuestion2(SharedPreferences sharedPref) {
			return getQuestion(2, sharedPref);
		}

		public static String getAnswer1(SharedPreferences sharedPref) {
			return getAnswer(1, sharedPref);
		}

		public static String getAnswer2(SharedPreferences sharedPref) {
			return getAnswer(2, sharedPref);
		}

		private static String getQuestion(int index, SharedPreferences sharedPref) {
			return sharedPref.getString("security_question"+index, "");
		}

		private static String getAnswer(int index, SharedPreferences sharedPref) {
			return sharedPref.getString("security_answer"+index, "");
		}

		public static void setEnabled(SharedPreferences sharedPref, boolean b) {
			sharedPref.edit().putBoolean("security_enabled", b).commit();
		}

		public static void setPin(SharedPreferences sharedPref, String pin) {
			sharedPref.edit().putString("security_pin", pin.trim()).commit();
		}

		public static void setChallenge1(SharedPreferences sharedPref, String question, String answer) {
			setChallenge(1, sharedPref, question, answer);
		}

		public static void setChallenge2(SharedPreferences sharedPref, String question, String answer) {
			setChallenge(2, sharedPref, question, answer);
		}

		private static void setChallenge(int index, SharedPreferences sharedPref, String question, String answer) {
			Editor editor = sharedPref.edit();
			editor.putString("security_question"+ index, question.trim());
			editor.putString("security_answer"+ index, answer.trim());
			editor.commit();
		}
	}
}
