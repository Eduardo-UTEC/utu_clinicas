package com.clinicas.dao; import com.clinicas.model.Consulta; public interface ConsultaDao{ int upsert(Consulta c) throws Exception; Consulta findByReserva(int idReserva) throws Exception; }
