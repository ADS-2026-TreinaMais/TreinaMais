package com.pucgo.edu.treinamais.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pucgo.edu.treinamais.security.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = "ApiClient";
    public static final String EMULATOR_BASE_URL = "http://10.0.2.2:8081/";
    public static final String DEVICE_BASE_URL = "http://127.0.0.1:8081/";

    public static boolean isEmulator() {
        return (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.HARDWARE.contains("goldfish")
                || android.os.Build.HARDWARE.contains("ranchu")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || android.os.Build.PRODUCT.contains("sdk_google")
                || android.os.Build.PRODUCT.contains("google_sdk")
                || android.os.Build.PRODUCT.contains("sdk")
                || android.os.Build.PRODUCT.contains("sdk_x86")
                || android.os.Build.PRODUCT.contains("vbox86p")
                || android.os.Build.PRODUCT.contains("emulator")
                || android.os.Build.PRODUCT.contains("simulator");
    }

    public static String getBaseUrl(Context context) {
        String defaultUrl = isEmulator() ? EMULATOR_BASE_URL : DEVICE_BASE_URL;
        return context.getSharedPreferences("api_config", Context.MODE_PRIVATE)
                .getString("base_url", defaultUrl);
    }

    private static volatile ApiClient instance;
    private final AuthApiService authApiService;
    private final Retrofit retrofit;

    private ApiClient(Context context) {
        SessionManager sessionManager = SessionManager.getInstance(context);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder();

                String token = sessionManager.getToken();
                if (token != null && !token.trim().isEmpty() && originalRequest.header("Authorization") == null) {
                    builder.header("Authorization", "Bearer " + token);
                }

                Response response = chain.proceed(builder.build());

                if (response.code() == 401) {
                    Log.w(TAG, "Resposta HTTP 401 detectada! Token expirado ou revogado.");
                    if (!originalRequest.url().encodedPath().contains("/login") &&
                            !originalRequest.url().encodedPath().contains("/register") &&
                            !originalRequest.url().encodedPath().contains("/cadastro")) {
                        sessionManager.notifySessionExpired();
                    }
                }

                return response;
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(context))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.authApiService = retrofit.create(AuthApiService.class);
    }

    public static ApiClient getInstance(Context context) {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public AuthApiService getAuthApiService() {
        return authApiService;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
