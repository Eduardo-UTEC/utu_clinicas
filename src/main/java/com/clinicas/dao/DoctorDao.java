package com.clinicas.dao;

import com.clinicas.model.Doctor;
import java.util.List;

public interface DoctorDao {
	int insert(Doctor d) throws Exception;

	int update(Doctor d) throws Exception;

	Doctor findByMatricula(String matricula) throws Exception;

	List<Doctor> search(String text) throws Exception;

	// Nuevo (y requerido por la app)
	Doctor findById(int id) throws Exception;
}
