
package com.clinicas.dao;
import com.clinicas.model.Consulta; import com.clinicas.util.DB;
import java.sql.*;
public class ConsultaDaoJdbc implements ConsultaDao{
  public int upsert(Consulta c) throws Exception{
    final String UPD="UPDATE CONSULTA SET Sintomas=?, Nivel_Emergencia=? WHERE idReserva=?";
    try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(UPD)){ ps.setString(1,c.getSintomas()); ps.setInt(2,c.getNivelEmergencia()); ps.setInt(3,c.getIdReserva()); int n=ps.executeUpdate(); if(n>0) return n; }
    final String INS="INSERT INTO CONSULTA (idReserva,Sintomas,Nivel_Emergencia) VALUES (?,?,?)";
    try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(INS, Statement.RETURN_GENERATED_KEYS)){ ps.setInt(1,c.getIdReserva()); ps.setString(2,c.getSintomas()); ps.setInt(3,c.getNivelEmergencia()); int n=ps.executeUpdate(); try(ResultSet rs=ps.getGeneratedKeys()){ if(rs.next()) c.setIdConsulta(rs.getInt(1)); } return n; }
  }
  public Consulta findByReserva(int idReserva) throws Exception{
    final String SQL="SELECT * FROM CONSULTA WHERE idReserva=?";
    try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL)){ ps.setInt(1,idReserva); try(ResultSet rs=ps.executeQuery()){ if(rs.next()){ Consulta c=new Consulta(); c.setIdConsulta(rs.getInt("idConsulta")); c.setIdReserva(rs.getInt("idReserva")); c.setSintomas(rs.getString("Sintomas")); c.setNivelEmergencia(rs.getInt("Nivel_Emergencia")); return c; } } } return null;
  }
}
