package com.conversor.api.conversor_moeda;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching
public class ConversorMoedaApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConversorMoedaApplication.class, args);
    log.info("✅ ConversorMoedaApplication iniciado com sucesso!");
  }
}
