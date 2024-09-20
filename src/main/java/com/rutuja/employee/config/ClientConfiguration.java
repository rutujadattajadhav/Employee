package com.rutuja.employee.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {


    @Bean()
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().observationRegistry(ObservationRegistry.NOOP);
    }
}
