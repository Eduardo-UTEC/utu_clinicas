
package com.clinicas.dao;

import com.clinicas.model.Turno;
import com.clinicas.util.DB;
import java.sql.*;
import java.util.*;

public class DoctorTurnoDaoJdbc implements DoctorTurnoDao {
	public void add(int idDoctor, int idTurno) throws Exception {
		final String SQL = "INSERT INTO doctorturno (idDoctor,idTurno) VALUES (?,?)";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idDoctor);
			ps.setInt(2, idTurno);
			ps.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RuntimeException("Turno ya registrado para el doctor.");
		}
	}

	public void remove(int idDoctor, int idTurno) throws Exception {
		final String SQL = "DELETE FROM doctorturno WHERE idDoctor=? AND idTurno=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idDoctor);
			ps.setInt(2, idTurno);
			ps.executeUpdate();
		}
	}

	public List<Turno> findByDoctor(int idDoctor) throws Exception {
		final String SQL = "SELECT t.* FROM doctorturno dt JOIN turno t ON t.idTurno=dt.idTurno WHERE dt.idDoctor=? ORDER BY t.nombre";
		List<Turno> list = new ArrayList<>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idDoctor);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Turno t = new Turno();
					t.setIdTurno(rs.getInt("idTurno"));
					t.setNombre(rs.getString("nombre"));
					list.add(t);
				}
			}
		}
		return list;
	}
}
