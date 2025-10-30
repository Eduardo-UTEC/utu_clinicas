
package com.clinicas.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

public class Validators {
	private static final Pattern EMAIL_RX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
			Pattern.CASE_INSENSITIVE);

	public static boolean isValidEmail(String email) {
		return email != null && EMAIL_RX.matcher(email.trim()).matches();
	}

	public static LocalDateTime parseStrictDateTime(String text) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
		return LocalDateTime.parse(text.trim(), fmt);
	}
}
