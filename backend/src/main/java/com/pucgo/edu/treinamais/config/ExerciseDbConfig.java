package com.pucgo.edu.treinamais.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ExerciseDbConfig {

    @Bean(name = "exerciseDbRestClient")
    public RestClient exerciseDbRestClient(
            RestClient.Builder builder,
            @Value("${exercisedb.api.base-url:https://oss.exercisedb.dev/api/v1}") String baseUrl,
            @Value("${exercisedb.api.timeout-ms:5000}") int timeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
