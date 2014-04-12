package com.formation.formationrest.util;

import java.util.regex.Pattern;

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
}
