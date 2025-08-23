package com.jamd.dao;

import java.sql.*;

import org.springframework.stereotype.Component;

@Component
public class ManagerDB {
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=Imitacion_Gestor_Citas;trustServerCertificate=true;";
    private String user = "AdminLogin";
    private String password = "Administrador123%";

    public ManagerDB() {
    }

    public Connection conectar() throws SQLException {

        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println("Conexion establecida!");
        return connection;
    }

    public void desconectar(Connection connection){
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
