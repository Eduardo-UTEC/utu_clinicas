
package com.clinicas.ui;

import javax.swing.*;
import com.clinicas.util.UITheme;
import com.clinicas.util.ReminderScheduler;
import com.clinicas.email.*;

public class MainApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}
			UITheme.boostFonts(2);

			EmailService email = (EmailConfig.SMTP_HOST == null || EmailConfig.SMTP_HOST.isBlank())
					? new ConsoleEmailService()
					: new SmtpEmailService(EmailConfig.SMTP_HOST, EmailConfig.SMTP_PORT, EmailConfig.SMTP_USER,
							EmailConfig.SMTP_PASS, EmailConfig.FROM);

			//arrancar planificador de recordatorios
			ReminderScheduler.get().start(email);

			MainFrame f = new MainFrame(email);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(1200, 760);
			f.setLocationRelativeTo(null);
			f.setVisible(true);
		});
	}
}
