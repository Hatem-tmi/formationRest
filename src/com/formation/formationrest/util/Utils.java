package com.formation.formationrest.util;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {

	/**
	 * Check if email address is valid
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {
		Pattern EMAIL_ADDRESS_PATTERN = Pattern
				.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
						+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
						+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	/**
	 * Save User LoggedIn value in Shared-Preferences
	 * 
	 * @param context
	 * @param isLoggedIn
	 */
	public static void setUserLoggedIn(Context context, boolean isLoggedIn) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor editor = prefs.edit();

		editor.putBoolean("isLoggedIn", isLoggedIn);
		editor.commit();
	}

	/**
	 * Check if user is logged in from Shared-Preferences
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isUserLoggedIn(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return prefs.getBoolean("isLoggedIn", false);
	}

	/**
	 * Save User email value in Shared-Preferences
	 * 
	 * @param context
	 * @param email
	 */
	public static void setUserEmail(Context context, String email) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor editor = prefs.edit();

		editor.putString("email", email);
		editor.commit();
	}

	/**
	 * Get User email from Shared-Preferences
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserEmail(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return prefs.getString("email", null);
	}

	/**
	 * Get Foreground App Name
	 * 
	 * @param context
	 * @return
	 */
	public static String getForegroundApp(Context context) {
		RunningTaskInfo info = null;
		ActivityManager am;
		am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> l = am.getRunningTasks(1000);
		System.out.println(l);
		Iterator<RunningTaskInfo> i = l.iterator();

		String packName = new String();

		while (i.hasNext()) {
			info = i.next();
			packName = info.topActivity.getPackageName();
			if (!packName.equals("com.htc.launcher")
					&& !packName.equals("com.android.launcher")) {
				packName = info.topActivity.getPackageName();
				break;
			}

			if (i.hasNext()) {
				info = i.next();
				packName = info.topActivity.getPackageName();
			}
			break;
		}
		return packName;
	}
}
