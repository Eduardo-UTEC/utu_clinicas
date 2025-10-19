
package com.clinicas.dao;
import com.clinicas.model.Turno; import com.clinicas.util.DB;
import java.sql.*; import java.util.*;
public class TurnoDaoJdbc implements TurnoDao{
  public List<Turno> findAll() throws Exception{
    final String SQL="SELECT * FROM TURNO ORDER BY nombre";
    List<Turno> list=new ArrayList<>();
    try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL)){
      try(ResultSet rs=ps.executeQuery()){
        while(rs.next()){ Turno t=new Turno(); t.setIdTurno(rs.getInt("idTurno")); t.setNombre(rs.getString("nombre")); list.add(t); }
      }
    }
    return list;
  }
  public int insert(Turno t) throws Exception{
    final String SQL="INSERT INTO TURNO (nombre) VALUES (?)";
    try(Connection cn=DB.getConnection(); PreparedStatement ps=cn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)){
      ps.setString(1,t.getNombre()); int n=ps.executeUpdate();
      try(ResultSet rs=ps.getGeneratedKeys()){ if(rs.next()) t.setIdTurno(rs.getInt(1)); }
      return n;
    }
  }
}
