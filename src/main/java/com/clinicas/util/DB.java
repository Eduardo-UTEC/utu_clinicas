
package com.clinicas.util;
import java.sql.*;
public class DB{
  private static final String URL="jdbc:mysql://localhost:3306/clinicas"
    + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
    + "&useUnicode=true&characterEncoding=utf8";
  private static final String USER="root"; private static final String PASS="root";
  public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL,USER,PASS); }
}
