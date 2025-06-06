package com.conversor.api.conversor_moeda.service;

import com.conversor.api.conversor_moeda.DTO.response.MoedaApiResponse;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MoedaService {

  @Autowired private WebClient clienteApiMoedas;

  public MoedaService(WebClient clienteApiMoedas) {
    this.clienteApiMoedas = clienteApiMoedas;
  }

  private final Set<String> moedasValidas = new HashSet<>();

  @PostConstruct
  public void carregarMoedas() {
    log.info("Inicializando MoedaService - carregando moedas...");

    try {
      Mono<MoedaApiResponse> responseMono =
          this.clienteApiMoedas
              .get()
              .uri("/latest/USD") // mantido para a API antiga
              .retrieve()
              .onStatus(
                  status -> !status.is2xxSuccessful(),
                  clientResponse -> {
                    log.warn(
                        "Status HTTP não OK ao carregar moedas: {}", clientResponse.statusCode());
                    return Mono.error(new RuntimeException("Erro na API de moedas"));
                  })
              .bodyToMono(MoedaApiResponse.class)
              .doOnError(e -> log.error("Erro na chamada da API de moedas: {}", e.getMessage()));

      MoedaApiResponse response = responseMono.block();

      if (response == null) {
        log.warn("Resposta da API veio nula. Usando moedas padrão.");
        moedasValidas.clear();
        moedasValidas.addAll(Set.of("USD", "BRL", "EUR"));
        return;
      }

      log.info(
          "Resposta da API obtida com sucesso: base={}, total rates={}",
          response.getBase(),
          response.getRates().size());

      moedasValidas.clear();
      moedasValidas.addAll(response.getRates().keySet());
      moedasValidas.add(response.getBase());

      log.info("Moedas carregadas: {}", moedasValidas.size());
    } catch (Exception e) {
      log.error("Erro ao carregar moedas: {}", e.getMessage());
      moedasValidas.clear();
      moedasValidas.addAll(Set.of("USD", "BRL", "EUR"));
    }
  }

  public boolean isMoedaValida(String codigo) {
    return moedasValidas.contains(codigo.toUpperCase());
  }
}
