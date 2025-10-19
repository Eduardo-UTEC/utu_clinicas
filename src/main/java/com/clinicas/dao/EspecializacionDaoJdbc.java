
package com.clinicas.dao;
import com.clinicas.model.Especializacion; import com.clinicas.util.DB;
import java.sql.*; import java.util.*;
public class EspecializacionDaoJdbc implements EspecializacionDao{
  public int insert(Especializacion e) throws Exception{ final String SQL="INSERT INTO ESPECIALIZACION (Nombre) VALUES (?)"; try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)){ ps.setString(1,e.getNombre()); int n=ps.executeUpdate(); try(ResultSet rs=ps.getGeneratedKeys()){ if(rs.next()) e.setIdEspecializacion(rs.getInt(1)); } return n; } }
  public int update(Especializacion e) throws Exception{ final String SQL="UPDATE ESPECIALIZACION SET Nombre=? WHERE idEspecializacion=?"; try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL)){ ps.setString(1,e.getNombre()); ps.setInt(2,e.getIdEspecializacion()); return ps.executeUpdate(); } }
  public int delete(int id) throws Exception{ final String SQL="DELETE FROM ESPECIALIZACION WHERE idEspecializacion=?"; try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL)){ ps.setInt(1,id); return ps.executeUpdate(); } }
  public java.util.List<Especializacion> findAll() throws Exception{ final String SQL="SELECT * FROM ESPECIALIZACION ORDER BY Nombre"; java.util.List<Especializacion> list=new java.util.ArrayList<>(); try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL)){ try(ResultSet rs=ps.executeQuery()){ while(rs.next()){ Especializacion e=new Especializacion(); e.setIdEspecializacion(rs.getInt("idEspecializacion")); e.setNombre(rs.getString("Nombre")); list.add(e);} } } return list; }
  public Especializacion findById(int id) throws Exception{ final String SQL="SELECT * FROM ESPECIALIZACION WHERE idEspecializacion=?"; try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL)){ ps.setInt(1,id); try(ResultSet rs=ps.executeQuery()){ if(rs.next()){ Especializacion e=new Especializacion(); e.setIdEspecializacion(rs.getInt("idEspecializacion")); e.setNombre(rs.getString("Nombre")); return e; } } } return null; }
}
