package com.clinicas.dao;

import com.clinicas.model.Paciente;
import java.util.List;

public interface PacienteDao {
    int insert(Paciente p) throws Exception;
    int update(Paciente p) throws Exception;
    Paciente findByCedula(String cedula) throws Exception;
    List<Paciente> search(String text) throws Exception;

    // Nuevo (usado por Reservas y Scheduler)
    Paciente findById(int id) throws Exception;
}
