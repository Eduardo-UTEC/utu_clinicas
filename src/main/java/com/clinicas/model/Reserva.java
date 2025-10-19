
package com.clinicas.model;
import java.time.LocalDateTime;
public class Reserva { private Integer idReserva,idPaciente,idDoctor,idTurno; private LocalDateTime fechaInicio,fechaFin,fechaCreacion; private String estado,motivo,observaciones;
  public Integer getIdReserva(){return idReserva;} public void setIdReserva(Integer v){idReserva=v;}
  public Integer getIdPaciente(){return idPaciente;} public void setIdPaciente(Integer v){idPaciente=v;}
  public Integer getIdDoctor(){return idDoctor;} public void setIdDoctor(Integer v){idDoctor=v;}
  public Integer getIdTurno(){return idTurno;} public void setIdTurno(Integer v){idTurno=v;}
  public LocalDateTime getFechaInicio(){return fechaInicio;} public void setFechaInicio(LocalDateTime v){fechaInicio=v;}
  public LocalDateTime getFechaFin(){return fechaFin;} public void setFechaFin(LocalDateTime v){fechaFin=v;}
  public LocalDateTime getFechaCreacion(){return fechaCreacion;} public void setFechaCreacion(LocalDateTime v){fechaCreacion=v;}
  public String getEstado(){return estado;} public void setEstado(String v){estado=v;}
  public String getMotivo(){return motivo;} public void setMotivo(String v){motivo=v;}
  public String getObservaciones(){return observaciones;} public void setObservaciones(String v){observaciones=v;}
}
