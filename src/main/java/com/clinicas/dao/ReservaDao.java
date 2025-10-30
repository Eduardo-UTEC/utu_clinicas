package com.clinicas.dao;

import com.clinicas.model.Reserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservaDao {
	int insert(Reserva r) throws Exception;

	List<Reserva> listUpcoming() throws Exception;

	boolean existsByPacienteAndDate(int idPaciente, LocalDate date) throws Exception;

	List<Reserva> findAllBetween(LocalDateTime from, LocalDateTime to) throws Exception;

	int updateEstado(int idReserva, String estado) throws Exception;

	List<Reserva> listUpcomingByEstado(String estado) throws Exception;

	List<Reserva> listUpcomingByDoctorAndEstado(int idDoctor, String estado) throws Exception;

	// NUEVO: finaliza la reserva (pone FINALIZADA y setea Fecha_Fin)
	int finalizar(int idReserva, LocalDateTime fechaFin) throws Exception;
}
