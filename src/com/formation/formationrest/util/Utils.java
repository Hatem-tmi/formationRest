package com.formation.formationrest.util;

import java.util.regex.Pattern;

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
}
