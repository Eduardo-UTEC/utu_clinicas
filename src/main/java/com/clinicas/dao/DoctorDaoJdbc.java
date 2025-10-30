package com.clinicas.dao;

import com.clinicas.model.Doctor;
import com.clinicas.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorDaoJdbc implements DoctorDao {

	@Override
	public int insert(Doctor d) throws Exception {
		final String sql = "INSERT INTO DOCTOR (Nom1,Nom2,Ape1,Ape2,Matricula) VALUES (?,?,?,?,?)";
		try (Connection cn = DB.getConnection();
				PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, d.getNom1());
			ps.setString(2, d.getNom2());
			ps.setString(3, d.getApe1());
			ps.setString(4, d.getApe2());
			ps.setString(5, d.getMatricula());
			int n = ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					d.setIdDoctor(rs.getInt(1));
			}
			return n;
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RuntimeException("La matrícula ya existe.");
		}
	}

	@Override
	public int update(Doctor d) throws Exception {
		final String sql = "UPDATE DOCTOR SET Nom1=?,Nom2=?,Ape1=?,Ape2=?,Matricula=? WHERE idDoctor=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, d.getNom1());
			ps.setString(2, d.getNom2());
			ps.setString(3, d.getApe1());
			ps.setString(4, d.getApe2());
			ps.setString(5, d.getMatricula());
			ps.setInt(6, d.getIdDoctor());
			return ps.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RuntimeException("La matrícula ya existe.");
		}
	}

	@Override
	public Doctor findByMatricula(String m) throws Exception {
		final String sql = "SELECT * FROM DOCTOR WHERE Matricula=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, m);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Doctor d = new Doctor();
					d.setIdDoctor(rs.getInt("idDoctor"));
					d.setNom1(rs.getString("Nom1"));
					d.setNom2(rs.getString("Nom2"));
					d.setApe1(rs.getString("Ape1"));
					d.setApe2(rs.getString("Ape2"));
					d.setMatricula(rs.getString("Matricula"));
					return d;
				}
			}
		}
		return null;
	}

	@Override
	public List<Doctor> search(String t) throws Exception {
		final String sql = "SELECT * FROM DOCTOR "
				+ "WHERE CONCAT(COALESCE(Matricula,''),' ',COALESCE(Nom1,''),' ',COALESCE(Ape1,'')) LIKE ? "
				+ "ORDER BY idDoctor DESC";
		List<Doctor> list = new ArrayList<>();
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, "%" + t + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Doctor d = new Doctor();
					d.setIdDoctor(rs.getInt("idDoctor"));
					d.setNom1(rs.getString("Nom1"));
					d.setNom2(rs.getString("Nom2"));
					d.setApe1(rs.getString("Ape1"));
					d.setApe2(rs.getString("Ape2"));
					d.setMatricula(rs.getString("Matricula"));
					list.add(d);
				}
			}
		}
		return list;
	}

	@Override
	public Doctor findById(int id) throws Exception {
		final String sql = "SELECT * FROM DOCTOR WHERE idDoctor=?";
		try (Connection cn = DB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Doctor d = new Doctor();
					d.setIdDoctor(rs.getInt("idDoctor"));
					d.setNom1(rs.getString("Nom1"));
					d.setNom2(rs.getString("Nom2"));
					d.setApe1(rs.getString("Ape1"));
					d.setApe2(rs.getString("Ape2"));
					d.setMatricula(rs.getString("Matricula"));
					return d;
				}
			}
		}
		return null;
	}
}
