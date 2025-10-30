
package com.clinicas.email;

public class EmailConfig {
	public static final String SMTP_HOST = "smtp.gmail.com";
	public static final int SMTP_PORT = 587; // STARTTLS
	public static final String SMTP_USER = "eolivera1974@gmail.com";
	public static final String SMTP_PASS = System.getenv("SMTP_java_app");
	public static final String FROM = "eolivera1974@gmail.com";
}
