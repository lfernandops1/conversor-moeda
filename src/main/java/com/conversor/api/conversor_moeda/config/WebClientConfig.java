package com.conversor.api.conversor_moeda.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {
  @Bean
  public WebClient clienteApiMoedas() {
    return WebClient.builder().baseUrl("https://api.exchangerate-api.com/v4").build();
  }
}
