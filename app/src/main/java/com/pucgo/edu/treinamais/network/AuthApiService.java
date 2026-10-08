package com.pucgo.edu.treinamais.network;

import com.pucgo.edu.treinamais.model.Aluno;
import com.pucgo.edu.treinamais.network.dto.AlunoResponseDto;
import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;
import com.pucgo.edu.treinamais.network.dto.LoginRequestDto;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.network.dto.ProfessorMetricasResponseDto;
import com.pucgo.edu.treinamais.network.dto.RegisterRequestDto;
import com.pucgo.edu.treinamais.network.dto.TreinoResponseDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<AuthResponseDto> login(@Body LoginRequestDto request);

    @POST("api/auth/register")
    Call<MessageResponseDto> register(@Body RegisterRequestDto request);

    @POST("api/auth/refresh")
    Call<AuthResponseDto> refreshToken(@Header("Authorization") String token);

    @POST("api/auth/logout")
    Call<MessageResponseDto> logout(@Header("Authorization") String token);

    @GET("api/auth/me")
    Call<Map<String, String>> getCurrentUser();

    @GET("api/alunos")
    Call<List<Aluno>> getAlunos();

    @GET("api/alunos/me")
    Call<AlunoResponseDto> getMeuPerfilAluno();

    @GET("api/professores/me/metricas")
    Call<ProfessorMetricasResponseDto> getMetricasProfessor();

    @GET("api/treinos")
    Call<List<TreinoResponseDto>> getTreinos();

    @GET("api/treinos/{id}")
    Call<TreinoResponseDto> getTreinoPorId(@Path("id") Long id);
}
