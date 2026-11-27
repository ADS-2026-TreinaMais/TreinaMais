
package com.pucgo.edu.treinamais.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucgo.edu.treinamais.databinding.ActivityAtribuicaoBinding;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.AlunoResponseDto;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.network.dto.TreinoResponseDto;
import com.pucgo.edu.treinamais.security.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtribuicaoActivity extends AppCompatActivity {

    private ActivityAtribuicaoBinding B;

    private SessionManager sessionManager;
    private AtribuicaoAdapter atribuicaoAdapter;

    private final List<TreinoResponseDto> treinos = new ArrayList<>();
    private final List<AlunoResponseDto> alunos = new ArrayList<>();

    private TreinoResponseDto treinoSelecionado;

    private boolean carregandoTreinos = false;
    private boolean carregandoAlunos = false;
    private boolean operacaoEmAndamento = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        B = ActivityAtribuicaoBinding.inflate(getLayoutInflater());
        setContentView(B.getRoot());

        sessionManager = SessionManager.getInstance(this);

        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        inicializarViews();
        configurarListeners();
        carregarTreinos();
    }

    private void inicializarViews() {
        B.recyclerAlunosAtribuicao.setLayoutManager(
                new LinearLayoutManager(this)
        );

        atribuicaoAdapter = new AtribuicaoAdapter(
                (aluno, atribuido) -> realizarAtribuicao(aluno, atribuido)
        );

        B.recyclerAlunosAtribuicao.setAdapter(atribuicaoAdapter);

        B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
        B.recyclerAlunosAtribuicao.setVisibility(View.GONE);

        B.tvEstadoAtribuicoes.setText("Carregando treinos...");
    }

    private void configurarListeners() {
        B.btnVoltarAtribuicao.setOnClickListener(v -> finish());

        B.spTreinosAtribuicao.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        if (position >= 0 && position < treinos.size()) {

                            treinoSelecionado = treinos.get(position);

                            atualizarListaAlunos();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        treinoSelecionado = null;
                    }
                }
        );
    }

    private void carregarTreinos() {
        carregandoTreinos = true;

        B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
        B.tvEstadoAtribuicoes.setText("Carregando treinos...");

        ApiClient.getInstance(this)
                .getAuthApiService()
                .getTreinos()
                .enqueue(new Callback<List<TreinoResponseDto>>() {

                    @Override
                    public void onResponse(
                            Call<List<TreinoResponseDto>> call,
                            Response<List<TreinoResponseDto>> response
                    ) {
                        carregandoTreinos = false;

                        if (response.isSuccessful()
                                && response.body() != null) {

                            treinos.clear();
                            treinos.addAll(response.body());

                            if (treinos.isEmpty()) {

                                treinoSelecionado = null;

                                B.tvInfoTreinoAtribuicao.setText(
                                        "Você ainda não possui treinos cadastrados."
                                );

                                B.tvEstadoAtribuicoes.setText(
                                        "Cadastre um treino para começar a atribuir alunos."
                                );

                                B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
                                B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
                                B.spTreinosAtribuicao.setEnabled(false);

                                return;
                            }

                            List<String> nomesTreinos = new ArrayList<>();

                            for (TreinoResponseDto treino : treinos) {
                                nomesTreinos.add(treino.getNome());
                            }

                            ArrayAdapter<String> spinnerAdapter =
                                    new ArrayAdapter<>(
                                            AtribuicaoActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            nomesTreinos
                                    );

                            spinnerAdapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            B.spTreinosAtribuicao.setAdapter(spinnerAdapter);
                            B.spTreinosAtribuicao.setEnabled(true);

                            // Seleciona explicitamente o primeiro treino
                            B.spTreinosAtribuicao.setSelection(0);

                            // Define o treino selecionado manualmente
                            treinoSelecionado = treinos.get(0);

                            B.tvInfoTreinoAtribuicao.setText(
                                    "Treino selecionado: "
                                            + treinoSelecionado.getNome()
                            );

                            // Carrega os alunos
                            carregarAlunos();

                            // Atualiza a tela explicitamente
                            atualizarListaAlunos();

                        } else {

                            B.tvEstadoAtribuicoes.setText(
                                    "Não foi possível carregar seus treinos. Código: "
                                            + response.code()
                            );

                            B.tvInfoTreinoAtribuicao.setText(
                                    "Erro ao consultar os treinos."
                            );

                            B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
                            B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<TreinoResponseDto>> call,
                            Throwable t
                    ) {
                        carregandoTreinos = false;

                        B.tvEstadoAtribuicoes.setText(
                                "Erro: " + t.toString()
                        );

                        B.tvInfoTreinoAtribuicao.setText(
                                "Falha ao carregar treinos."
                        );

                        B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
                        B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void carregarAlunos() {
        carregandoAlunos = true;

        B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
        B.tvEstadoAtribuicoes.setText("Carregando alunos...");

        ApiClient.getInstance(this)
                .getAuthApiService()
                .getAlunos()
                .enqueue(new Callback<List<AlunoResponseDto>>() {

                    @Override
                    public void onResponse(
                            Call<List<AlunoResponseDto>> call,
                            Response<List<AlunoResponseDto>> response
                    ) {
                        carregandoAlunos = false;

                        if (response.isSuccessful()
                                && response.body() != null) {

                            alunos.clear();
                            alunos.addAll(response.body());

                            atualizarListaAlunos();

                        } else {

                            B.tvEstadoAtribuicoes.setText(
                                    "Não foi possível carregar os alunos. Código: "
                                            + response.code()
                            );

                            B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
                            B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<AlunoResponseDto>> call,
                            Throwable t
                    ) {
                        carregandoAlunos = false;

                        B.tvEstadoAtribuicoes.setText(
                                "Erro de conexão ao carregar alunos."
                        );

                        B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
                        B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void atualizarListaAlunos() {

        if (treinoSelecionado == null) {
            B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
            B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);

            B.tvEstadoAtribuicoes.setText(
                    "Selecione um treino acima."
            );

            return;
        }

        if (carregandoAlunos) {
            B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
            B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);

            B.tvEstadoAtribuicoes.setText(
                    "Carregando alunos..."
            );

            return;
        }

        List<Long> alunosAtribuidos =
                treinoSelecionado.getAlunosIds();

        if (alunosAtribuidos == null) {
            alunosAtribuidos = Collections.emptyList();
        }

        atribuicaoAdapter.setAlunos(
                alunos,
                alunosAtribuidos
        );

        if (alunos.isEmpty()) {

            B.recyclerAlunosAtribuicao.setVisibility(View.GONE);
            B.tvEstadoAtribuicoes.setVisibility(View.VISIBLE);

            B.tvEstadoAtribuicoes.setText(
                    "Nenhum aluno foi encontrado."
            );

        } else {

            B.tvEstadoAtribuicoes.setVisibility(View.GONE);
            B.recyclerAlunosAtribuicao.setVisibility(View.VISIBLE);
        }
    }

    private void realizarAtribuicao(
            AlunoResponseDto aluno,
            boolean atribuido
    ) {

        if (treinoSelecionado == null
                || operacaoEmAndamento) {
            return;
        }

        operacaoEmAndamento = true;

        B.spTreinosAtribuicao.setEnabled(false);

        Long treinoId = treinoSelecionado.getId();
        Long alunoId = aluno.getId();

        Call<MessageResponseDto> chamada;

        if (atribuido) {

            chamada = ApiClient.getInstance(this)
                    .getAuthApiService()
                    .desassociarAlunoDoTreino(
                            treinoId,
                            alunoId
                    );

        } else {

            chamada = ApiClient.getInstance(this)
                    .getAuthApiService()
                    .associarAlunoAoTreino(
                            treinoId,
                            alunoId
                    );
        }

        chamada.enqueue(new Callback<MessageResponseDto>() {

            @Override
            public void onResponse(
                    Call<MessageResponseDto> call,
                    Response<MessageResponseDto> response
            ) {
                operacaoEmAndamento = false;
                B.spTreinosAtribuicao.setEnabled(true);

                if (response.isSuccessful()) {

                    String mensagem = atribuido
                            ? "Treino removido do aluno."
                            : "Treino atribuído ao aluno.";

                    Toast.makeText(
                            AtribuicaoActivity.this,
                            mensagem,
                            Toast.LENGTH_SHORT
                    ).show();

                    recarregarTreinos();

                } else {

                    Toast.makeText(
                            AtribuicaoActivity.this,
                            "Não foi possível realizar a operação. Código: "
                                    + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<MessageResponseDto> call,
                    Throwable t
            ) {
                operacaoEmAndamento = false;
                B.spTreinosAtribuicao.setEnabled(true);

                Toast.makeText(
                        AtribuicaoActivity.this,
                        "Erro de conexão ao realizar a operação.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void recarregarTreinos() {
        Long treinoIdAnterior = treinoSelecionado != null
                ? treinoSelecionado.getId()
                : null;

        carregarTreinos();

        if (treinoIdAnterior != null) {
            for (int i = 0; i < treinos.size(); i++) {
                if (treinos.get(i).getId().equals(treinoIdAnterior)) {
                    B.spTreinosAtribuicao.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        B = null;
    }
}
