package com.clinicas.dao;

import com.clinicas.model.Paciente;
import com.clinicas.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PacienteDaoJdbc implements PacienteDao {

	@Override
	public int insert(Paciente p) throws Exception {
		final String sql = "INSERT INTO PACIENTE (Nom1,Nom2,Ape1,Ape2,Cedula,Email) VALUES (?,?,?,?,?,?)";
		try (Connection cn = DB.getConnection();
				PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, p.getNom1());
			ps.setString(2, p.getNom2());
			ps.setString(3, p.getApe1());
			ps.setString(4, p.getApe2());
			ps.setString(5, p.getCedula());
			ps.setString(6, p.getEmail());
			int n = ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					p.setIdPaciente(rs.getInt(1));
			}
			return n;
		} catch (SQLIntegrityConstraintViolationException e) {
			String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
			if (msg.contains("cedula"))
				throw new RuntimeException("Cédula ya registrada");
			if (msg.contains("email"))
				throw new RuntimeException("EMail ya registrado");
			throw e;
		}
	}

	@Override
	public int update(Paciente p) throws Exception {
		final String sql = "UPDATE PACIENTE SET Nom1=?,Nom2=?,Ape1=?,Ape2=?,Cedula=?,Email=? WHERE idPaciente=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, p.getNom1());
			ps.setString(2, p.getNom2());
			ps.setString(3, p.getApe1());
			ps.setString(4, p.getApe2());
			ps.setString(5, p.getCedula());
			ps.setString(6, p.getEmail());
			ps.setInt(7, p.getIdPaciente());
			return ps.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
			if (msg.contains("cedula"))
				throw new RuntimeException("Cédula ya registrada");
			if (msg.contains("email"))
				throw new RuntimeException("EMail ya registrado");
			throw e;
		}
	}

	@Override
	public Paciente findByCedula(String c) throws Exception {
		final String sql = "SELECT * FROM PACIENTE WHERE Cedula=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, c);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Paciente p = new Paciente();
					p.setIdPaciente(rs.getInt("idPaciente"));
					p.setNom1(rs.getString("Nom1"));
					p.setNom2(rs.getString("Nom2"));
					p.setApe1(rs.getString("Ape1"));
					p.setApe2(rs.getString("Ape2"));
					p.setCedula(rs.getString("Cedula"));
					p.setEmail(rs.getString("Email"));
					return p;
				}
			}
		}
		return null;
	}

	@Override
	public List<Paciente> search(String t) throws Exception {
		final String sql = "SELECT * FROM PACIENTE "
				+ "WHERE CONCAT(COALESCE(Cedula,''),' ',COALESCE(Nom1,''),' ',COALESCE(Ape1,'')) LIKE ? "
				+ "ORDER BY idPaciente DESC";
		List<Paciente> list = new ArrayList<>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, "%" + t + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Paciente p = new Paciente();
					p.setIdPaciente(rs.getInt("idPaciente"));
					p.setNom1(rs.getString("Nom1"));
					p.setNom2(rs.getString("Nom2"));
					p.setApe1(rs.getString("Ape1"));
					p.setApe2(rs.getString("Ape2"));
					p.setCedula(rs.getString("Cedula"));
					p.setEmail(rs.getString("Email"));
					list.add(p);
				}
			}
		}
		return list;
	}

	@Override
	public Paciente findById(int id) throws Exception {
		final String sql = "SELECT * FROM PACIENTE WHERE idPaciente=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Paciente p = new Paciente();
					p.setIdPaciente(rs.getInt("idPaciente"));
					p.setNom1(rs.getString("Nom1"));
					p.setNom2(rs.getString("Nom2"));
					p.setApe1(rs.getString("Ape1"));
					p.setApe2(rs.getString("Ape2"));
					p.setCedula(rs.getString("Cedula"));
					p.setEmail(rs.getString("Email"));
					return p;
				}
			}
		}
		return null;
	}
}
