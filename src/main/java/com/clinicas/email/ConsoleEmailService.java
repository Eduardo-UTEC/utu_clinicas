
package com.clinicas.email;

public class ConsoleEmailService implements EmailService {
	public void send(String to, String subject, String body) {
		System.out.println("[EMAIL] " + to + " | " + subject + " | " + body);
	}
}
