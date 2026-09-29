package com.pucgo.edu.treinamais.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.pucgo.edu.treinamais.database.AppDatabase;
import com.pucgo.edu.treinamais.model.entity.UserSessionEntity;
import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionManager {

    private static final String TAG = "SessionManager";
    private static final String PREFS_NAME = "treinamais_secure_session";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NOME = "user_nome";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_TIPO = "user_tipo";
    private static final String KEY_USER_STATUS = "user_status";
    private static final String KEY_EXPIRA_EM = "token_expira_em";

    private static volatile SessionManager instance;

    private final SharedPreferences preferences;
    private final AppDatabase database;
    private final ExecutorService executorService;
    private SessionListener sessionListener;

    public interface SessionListener {
        void onSessionExpired();
        void onLoggedOut();
    }

    private SessionManager(Context context) {
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.preferences = createSharedPreferences(context);
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private SharedPreferences createSharedPreferences(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            return EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(TAG, "Falha ao inicializar EncryptedSharedPreferences, usando SharedPreferences privado seguro", e);
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public void setSessionListener(SessionListener listener) {
        this.sessionListener = listener;
    }

    public synchronized void saveSession(AuthResponseDto authResponse) {
        if (authResponse == null || authResponse.getToken() == null) {
            return;
        }

        long expiraEm = authResponse.getExpiraEm() != null ?
                authResponse.getExpiraEm() :
                (System.currentTimeMillis() + 86400000L);

        preferences.edit()
                .putString(KEY_TOKEN, authResponse.getToken())
                .putLong(KEY_USER_ID, authResponse.getId() != null ? authResponse.getId() : -1L)
                .putString(KEY_USER_NOME, authResponse.getNome())
                .putString(KEY_USER_EMAIL, authResponse.getEmail())
                .putString(KEY_USER_TIPO, authResponse.getTipo())
                .putString(KEY_USER_STATUS, authResponse.getStatus())
                .putLong(KEY_EXPIRA_EM, expiraEm)
                .apply();

        UserSessionEntity entity = new UserSessionEntity(
                authResponse.getId(),
                authResponse.getNome(),
                authResponse.getEmail(),
                authResponse.getTipo(),
                authResponse.getStatus(),
                authResponse.getToken(),
                expiraEm
        );

        executorService.execute(() -> {
            try {
                database.sessionDao().clearAll();
                database.sessionDao().insert(entity);
                Log.d(TAG, "Sessão persistida no Room com sucesso para o usuário: " + entity.getEmail());
            } catch (Exception e) {
                Log.e(TAG, "Erro ao persistir sessão no Room", e);
            }
        });
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public Long getUserId() {
        long id = preferences.getLong(KEY_USER_ID, -1L);
        return id != -1L ? id : null;
    }

    public String getUserNome() {
        return preferences.getString(KEY_USER_NOME, "Usuário");
    }

    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserTipo() {
        return preferences.getString(KEY_USER_TIPO, "ALUNO");
    }

    public Long getExpiraEm() {
        long exp = preferences.getLong(KEY_EXPIRA_EM, -1L);
        return exp != -1L ? exp : null;
    }

    public boolean isTokenExpired() {
        Long expiraEm = getExpiraEm();
        if (expiraEm == null) {
            return true;
        }
        return System.currentTimeMillis() >= expiraEm;
    }

    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.trim().isEmpty() && !isTokenExpired();
    }

    public synchronized void clearSession() {
        preferences.edit().clear().apply();

        executorService.execute(() -> {
            try {
                database.sessionDao().clearAll();
                Log.d(TAG, "Cache de sessão limpo no Room.");
            } catch (Exception e) {
                Log.e(TAG, "Erro ao limpar cache de sessão no Room", e);
            }
        });

        if (sessionListener != null) {
            sessionListener.onLoggedOut();
        }
    }

    public void notifySessionExpired() {
        clearSession();
        if (sessionListener != null) {
            sessionListener.onSessionExpired();
        }
    }
}
