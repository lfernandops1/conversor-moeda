package com.conversor.api.conversor_moeda.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.conversor.api.conversor_moeda.DTO.request.ConversaoRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

@DisplayName("Testes do Serviço de Conversão de Moedas")
class ConversorServiceTest {

  private static final int WIREMOCK_PORT = 8089;
  private static WireMockServer wireMockServer;

  private static final String USD_BRL_JSON =
      """
            {
              "rates": {
                "USD": 1.0,
                "BRL": 5.25,
                "EUR": 0.95
              }
            }""";

  private ConversorService conversorService;

  @BeforeAll
  static void setupServer() {
    wireMockServer = new WireMockServer(wireMockConfig().port(WIREMOCK_PORT).containerThreads(10));
    wireMockServer.start();
  }

  @BeforeEach
  void setup() {
    WebClient webClient = WebClient.builder().baseUrl("http://localhost:" + WIREMOCK_PORT).build();

    conversorService = new ConversorService(webClient);
  }

  @Test
  @DisplayName("Deve converter moeda com sucesso quando taxas estão disponíveis")
  void converterMoedaComSucesso() {

    stubForApiResponse(USD_BRL_JSON);
    ConversaoRequest req = criarRequest("USD", "BRL", 10.0);

    StepVerifier.create(conversorService.converterMoeda(req))
        .assertNext(
            response -> {
              assertThat(response.getValorConvertido()).isEqualTo(52.5);
              assertThat(response.getTaxaUtilizada()).isEqualTo(5.25);
              assertThat(response.getDataConversao())
                  .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
            })
        .verifyComplete();
  }

  private void stubForApiResponse(String jsonBody) {
    wireMockServer.stubFor(get(urlEqualTo("/latest/USD")).willReturn(okJson(jsonBody)));
  }

  @Test
  @DisplayName("Deve lançar exceção quando API retorna erro 4xx")
  void testErroNaApiExterna() {

    wireMockServer.stubFor(get(urlEqualTo("/latest/USD")).willReturn(aResponse().withStatus(404)));

    StepVerifier.create(conversorService.converterMoeda(criarRequestExemplo()))
        .expectErrorSatisfies(
            throwable -> {
              assertThat(throwable)
                  .isInstanceOf(RuntimeException.class)
                  .hasMessageContaining("Erro ao acessar API de moedas");
            })
        .verify();
  }

  @Test
  @DisplayName("Deve calcular corretamente quando moeda origem não é USD")
  void testConversaoComMoedaOrigemDiferenteDeUSD() {
    // Given
    String jsonResponse =
        """
                {
                  "rates": {
                    "USD": 1.0,
                    "BRL": 5.25,
                    "EUR": 0.95,
                    "JPY": 150.0
                  }
                }""";

    stubForApiResponse(jsonResponse);
    ConversaoRequest req = criarRequest("EUR", "JPY", 100.0);

    StepVerifier.create(conversorService.converterMoeda(req))
        .assertNext(
            response -> {
              assertThat(response.getValorConvertido()).isEqualTo(15789.47, within(0.01));
            })
        .verifyComplete();
  }

  private ConversaoRequest criarRequest(String moedaOrigem, String moedaDestino, double valor) {
    ConversaoRequest req = new ConversaoRequest();
    req.setMoedaOrigem(moedaOrigem);
    req.setMoedaDestino(moedaDestino);
    req.setValor(valor);
    return req;
  }

  private ConversaoRequest criarRequestExemplo() {
    return criarRequest("USD", "BRL", 10.0);
  }
}
