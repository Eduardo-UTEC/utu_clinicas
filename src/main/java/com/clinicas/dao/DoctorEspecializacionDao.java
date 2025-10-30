package com.clinicas.dao;

import com.clinicas.model.Especializacion;
import java.util.*;

public interface DoctorEspecializacionDao {
	void add(int idDoctor, int idEspecializacion) throws Exception;

	void remove(int idDoctor, int idEspecializacion) throws Exception;

	List<Especializacion> findByDoctor(int idDoctor) throws Exception;
}
