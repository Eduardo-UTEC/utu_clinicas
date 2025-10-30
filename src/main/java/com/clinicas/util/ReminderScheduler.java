package com.clinicas.util;

import com.clinicas.dao.*;
import com.clinicas.email.EmailService;
import com.clinicas.model.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Programa un recordatorio por mail 2 hs antes de la reserva. También hace un
 * escaneo periódico cada 60s para agendar envíos pendientes. Sin cambios de
 * esquema: todo en memoria mientras la app esté abierta.
 */
public class ReminderScheduler {

	private static final long SCAN_SECONDS = 60;
	private static final ReminderScheduler INSTANCE = new ReminderScheduler();

	private final ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
	private final Set<Integer> scheduled = ConcurrentHashMap.newKeySet();

	private EmailService email;
	private final ReservaDao reservaDao = new ReservaDaoJdbc();
	private final PacienteDao pacienteDao = new PacienteDaoJdbc();
	private final DoctorDao doctorDao = new DoctorDaoJdbc();

	private ReminderScheduler() {
	}

	public static ReminderScheduler get() {
		return INSTANCE;
	}

	/** Llamar una vez desde MainApp, pasando la implementación de EmailService. */
	public void start(EmailService emailService) {
		this.email = emailService;
		// Escaneo periódico para agendar envíos (por si se agregan reservas mientras la
		// app corre)
		exec.scheduleAtFixedRate(this::scanAndSchedule, 2, SCAN_SECONDS, TimeUnit.SECONDS);
		// Carga inicial de próximas 24 hs
		exec.execute(this::scanAndSchedule);
	}

	/** Invocado al crear una reserva para programar su recordatorio. */
	public void scheduleFor(Reserva r) {
		if (r == null || r.getIdReserva() == null)
			return;
		try {
			Paciente p = pacienteDao.findById(r.getIdPaciente());
			Doctor d = doctorDao.findById(r.getIdDoctor());
			scheduleInternal(r, p, d);
		} catch (Exception ignored) {
		}
	}

	private void scanAndSchedule() {
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime horizon = now.plusHours(24);
			List<Reserva> proximas = new ReservaDaoJdbc().findAllBetween(now, horizon);
			for (Reserva r : proximas) {
				if (!scheduled.contains(r.getIdReserva())) {
					Paciente p = pacienteDao.findById(r.getIdPaciente());
					Doctor d = doctorDao.findById(r.getIdDoctor());
					scheduleInternal(r, p, d);
				}
			}
		} catch (Exception ignored) {
		}
	}

	private void scheduleInternal(Reserva r, Paciente p, Doctor d) {
		if (p == null || d == null || p.getEmail() == null || p.getEmail().isBlank())
			return;

		LocalDateTime when = r.getFechaInicio().minusHours(2); // 2 hs antes
		long delayMs = Duration.between(LocalDateTime.now(), when).toMillis();

		Runnable task = () -> {
			try {
				String asunto = "Recordatorio de consulta";
				String cuerpo = "Estimado/a " + safeName(p) + ",\n\n" + "Le recordamos su consulta con el Dr./Dra. "
						+ safeName(d) + " el " + r.getFechaInicio() + ".\n\nSaludos.";
				email.send(p.getEmail(), asunto, cuerpo);
			} catch (Exception ignored) {
			}
		};

		if (delayMs <= 0) {
			exec.execute(task); // ya estamos dentro de la ventana: enviar ahora
		} else {
			exec.schedule(task, delayMs, TimeUnit.MILLISECONDS);
		}
		scheduled.add(r.getIdReserva());
	}

	private static String safeName(Paciente p) {
		return String.format("%s %s", nullToEmpty(p.getNom1()), nullToEmpty(p.getApe1())).trim();
	}

	private static String safeName(Doctor d) {
		return String.format("%s %s", nullToEmpty(d.getNom1()), nullToEmpty(d.getApe1())).trim();
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}
}
