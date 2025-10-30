
package com.clinicas.dao;

import com.clinicas.model.Visitante;
import com.clinicas.util.DB;
import java.sql.*;
import java.util.*;

public class VisitanteDaoJdbc implements VisitanteDao {
	public int insert(Visitante v) throws Exception {
		final String SQL = "INSERT INTO VISITANTE (Nom1,Nom2,Ape1,Ape2) VALUES (?,?,?,?)";
		try (Connection cn = DB.getConnection();
				PreparedStatement ps = cn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, v.getNom1());
			ps.setString(2, v.getNom2());
			ps.setString(3, v.getApe1());
			ps.setString(4, v.getApe2());
			int n = ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					v.setIdVisitante(rs.getInt(1));
			}
			return n;
		}
	}

	public int update(Visitante v) throws Exception {
		final String SQL = "UPDATE VISITANTE SET Nom1=?,Nom2=?,Ape1=?,Ape2=? WHERE idVisitante=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setString(1, v.getNom1());
			ps.setString(2, v.getNom2());
			ps.setString(3, v.getApe1());
			ps.setString(4, v.getApe2());
			ps.setInt(5, v.getIdVisitante());
			return ps.executeUpdate();
		}
	}

	public java.util.List<Visitante> findByPaciente(int idPaciente) throws Exception {
		final String SQL = "SELECT v.* FROM pacientevisitante pv JOIN visitante v ON v.idVisitante=pv.idVisitante WHERE pv.idPaciente=? ORDER BY v.Nom1";
		java.util.List<Visitante> list = new java.util.ArrayList<>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idPaciente);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Visitante v = new Visitante();
					v.setIdVisitante(rs.getInt("idVisitante"));
					v.setNom1(rs.getString("Nom1"));
					v.setNom2(rs.getString("Nom2"));
					v.setApe1(rs.getString("Ape1"));
					v.setApe2(rs.getString("Ape2"));
					list.add(v);
				}
			}
		}
		return list;
	}

	public void link(int idPaciente, int idVisitante) throws Exception {
		final String SQL = "INSERT INTO pacientevisitante (idPaciente,idVisitante) VALUES (?,?)";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idPaciente);
			ps.setInt(2, idVisitante);
			ps.executeUpdate();
		}
	}

	public void unlink(int idPaciente, int idVisitante) throws Exception {
		final String SQL = "DELETE FROM pacientevisitante WHERE idPaciente=? AND idVisitante=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(SQL)) {
			ps.setInt(1, idPaciente);
			ps.setInt(2, idVisitante);
			ps.executeUpdate();
		}
	}
}
