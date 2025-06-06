package com.conversor.api.conversor_moeda.service;

import com.conversor.api.conversor_moeda.DTO.request.ConversaoRequest;
import com.conversor.api.conversor_moeda.DTO.response.ConversaoResponse;
import com.conversor.api.conversor_moeda.DTO.response.MoedaApiResponse;
import com.conversor.api.conversor_moeda.config.exception.MoedaNaoEncontradaException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ConversorService {

  @Autowired private WebClient clienteApiConverterMoeda;

  public ConversorService(WebClient clienteApiConverterMoeda) {
    this.clienteApiConverterMoeda = clienteApiConverterMoeda;
  }

  public Mono<ConversaoResponse> converterMoeda(ConversaoRequest requisicao) {
    log.info(
        "ConverterMoeda chamado com moedaOrigem={} e moedaDestino={}",
        requisicao.getMoedaOrigem(),
        requisicao.getMoedaDestino());

    return getTaxasDaApi()
        .flatMap(
            resposta -> {
              Double taxaOrigem = resposta.getRates().get(requisicao.getMoedaOrigem());
              Double taxaDestino = resposta.getRates().get(requisicao.getMoedaDestino());

              log.info("Taxa para moedaOrigem ({}): {}", requisicao.getMoedaOrigem(), taxaOrigem);
              log.info(
                  "Taxa para moedaDestino ({}): {}", requisicao.getMoedaDestino(), taxaDestino);

              if (taxaOrigem == null) {
                return Mono.error(new MoedaNaoEncontradaException(requisicao.getMoedaOrigem()));
              }
              if (taxaDestino == null) {
                return Mono.error(new MoedaNaoEncontradaException(requisicao.getMoedaDestino()));
              }

              double taxaUtilizada = taxaDestino / taxaOrigem;
              double valorConvertido = requisicao.getValor() * taxaUtilizada;

              log.info("Valor convertido: {}", valorConvertido);

              ConversaoResponse respostaDto = new ConversaoResponse();
              respostaDto.setValorConvertido(valorConvertido);
              respostaDto.setTaxaUtilizada(taxaUtilizada);
              respostaDto.setDataConversao(LocalDateTime.now());

              return Mono.just(respostaDto);
            });
  }

  @Cacheable("taxas")
  public Mono<MoedaApiResponse> getTaxasDaApi() {
    return clienteApiConverterMoeda
        .get()
        .uri("/latest/USD")
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            response -> {
              log.error("Erro 4xx ao buscar taxas base USD");
              return Mono.error(new RuntimeException("Erro ao acessar API de moedas"));
            })
        .bodyToMono(MoedaApiResponse.class);
  }
}
