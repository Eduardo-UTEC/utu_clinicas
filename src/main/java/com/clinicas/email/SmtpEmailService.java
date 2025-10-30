
package com.clinicas.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class SmtpEmailService implements EmailService {
	private final Session session;
	private final String from;

	public SmtpEmailService(String host, int port, String user, String pass, String from) {
		Properties p = new Properties();
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.starttls.enable", "true");
		p.put("mail.smtp.host", host);
		p.put("mail.smtp.port", String.valueOf(port));
		this.from = from;
		this.session = Session.getInstance(p, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});
	}

	public void send(String to, String subject, String body) {
		try {
			Message m = new MimeMessage(session);
			m.setFrom(new InternetAddress(from));
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			m.setSubject(subject);
			m.setText(body);
			Transport.send(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
