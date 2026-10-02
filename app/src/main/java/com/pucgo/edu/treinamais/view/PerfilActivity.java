package com.pucgo.edu.treinamais.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pucgo.edu.treinamais.R;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.security.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // Componentes da tela
        TextView txtNome = findViewById(R.id.txtNome);
        TextView txtEmail = findViewById(R.id.txtEmail);
        TextView txtTipo = findViewById(R.id.txtTipo);

        Button btnSair = findViewById(R.id.btnSair);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        // Recupera a sessão atual
        sessionManager = SessionManager.getInstance(this);

        // Exibe os dados do usuário logado
        txtNome.setText(sessionManager.getUserNome());
        txtEmail.setText(sessionManager.getUserEmail());
        txtTipo.setText(sessionManager.getUserTipo());

        // Botão para sair da conta
        btnSair.setOnClickListener(v -> realizarLogout());

        // Botão para voltar ao painel
        btnVoltar.setOnClickListener(v -> finish());

        // Ajuste da tela para as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void realizarLogout() {

        String token = sessionManager.getToken();

        if (token != null) {

            String bearerToken = "Bearer " + token;

            ApiClient.getInstance(this)
                    .getAuthApiService()
                    .logout(bearerToken)
                    .enqueue(new Callback<MessageResponseDto>() {

                        @Override
                        public void onResponse(
                                Call<MessageResponseDto> call,
                                Response<MessageResponseDto> response) {

                            sessionManager.clearSession();

                            Toast.makeText(
                                    PerfilActivity.this,
                                    "Sessão encerrada com sucesso.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            redirecionarParaLogin();
                        }

                        @Override
                        public void onFailure(
                                Call<MessageResponseDto> call,
                                Throwable t) {

                            sessionManager.clearSession();
                            redirecionarParaLogin();
                        }
                    });

        } else {

            sessionManager.clearSession();
            redirecionarParaLogin();
        }
    }

    private void redirecionarParaLogin() {

        Intent intent = new Intent(
                PerfilActivity.this,
                MainActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}