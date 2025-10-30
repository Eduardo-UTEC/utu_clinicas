package com.clinicas.dao;

import com.clinicas.model.Reserva;
import com.clinicas.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDaoJdbc implements ReservaDao {

	@Override
	public int insert(Reserva r) throws Exception {
		// Insert con Fecha_Fin = NULL
		final String sql = "INSERT INTO RESERVA ("
				+ "idPaciente,idDoctor,idTurno,Fecha_Inicio,Fecha_Creacion,Estado,Motivo,Observaciones,Fecha_Fin"
				+ ") VALUES (?,?,?,?,?,?,?,?,?)";
		try (Connection cn = DB.getConnection();
				PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, r.getIdPaciente());
			ps.setInt(2, r.getIdDoctor());
			ps.setInt(3, r.getIdTurno());
			ps.setTimestamp(4, Timestamp.valueOf(r.getFechaInicio()));
			ps.setTimestamp(5, Timestamp.valueOf(r.getFechaCreacion()));
			ps.setString(6, r.getEstado());
			ps.setString(7, r.getMotivo());
			ps.setString(8, r.getObservaciones());
			ps.setNull(9, Types.TIMESTAMP); // Fecha_Fin = NULL mientras esté pendiente

			int n = ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					r.setIdReserva(rs.getInt(1));
			}
			return n;
		}
	}

	@Override
	public List<Reserva> listUpcoming() throws Exception {
		final String sql = "SELECT * FROM RESERVA WHERE Fecha_Inicio >= NOW() ORDER BY Fecha_Inicio ASC";
		List<Reserva> list = new ArrayList<Reserva>();
		try (Connection cn = DB.getConnection();
				PreparedStatement ps = cn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				list.add(mapRow(rs));
		}
		return list;
	}

	@Override
	public List<Reserva> listUpcomingByEstado(String estado) throws Exception {
		if (estado == null || estado.equalsIgnoreCase("TODOS")) {
			return listUpcoming();
		}
		final String sql = "SELECT * FROM RESERVA WHERE Fecha_Inicio >= NOW() AND Estado = ? ORDER BY Fecha_Inicio ASC";
		List<Reserva> list = new ArrayList<Reserva>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, estado);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapRow(rs));
			}
		}
		return list;
	}

	@Override
	public boolean existsByPacienteAndDate(int idPaciente, LocalDate date) throws Exception {
		final String sql = "SELECT 1 FROM RESERVA WHERE idPaciente=? AND DATE(Fecha_Inicio)=? LIMIT 1";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, idPaciente);
			ps.setString(2, date.toString());
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public List<Reserva> findAllBetween(LocalDateTime from, LocalDateTime to) throws Exception {
		final String sql = "SELECT * FROM RESERVA WHERE Fecha_Inicio BETWEEN ? AND ? ORDER BY Fecha_Inicio ASC";
		List<Reserva> list = new ArrayList<Reserva>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setTimestamp(1, Timestamp.valueOf(from));
			ps.setTimestamp(2, Timestamp.valueOf(to));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapRow(rs));
			}
		}
		return list;
	}

	@Override
	public int updateEstado(int idReserva, String estado) throws Exception {
		final String sql = "UPDATE RESERVA SET Estado=? WHERE idReserva=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, estado);
			ps.setInt(2, idReserva);
			return ps.executeUpdate();
		}
	}

	@Override
	public List<Reserva> listUpcomingByDoctorAndEstado(int idDoctor, String estado) throws Exception {
		final String sql = "SELECT * FROM RESERVA " + "WHERE Fecha_Inicio >= NOW() AND idDoctor = ? AND Estado = ? "
				+ "ORDER BY Fecha_Inicio ASC";
		List<Reserva> list = new ArrayList<Reserva>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, idDoctor);
			ps.setString(2, estado);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapRow(rs));
			}
		}
		return list;
	}

	@Override
	public int finalizar(int idReserva, LocalDateTime fechaFin) throws Exception {
		// Solo finaliza si estaba PENDIENTE (evita pisar estados)
		final String sql = "UPDATE RESERVA SET Estado='FINALIZADA', Fecha_Fin=? "
				+ "WHERE idReserva=? AND Estado='PENDIENTE'";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setTimestamp(1, Timestamp.valueOf(fechaFin));
			ps.setInt(2, idReserva);
			return ps.executeUpdate();
		}
	}

	private Reserva mapRow(ResultSet rs) throws Exception {
		Reserva r = new Reserva();
		r.setIdReserva(rs.getInt("idReserva"));
		r.setIdPaciente(rs.getInt("idPaciente"));
		r.setIdDoctor(rs.getInt("idDoctor"));
		r.setIdTurno(rs.getInt("idTurno"));
		r.setFechaInicio(rs.getTimestamp("Fecha_Inicio").toLocalDateTime());
		r.setFechaFin(rs.getTimestamp("Fecha_Fin") == null ? null : rs.getTimestamp("Fecha_Fin").toLocalDateTime());
		r.setFechaCreacion(rs.getTimestamp("Fecha_Creacion").toLocalDateTime());
		r.setEstado(rs.getString("Estado"));
		r.setMotivo(rs.getString("Motivo"));
		r.setObservaciones(rs.getString("Observaciones"));
		return r;
	}
}
