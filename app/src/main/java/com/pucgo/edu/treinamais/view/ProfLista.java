package com.pucgo.edu.treinamais.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pucgo.edu.treinamais.databinding.ProfListaBinding;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.ProfessorResponseDto;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfLista extends AppCompatActivity {
    private static final String TAG = "ProfLista";
    private ProfListaBinding B;
    private ProfAdapter profAdapter;
    //private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle saveInstanceBundle){
        super.onCreate(saveInstanceBundle);
        B = ProfListaBinding.inflate(getLayoutInflater());
        setContentView(B.getRoot());

        B.RCVprofLista.setAdapter(new ProfAdapter());
    }
    private void carregarProfs(){
        ApiClient.getInstance(this).getAuthApiService().getProfessores(5).enqueue(new Callback<List<ProfessorResponseDto>>() {
            @Override
            public void onResponse(Call<List<ProfessorResponseDto>> call, Response<List<ProfessorResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<ProfessorResponseDto> profs = response.body();
                    Log.d(TAG, "Treinos recebidos da API: " + profs.size());

                    B.tvSemProfs.setVisibility(View.GONE);
                    profAdapter.setProfs(profs);
                } else {
                    B.tvSemProfs.setVisibility(View.VISIBLE);
                    profAdapter.setProfs(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<ProfessorResponseDto>> call, Throwable t) {

            }
        });
    }
}
