package com.clinicas.dao;

import com.clinicas.model.Visitante;
import java.util.*;

public interface VisitanteDao {
	int insert(Visitante v) throws Exception;

	int update(Visitante v) throws Exception;

	List<Visitante> findByPaciente(int idPaciente) throws Exception;

	void link(int idPaciente, int idVisitante) throws Exception;

	void unlink(int idPaciente, int idVisitante) throws Exception;
}
