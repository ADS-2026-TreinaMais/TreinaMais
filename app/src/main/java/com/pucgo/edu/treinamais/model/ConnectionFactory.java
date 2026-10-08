package com.pucgo.edu.treinamais.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionFactory {

    private static String IP = null;
    private static String PORTA = "5432";
    private static String USUARIO = "postgres";
    private static String SENHA = "postgres";
    private static String DB_NOME = "treinamais";

    private static boolean isEmulator() {
        return (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.HARDWARE.contains("goldfish")
                || android.os.Build.HARDWARE.contains("ranchu")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || android.os.Build.PRODUCT.contains("sdk_google")
                || android.os.Build.PRODUCT.contains("google_sdk")
                || android.os.Build.PRODUCT.contains("sdk")
                || android.os.Build.PRODUCT.contains("sdk_x86")
                || android.os.Build.PRODUCT.contains("vbox86p")
                || android.os.Build.PRODUCT.contains("emulator")
                || android.os.Build.PRODUCT.contains("simulator");
    }

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
        String host = (IP != null && !IP.trim().isEmpty()) ? IP : (isEmulator() ? "10.0.2.2" : "127.0.0.1");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, PORTA, DB_NOME);
        return DriverManager.getConnection(url, USUARIO, SENHA);
    }

    public static String getIp() { 
        return (IP != null && !IP.trim().isEmpty()) ? IP : (isEmulator() ? "10.0.2.2" : "127.0.0.1"); 
    }
    public static String getPorta() { return PORTA; }
    public static String getDbNome() { return DB_NOME; }
    public static String getUsuario() { return USUARIO; }
}
