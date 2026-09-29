package com.pucgo.edu.treinamais.network;

import com.pucgo.edu.treinamais.network.dto.AuthResponseDto;
import com.pucgo.edu.treinamais.network.dto.LoginRequestDto;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.network.dto.RegisterRequestDto;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

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
}
