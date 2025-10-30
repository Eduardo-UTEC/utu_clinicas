
package com.clinicas.model;

public class Paciente {
	private Integer idPaciente;
	private String nom1, nom2, ape1, ape2, cedula, email;

	public Integer getIdPaciente() {
		return idPaciente;
	}

	public void setIdPaciente(Integer v) {
		idPaciente = v;
	}

	public String getNom1() {
		return nom1;
	}

	public void setNom1(String v) {
		nom1 = v;
	}

	public String getNom2() {
		return nom2;
	}

	public void setNom2(String v) {
		nom2 = v;
	}

	public String getApe1() {
		return ape1;
	}

	public void setApe1(String v) {
		ape1 = v;
	}

	public String getApe2() {
		return ape2;
	}

	public void setApe2(String v) {
		ape2 = v;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String v) {
		cedula = v;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String v) {
		email = v;
	}

	public String toString() {
		return (nom1 != null ? nom1 : "") + " " + (ape1 != null ? ape1 : "");
	}
}
