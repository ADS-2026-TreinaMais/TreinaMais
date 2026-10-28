package com.pucgo.edu.treinamais.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.pucgo.edu.treinamais.R;
import com.pucgo.edu.treinamais.databinding.ActivityMainBinding;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;
import com.pucgo.edu.treinamais.network.dto.LoginRequestDto;
import com.pucgo.edu.treinamais.security.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private ActivityMainBinding B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        B = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(B.getRoot());

        sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn()) {
            abrirPainel();
            return;
        }
        configurarListeners();
    }

    private void configurarListeners() {
        B.btnEntrar.setOnClickListener(v -> realizarLogin());

        B.btnIrParaCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void realizarLogin() {
        String email = B.editLoginEmail.getText() != null ? B.editLoginEmail.getText().toString().trim() : "";
        String senha = B.editLoginSenha.getText() != null ? B.editLoginSenha.getText().toString().trim() : "";

        if (email.isEmpty()) {
            B.editLoginEmail.setError("Informe seu e-mail");
            B.editLoginEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            B.editLoginSenha.setError("Informe sua senha");
            B.editLoginSenha.requestFocus();
            return;
        }

        setCarregando(true);

        LoginRequestDto request = new LoginRequestDto(email, senha);
        ApiClient.getInstance(this).getAuthApiService().login(request).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                setCarregando(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponseDto authResponse = response.body();
                    sessionManager.saveSession(authResponse);

                    Toast.makeText(MainActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    abrirPainel();
                } else {
                    String msg = "E-mail ou senha inválidos.";
                    if (response.code() == 401) {
                        msg = "Credenciais incorretas.";
                    }
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                setCarregando(false);
                Toast.makeText(MainActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCarregando(boolean carregando) {
        B.progressBarLogin.setVisibility(carregando ? View.VISIBLE : View.INVISIBLE);
        B.btnEntrar.setEnabled(!carregando);
    }

    private void abrirPainel() {
        Intent intent;
        if ("ALUNO".equalsIgnoreCase(sessionManager.getUserTipo())) {
            intent = new Intent(MainActivity.this, PainelAlunoActivity.class);
        } else {
            intent = new Intent(MainActivity.this, PainelProfessorActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
