package com.pucgo.edu.treinamais.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.pucgo.edu.treinamais.R;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;
import com.pucgo.edu.treinamais.network.dto.LoginRequestDto;
import com.pucgo.edu.treinamais.security.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editEmail;
    private TextInputEditText editSenha;
    private Button btnLogin;
    private Button btnIrParaCadastro;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn()) {
            abrirPainel();
            return;
        }

        setContentView(R.layout.activity_main);

        inicializarViews();
        configurarListeners();
    }

    private void inicializarViews() {
        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrParaCadastro = findViewById(R.id.btnIrParaCadastro);
        progressBar = findViewById(R.id.progressBarLogin);
    }

    private void configurarListeners() {
        btnLogin.setOnClickListener(v -> realizarLogin());

        btnIrParaCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void realizarLogin() {
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String senha = editSenha.getText() != null ? editSenha.getText().toString().trim() : "";

        if (email.isEmpty()) {
            editEmail.setError("Informe seu e-mail");
            editEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            editSenha.setError("Informe sua senha");
            editSenha.requestFocus();
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
        if (progressBar != null) {
            progressBar.setVisibility(carregando ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) {
            btnLogin.setEnabled(!carregando);
        }
    }

    private void abrirPainel() {
        Intent intent = new Intent(MainActivity.this, PainelProfessorActivity.class);
        startActivity(intent);
        finish();
    }
}
