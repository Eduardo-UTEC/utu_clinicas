
package com.clinicas.util;

public class Sanitizer {
	public static String cleanName(String s) {
		if (s == null)
			return null;
		String t = s.trim().replaceAll("\\s+", " ");
		return t.replaceAll("[^\\p{L} .'-]", "");
	}

	public static String cleanText(String s, int max) {
		if (s == null)
			return null;
		String t = s.replaceAll("\\p{Cntrl}", "").trim();
		return t.length() > max ? t.substring(0, max) : t;
	}

	public static String cleanEmail(String s) {
		if (s == null)
			return null;
		return s.trim().toLowerCase();
	}

	public static String cleanId(String s) {
		if (s == null)
			return null;
		return s.replaceAll("\\s+", "").replaceAll("[^0-9A-Za-z.-]", "");
	}
}
