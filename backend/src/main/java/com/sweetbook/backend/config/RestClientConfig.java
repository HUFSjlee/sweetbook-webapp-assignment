package com.sweetbook.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Qualifier("sweetBookRestClient")
    public RestClient sweetBookRestClient(SweetBookProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    @Qualifier("supabaseRestClient")
    public RestClient supabaseRestClient(SupabaseProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader("apikey", properties.getServiceRoleKey())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getServiceRoleKey())
                .build();
    }
}
