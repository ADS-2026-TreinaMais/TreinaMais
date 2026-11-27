
package com.pucgo.edu.treinamais.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucgo.edu.treinamais.databinding.PainelProfBinding;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.AlunoResponseDto;
import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.network.dto.ProfessorMetricasResponseDto;
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

public class PainelProfessorActivity extends AppCompatActivity {

    private static final String TAG = "PainelProfessor";

    private PainelProfBinding B;

    private AlunoAdapter alunoAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        B = PainelProfBinding.inflate(getLayoutInflater());
        setContentView(B.getRoot());

        B.btnProfPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
        });

        sessionManager = SessionManager.getInstance(this);

        if (!sessionManager.isLoggedIn()) {
            redirecionarParaLogin();
            return;
        }

        inicializarViews();
        configurarDadosUsuario();
        configurarListeners();
        configurarMonitoramentoSessao();
        carregarAlunos();
    }

    // Recarrega os dados ao voltar para a tela
    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager != null && sessionManager.isLoggedIn()) {
            carregarAlunos();
        }
    }

    private void inicializarViews() {
        B.recyclerAlunos.setLayoutManager(
                new LinearLayoutManager(this)
        );

        alunoAdapter = new AlunoAdapter();

        alunoAdapter.setOnAlunoClickListener(aluno -> {
            Toast.makeText(
                    PainelProfessorActivity.this,
                    "Aluno: " + aluno.getNome(),
                    Toast.LENGTH_SHORT
            ).show();
        });

        B.recyclerAlunos.setAdapter(alunoAdapter);
    }

    private void configurarDadosUsuario() {
        B.tvProfNome.setText(sessionManager.getUserNome());

        B.tvProfEmail.setText(sessionManager.getUserEmail()
                + " (" + sessionManager.getUserTipo() + ")"
        );

        atualizarTextoStatusSessao();
    }

    private void atualizarTextoStatusSessao() {
        Long expiraEm = sessionManager.getExpiraEm();

        if (expiraEm != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm:ss",
                    Locale.getDefault()
            );

            String dataStr = sdf.format(new Date(expiraEm));

            B.tvStatusSessao.setText(
                    "Sessão ativa até: " + dataStr
            );
        } else {
            B.tvStatusSessao.setText("Sessão ativa.");
        }
    }

    private void carregarAlunos() {
        ApiClient.getInstance(this).getAuthApiService().getAlunos().enqueue(new Callback<List<AlunoResponseDto>>() {
            @Override
            public void onResponse(Call<List<AlunoResponseDto>> call, Response<List<AlunoResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AlunoResponseDto> lista = response.body();
                    Log.d(TAG, "Alunos recebidos da API: " + lista.size());

                            if (!lista.isEmpty()) {
                                alunoAdapter.setAlunos(lista);

                                B.tvSemAlunos.setVisibility(View.GONE);
                                B.recyclerAlunos.setVisibility(View.VISIBLE);

                                B.alunos.setText(
                                        String.valueOf(lista.size())
                                );

                            } else {
                                alunoAdapter.setAlunos(Collections.emptyList());

                                B.tvSemAlunos.setVisibility(View.VISIBLE);
                                B.recyclerAlunos.setVisibility(View.GONE);

                                B.alunos.setText("0");
                            }

                        } else {
                            Log.e(
                                    TAG,
                                    "Falha ao consultar /api/alunos: HTTP "
                                            + response.code()
                            );

                            B.tvSemAlunos.setVisibility(View.VISIBLE);
                            B.recyclerAlunos.setVisibility(View.GONE);
                        }
                    }

            @Override
            public void onFailure(Call<List<AlunoResponseDto>> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao consultar /api/alunos: " + t.getMessage(), t);
                B.tvSemAlunos.setVisibility(View.VISIBLE);
                B.recyclerAlunos.setVisibility(View.GONE);
            }
        });

        carregarMetricas();
    }

    private void carregarMetricas() {
        ApiClient.getInstance(this)
                .getAuthApiService()
                .getMetricasProfessor()
                .enqueue(new Callback<ProfessorMetricasResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<ProfessorMetricasResponseDto> call,
                            Response<ProfessorMetricasResponseDto> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {

                            ProfessorMetricasResponseDto metricas = response.body();

                            B.alunos.setText(
                                    String.valueOf(metricas.getTotalAlunos())
                            );

                            B.treinosCriados.setText(
                                    String.valueOf(metricas.getTotalTreinos())
                            );

                            B.ativosHoje.setText(
                                    String.valueOf(metricas.getAlunosAtivos())
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ProfessorMetricasResponseDto> call,
                            Throwable t
                    ) {
                        Log.w(
                                TAG,
                                "Erro ao carregar métricas: " + t.getMessage()
                        );
                    }
                });
    }

    private void configurarListeners() {
        B.btnProfLogout.setOnClickListener(v -> realizarLogout());

        B.btnRenovarSessao.setOnClickListener(v -> realizarRenovacao());

        B.btnVerificarStatus.setOnClickListener(v -> testarEndpointMe());

        B.btnGerenciarAtribuicoes.setOnClickListener(v -> {
            Intent intent = new Intent(PainelProfessorActivity.this, AtribuicaoActivity.class);
            startActivity(intent);
        });
    }

    private void configurarMonitoramentoSessao() {
        sessionManager.setSessionListener(
                new SessionManager.SessionListener() {

                    @Override
                    public void onSessionExpired() {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    PainelProfessorActivity.this,
                                    "Sua sessão expirou ou foi revogada. Faça login novamente.",
                                    Toast.LENGTH_LONG
                            ).show();

                            redirecionarParaLogin();
                        });
                    }

                    @Override
                    public void onLoggedOut() {
                        runOnUiThread(() -> redirecionarParaLogin());
                    }
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
                                Response<MessageResponseDto> response
                        ) {
                            sessionManager.clearSession();

                            Toast.makeText(
                                    PainelProfessorActivity.this,
                                    "Sessão encerrada com sucesso.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            redirecionarParaLogin();
                        }

                        @Override
                        public void onFailure(
                                Call<MessageResponseDto> call,
                                Throwable t
                        ) {
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

        ApiClient.getInstance(this)
                .getAuthApiService()
                .refreshToken(bearerToken)
                .enqueue(new Callback<AuthResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<AuthResponseDto> call,
                            Response<AuthResponseDto> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {

                            AuthResponseDto novoAuth = response.body();

                            sessionManager.saveSession(novoAuth);
                            atualizarTextoStatusSessao();

                            Toast.makeText(
                                    PainelProfessorActivity.this,
                                    "Sessão renovada com sucesso!",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {
                            Toast.makeText(
                                    PainelProfessorActivity.this,
                                    "Não foi possível renovar a sessão. Código: "
                                            + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<AuthResponseDto> call,
                            Throwable t
                    ) {
                        Toast.makeText(
                                PainelProfessorActivity.this,
                                "Erro de rede ao renovar: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void testarEndpointMe() {
        ApiClient.getInstance(this)
                .getAuthApiService()
                .getCurrentUser()
                .enqueue(new Callback<Map<String, String>>() {

                    @Override
                    public void onResponse(
                            Call<Map<String, String>> call,
                            Response<Map<String, String>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {

                            Map<String, String> data = response.body();

                            B.tvStatusSessao.setText(
                                    "Autenticado como: "
                                            + data.get("email")
                                            + "\nPermissões: "
                                            + data.get("authorities")
                            );

                        } else if (response.code() == 401) {
                            Toast.makeText(
                                    PainelProfessorActivity.this,
                                    "Acesso negado: Token revogado ou expirado no servidor.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Map<String, String>> call,
                            Throwable t
                    ) {
                        Toast.makeText(
                                PainelProfessorActivity.this,
                                "Erro ao consultar /me: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void redirecionarParaLogin() {
        Intent intent = new Intent(
                PainelProfessorActivity.this,
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
