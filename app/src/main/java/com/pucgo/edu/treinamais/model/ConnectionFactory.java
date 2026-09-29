package com.pucgo.edu.treinamais.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private String IP = "";
    private String USUARIO = "";
    private String SENHA = "";
    private String dbTreinaMais = "";
    private String URL = String.format("jdbc:postgressql://%s:%s/%s", IP, "5432", dbTreinaMais);
    private static ConnectionFactory instancia;
    public ConnectionFactory getInst() {
        if (instancia == null){
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    public Connection getConn() throws SQLException{
        System.err.println("Dados de conexão não implementados");
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
