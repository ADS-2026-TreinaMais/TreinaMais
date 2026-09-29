package com.pucgo.edu.treinamais.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionFactory {
    private final String IP = "";
    private final String USUARIO = "";
    private final String SENHA = "";
    private final String DB_NOME = "";
    private final String URL = String.format("jdbc:postgressql://%s:%s/%s", IP, SENHA, DB_NOME);
    private static ConnectionFactory instancia;
    public ConnectionFactory getInst() {
        if (instancia == null){
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    public Connection getConn() throws SQLException{
        if (Objects.equals(IP, "")) throw new UnsupportedOperationException("Dados de conexão incompletos");
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
