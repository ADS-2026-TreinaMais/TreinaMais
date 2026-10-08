package com.pucgo.edu.treinamais.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pucgo.edu.treinamais.R;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.network.dto.TreinoResponseDto;
import com.pucgo.edu.treinamais.security.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PainelAlunoActivity extends AppCompatActivity {

    private static final String TAG = "PainelAluno";

    private TextView tvNome;
    private TextView tvEmail;
    private TextView tvStatusSessao;
    private TextView tvQtdTreinos;
    private TextView tvSemTreinos;
    private Button btnPerfil;
    private Button btnLogout;
    private Button btnRenovarSessao;
    private Button btnVerificarStatus;
    private RecyclerView recyclerTreinos;
    private TreinoAdapter treinoAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.painel_aluno);

        sessionManager = SessionManager.getInstance(this);

        if (!sessionManager.isLoggedIn()) {
            redirecionarParaLogin();
            return;
        }

        inicializarViews();
        configurarDadosUsuario();
        configurarListeners();
        configurarMonitoramentoSessao();
        carregarTreinos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            carregarTreinos();
        }
    }

    private void inicializarViews() {
        tvNome = findViewById(R.id.tvAlunoNome);
        tvEmail = findViewById(R.id.tvAlunoEmail);
        tvStatusSessao = findViewById(R.id.tvStatusSessaoAluno);
        tvQtdTreinos = findViewById(R.id.tvQtdTreinos);
        tvSemTreinos = findViewById(R.id.tvSemTreinos);
        btnPerfil = findViewById(R.id.btnAlunoPerfil);
        btnLogout = findViewById(R.id.btnAlunoLogout);
        btnRenovarSessao = findViewById(R.id.btnRenovarSessaoAluno);
        btnVerificarStatus = findViewById(R.id.btnVerificarStatusAluno);

        recyclerTreinos = findViewById(R.id.recyclerTreinosAluno);
        recyclerTreinos.setLayoutManager(new LinearLayoutManager(this));
        treinoAdapter = new TreinoAdapter();
        recyclerTreinos.setAdapter(treinoAdapter);
    }

    private void configurarDadosUsuario() {
        tvNome.setText(sessionManager.getUserNome());
        tvEmail.setText(sessionManager.getUserEmail() + " (" + sessionManager.getUserTipo() + ")");
        atualizarTextoStatusSessao();
    }

    private void atualizarTextoStatusSessao() {
        Long expiraEm = sessionManager.getExpiraEm();
        if (expiraEm != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String dataStr = sdf.format(new Date(expiraEm));
            tvStatusSessao.setText("Sessão ativa até: " + dataStr);
        } else {
            tvStatusSessao.setText("Sessão ativa.");
        }
    }

    private void carregarTreinos() {
        ApiClient.getInstance(this).getAuthApiService().getTreinos().enqueue(new Callback<List<TreinoResponseDto>>() {
            @Override
            public void onResponse(Call<List<TreinoResponseDto>> call, Response<List<TreinoResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TreinoResponseDto> treinos = response.body();
                    Log.d(TAG, "Treinos recebidos da API: " + treinos.size());
                    if (!treinos.isEmpty()) {
                        treinoAdapter.setTreinos(treinos);
                        tvSemTreinos.setVisibility(View.GONE);
                        recyclerTreinos.setVisibility(View.VISIBLE);
                        if (tvQtdTreinos != null) {
                            tvQtdTreinos.setText(String.valueOf(treinos.size()));
                        }
                    } else {
                        treinoAdapter.setTreinos(Collections.emptyList());
                        tvSemTreinos.setVisibility(View.VISIBLE);
                        recyclerTreinos.setVisibility(View.GONE);
                        if (tvQtdTreinos != null) {
                            tvQtdTreinos.setText("0");
                        }
                    }
                } else {
                    Log.e(TAG, "Falha ao consultar /api/treinos: HTTP " + response.code());
                    tvSemTreinos.setVisibility(View.VISIBLE);
                    recyclerTreinos.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<TreinoResponseDto>> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao consultar /api/treinos: " + t.getMessage(), t);
                tvSemTreinos.setVisibility(View.VISIBLE);
                recyclerTreinos.setVisibility(View.GONE);
            }
        });
    }

    private void configurarListeners() {
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(PainelAlunoActivity.this, PerfilActivity.class);
                startActivity(intent);
            });
        }
        btnLogout.setOnClickListener(v -> realizarLogout());
        btnRenovarSessao.setOnClickListener(v -> realizarRenovacao());
        btnVerificarStatus.setOnClickListener(v -> testarEndpointMe());
    }

    private void configurarMonitoramentoSessao() {
        sessionManager.setSessionListener(new SessionManager.SessionListener() {
            @Override
            public void onSessionExpired() {
                runOnUiThread(() -> {
                    Toast.makeText(PainelAlunoActivity.this, "Sua sessão expirou. Faça login novamente.", Toast.LENGTH_LONG).show();
                    redirecionarParaLogin();
                });
            }

            @Override
            public void onLoggedOut() {
                runOnUiThread(() -> redirecionarParaLogin());
            }
        });
    }

    private void realizarLogout() {
        String token = sessionManager.getToken();
        if (token != null) {
            String bearerToken = "Bearer " + token;
            ApiClient.getInstance(this).getAuthApiService().logout(bearerToken).enqueue(new Callback<MessageResponseDto>() {
                @Override
                public void onResponse(Call<MessageResponseDto> call, Response<MessageResponseDto> response) {
                    sessionManager.clearSession();
                    Toast.makeText(PainelAlunoActivity.this, "Sessão encerrada com sucesso.", Toast.LENGTH_SHORT).show();
                    redirecionarParaLogin();
                }

                @Override
                public void onFailure(Call<MessageResponseDto> call, Throwable t) {
                    sessionManager.clearSession();
                    redirecionarParaLogin();
                }
            });
        } else {
            sessionManager.clearSession();
            redirecionarParaLogin();
        }
    }

    private void realizarRenovacao() {
        String token = sessionManager.getToken();
        if (token == null) {
            redirecionarParaLogin();
            return;
        }

        String bearerToken = "Bearer " + token;
        ApiClient.getInstance(this).getAuthApiService().refreshToken(bearerToken).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponseDto novoAuth = response.body();
                    sessionManager.saveSession(novoAuth);
                    atualizarTextoStatusSessao();
                    Toast.makeText(PainelAlunoActivity.this, "Sessão renovada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PainelAlunoActivity.this, "Não foi possível renovar a sessão. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                Toast.makeText(PainelAlunoActivity.this, "Erro de rede ao renovar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void testarEndpointMe() {
        ApiClient.getInstance(this).getAuthApiService().getCurrentUser().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> data = response.body();
                    tvStatusSessao.setText("Autenticado como: " + data.get("email") + "\nPermissões: " + data.get("authorities"));
                } else if (response.code() == 401) {
                    Toast.makeText(PainelAlunoActivity.this, "Acesso negado: Token revogado ou expirado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(PainelAlunoActivity.this, "Erro ao consultar /me: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirecionarParaLogin() {
        Intent intent = new Intent(PainelAlunoActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
