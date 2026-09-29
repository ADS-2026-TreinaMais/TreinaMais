package com.pucgo.edu.treinamais.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionFactory {

    // Configurações padrão apontando para o PostgreSQL (10.0.2.2 no emulador = localhost do PC)
    private static String IP = "10.0.2.2";
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
        if (Objects.equals(IP, "") || Objects.equals(DB_NOME, "")) {
            throw new UnsupportedOperationException("Dados de conexão incompletos");
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        String url = String.format("jdbc:postgresql://%s:%s/%s", IP, PORTA, DB_NOME);
        return DriverManager.getConnection(url, USUARIO, SENHA);
    }

    // Getters para inspeção
    public static String getIp() { return IP; }
    public static String getPorta() { return PORTA; }
    public static String getDbNome() { return DB_NOME; }
    public static String getUsuario() { return USUARIO; }
}
