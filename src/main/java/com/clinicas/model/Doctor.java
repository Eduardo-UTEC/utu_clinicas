
package com.clinicas.model;
public class Doctor { private Integer idDoctor; private String nom1,nom2,ape1,ape2,matricula;
  public Integer getIdDoctor(){return idDoctor;} public void setIdDoctor(Integer v){idDoctor=v;}
  public String getNom1(){return nom1;} public void setNom1(String v){nom1=v;}
  public String getNom2(){return nom2;} public void setNom2(String v){nom2=v;}
  public String getApe1(){return ape1;} public void setApe1(String v){ape1=v;}
  public String getApe2(){return ape2;} public void setApe2(String v){ape2=v;}
  public String getMatricula(){return matricula;} public void setMatricula(String v){matricula=v;}
  public String toString(){ return (nom1!=null?nom1:"")+" "+(ape1!=null?ape1:""); }
}
