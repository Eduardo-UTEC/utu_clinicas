package com.clinicas.model;

public class Consulta {
	private Integer idConsulta, idReserva;
	private String sintomas;
	private Integer nivelEmergencia;

	public Integer getIdConsulta() {
		return idConsulta;
	}

	public void setIdConsulta(Integer v) {
		idConsulta = v;
	}

	public Integer getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(Integer v) {
		idReserva = v;
	}

	public String getSintomas() {
		return sintomas;
	}

	public void setSintomas(String v) {
		sintomas = v;
	}

	public Integer getNivelEmergencia() {
		return nivelEmergencia;
	}

	public void setNivelEmergencia(Integer v) {
		nivelEmergencia = v;
	}
}
