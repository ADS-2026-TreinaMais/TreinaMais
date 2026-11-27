package com.pucgo.edu.treinamais.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucgo.edu.treinamais.databinding.ProfListaBinding;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.AlunoResponseDto;
import com.pucgo.edu.treinamais.network.dto.ProfessorResponseDto;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfLista extends AppCompatActivity {
    private static final String TAG = "ProfLista";
    /** Quando true, exibe apenas o professor vinculado ao aluno (somente leitura). */
    public static final String EXTRA_SOMENTE_MEU_PROF = "somente_meu_prof";

    private ProfListaBinding B;
    private ProfAdapter profAdapter;
    private boolean atribuindo = false;

    @Override
    protected void onCreate(Bundle saveInstanceBundle){
        super.onCreate(saveInstanceBundle);
        B = ProfListaBinding.inflate(getLayoutInflater());
        setContentView(B.getRoot());

        boolean somenteMeuProf = getIntent().getBooleanExtra(EXTRA_SOMENTE_MEU_PROF, false);

        profAdapter = new ProfAdapter();
        B.RCVprofLista.setLayoutManager(new LinearLayoutManager(this));
        B.RCVprofLista.setAdapter(profAdapter);

        if (somenteMeuProf) {
            carregarMeuProf();
        } else {
            profAdapter.setOnProfClickListener(this::atribuirProfessor);
            carregarProfs();
        }
    }

    private void mostrarMensagem(String mensagem){
        profAdapter.setProfs(Collections.emptyList());
        B.RCVprofLista.setVisibility(View.GONE);
        B.tvSemProfs.setText(mensagem);
        B.tvSemProfs.setVisibility(View.VISIBLE);
    }

    private void carregarMeuProf(){
        ApiClient.getInstance(this).getAuthApiService().getMeuPerfilAluno().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AlunoResponseDto> call, @NonNull Response<AlunoResponseDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Falha ao consultar api/alunos/me: HTTP " + response.code());
                    mostrarMensagem("Não foi possível carregar seu professor.");
                    return;
                }
                Long professorId = response.body().getProfessorId();
                if (professorId == null) {
                    mostrarMensagem("Você ainda não tem um professor vinculado.");
                    return;
                }
                buscarProfessor(professorId);
            }

            @Override
            public void onFailure(@NonNull Call<AlunoResponseDto> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro ao consultar api/alunos/me: " + t.getMessage(), t);
                mostrarMensagem("Erro de rede ao carregar seu professor.");
            }
        });
    }

    private void buscarProfessor(Long professorId){
        ApiClient.getInstance(this).getAuthApiService().getProfessorPorId(professorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ProfessorResponseDto> call, @NonNull Response<ProfessorResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    B.tvSemProfs.setVisibility(View.GONE);
                    B.RCVprofLista.setVisibility(View.VISIBLE);
                    profAdapter.setProfs(Collections.singletonList(response.body()));
                } else {
                    Log.e(TAG, "Falha ao consultar api/professores/" + professorId + ": HTTP " + response.code());
                    mostrarMensagem("Não foi possível carregar seu professor.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfessorResponseDto> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro ao consultar api/professores/" + professorId + ": " + t.getMessage(), t);
                mostrarMensagem("Erro de rede ao carregar seu professor.");
            }
        });
    }

    private void carregarProfs(){
        ApiClient.getInstance(this).getAuthApiService().getProfessores(5).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfessorResponseDto>> call, @NonNull Response<List<ProfessorResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()){
                    List<ProfessorResponseDto> profs = response.body();
                    Log.d(TAG, "Professores recebidos da API: " + profs.size());

                    B.tvSemProfs.setVisibility(View.GONE);
                    B.RCVprofLista.setVisibility(View.VISIBLE);
                    profAdapter.setProfs(profs);
                } else {
                    Log.e(TAG, "Falha ao consultar api/professores: HTTP " + response.code());
                    B.RCVprofLista.setVisibility(View.GONE);
                    B.tvSemProfs.setVisibility(View.VISIBLE);
                    profAdapter.setProfs(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfessorResponseDto>> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro ao consultar api/professores: " + t.getMessage(), t);
                B.RCVprofLista.setVisibility(View.GONE);
                B.tvSemProfs.setVisibility(View.VISIBLE);
            }
        });
    }

    private void atribuirProfessor(ProfessorResponseDto prof){
        if (atribuindo) return; // evita requisições duplicadas por cliques repetidos
        atribuindo = true;

        ApiClient.getInstance(this).getAuthApiService().escolherProfessor(prof.getId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AlunoResponseDto> call, @NonNull Response<AlunoResponseDto> response) {
                atribuindo = false;
                if (response.isSuccessful() && response.body() != null){
                    AlunoResponseDto aluno = response.body();
                    Log.d(TAG, "Professor atribuído: professorId=" + aluno.getProfessorId());
                    Toast.makeText(ProfLista.this, "Professor " + prof.getNome() + " selecionado!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.e(TAG, "Falha ao atribuir professor: HTTP " + response.code());
                    Toast.makeText(ProfLista.this, "Não foi possível selecionar o professor. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AlunoResponseDto> call, @NonNull Throwable t) {
                atribuindo = false;
                Log.e(TAG, "Erro ao atribuir professor: " + t.getMessage(), t);
                Toast.makeText(ProfLista.this, "Erro de rede ao selecionar professor.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
