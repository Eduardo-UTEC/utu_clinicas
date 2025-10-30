package com.clinicas.dao;

import com.clinicas.model.Turno;
import java.util.*;

public interface TurnoDao {
	List<Turno> findAll() throws Exception;

	int insert(Turno t) throws Exception;
}
