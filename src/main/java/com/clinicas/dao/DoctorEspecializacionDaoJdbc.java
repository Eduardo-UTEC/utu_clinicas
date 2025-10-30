
package com.clinicas.dao;

import com.clinicas.model.Especializacion;
import com.clinicas.util.DB;
import java.sql.*;
import java.util.*;

public class DoctorEspecializacionDaoJdbc implements DoctorEspecializacionDao {
	public void add(int idDoctor, int idEsp) throws Exception {
		final String SQL = "INSERT INTO doctorespecializacion (idDoctor,idEspecializacion) VALUES (?,?)";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idDoctor);
			ps.setInt(2, idEsp);
			ps.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RuntimeException("La especialidad ya está asignada a este doctor.");
		}
	}

	public void remove(int idDoctor, int idEsp) throws Exception {
		final String SQL = "DELETE FROM doctorespecializacion WHERE idDoctor=? AND idEspecializacion=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idDoctor);
			ps.setInt(2, idEsp);
			ps.executeUpdate();
		}
	}

	public java.util.List<Especializacion> findByDoctor(int idDoctor) throws Exception {
		final String SQL = "SELECT e.* FROM doctorespecializacion de JOIN especializacion e ON e.idEspecializacion=de.idEspecializacion WHERE de.idDoctor=? ORDER BY e.Nombre";
		java.util.List<Especializacion> list = new java.util.ArrayList<>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idDoctor);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Especializacion e = new Especializacion();
					e.setIdEspecializacion(rs.getInt("idEspecializacion"));
					e.setNombre(rs.getString("Nombre"));
					list.add(e);
				}
			}
		}
		return list;
	}
}
