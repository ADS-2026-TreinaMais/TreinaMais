package com.pucgo.edu.treinamais.model;

import com.pucgo.edu.treinamais.util.DeviceUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static String IP = null;
    private static String PORTA = "5432";
    private static String USUARIO = "postgres";
    private static String SENHA = "postgres";
    private static String DB_NOME = "treinamais";

    private static ConnectionFactory instancia;

    public ConnectionFactory() {
    }

    public static synchronized ConnectionFactory getInst() {
        if (instancia == null) {
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    public static void configurar(String ip, String porta, String dbNome, String usuario, String senha) {
        IP = ip;
        PORTA = porta;
        DB_NOME = dbNome;
        USUARIO = usuario;
        SENHA = senha;
    }

    public Connection getConn() throws SQLException {
        String host = getIp();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, PORTA, DB_NOME);
        return DriverManager.getConnection(url, USUARIO, SENHA);
    }

    public static String getIp() {
        return (IP != null && !IP.trim().isEmpty()) ? IP : (DeviceUtils.isEmulator() ? "10.0.2.2" : "127.0.0.1");
    }
    public static String getPorta() { return PORTA; }
    public static String getDbNome() { return DB_NOME; }
    public static String getUsuario() { return USUARIO; }
}
