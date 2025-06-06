package com.conversor.api.conversor_moeda.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@DisplayName("Testes do Serviço de Moedas")
class MoedaServiceTest {

  private static WireMockServer wireMockServer;
  private MoedaService moedaService;

  private static final String API_RESPONSE_JSON =
      """
            {
                "base": "USD",
                "rates": {
                    "BRL": 5.25,
                    "EUR": 0.95,
                    "JPY": 150.0
                }
            }""";

  private static final int WIREMOCK_PORT = 0; // porta dinâmica

  @BeforeAll
  static void setupServer() {
    wireMockServer = new WireMockServer(wireMockConfig().port(WIREMOCK_PORT).containerThreads(10));
    wireMockServer.start();
  }

  private int getWireMockPort() {
    return wireMockServer.port();
  }

  @BeforeEach
  void setup() {
    WebClient webClient =
        WebClient.builder().baseUrl("http://localhost:" + getWireMockPort()).build();

    moedaService = new MoedaService(webClient);
  }

  @AfterEach
  void reset() {
    wireMockServer.resetAll();
  }

  @Test
  @DisplayName("Deve carregar moedas válidas com sucesso")
  void testCarregarMoedas_Success() {

    wireMockServer.stubFor(
        get(urlEqualTo("/latest/USD"))
            .willReturn(
                okJson(API_RESPONSE_JSON)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

    moedaService.carregarMoedas();

    assertThat(moedaService.isMoedaValida("USD")).isTrue();
    assertThat(moedaService.isMoedaValida("BRL")).isTrue();
    assertThat(moedaService.isMoedaValida("EUR")).isTrue();
    assertThat(moedaService.isMoedaValida("JPY")).isTrue();
    assertThat(moedaService.isMoedaValida("XYZ")).isFalse();
  }

  @Test
  @DisplayName("Deve usar moedas padrão quando API retorna erro")
  void testCarregarMoedas_ApiError() {

    wireMockServer.stubFor(
        get(urlEqualTo("/latest/USD"))
            .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

    moedaService.carregarMoedas();

    assertThat(moedaService.isMoedaValida("USD")).isTrue();
    assertThat(moedaService.isMoedaValida("BRL")).isTrue();
    assertThat(moedaService.isMoedaValida("EUR")).isTrue();
    assertThat(moedaService.isMoedaValida("JPY")).isFalse(); // Não está nas moedas padrão
  }

  @Test
  @DisplayName("Deve usar moedas padrão quando resposta é nula")
  void testCarregarMoedas_NullResponse() {

    wireMockServer.stubFor(get(urlEqualTo("/latest/USD")).willReturn(okJson("{}")));

    moedaService.carregarMoedas();

    // Verifica as moedas padrão
    assertThat(moedaService.isMoedaValida("USD")).isTrue();
    assertThat(moedaService.isMoedaValida("BRL")).isTrue();
    assertThat(moedaService.isMoedaValida("EUR")).isTrue();
  }

  @Test
  @DisplayName("Deve considerar moeda válida quando código está em maiúsculas")
  void testIsMoedaValida_CaseInsensitive() {

    wireMockServer.stubFor(get(urlEqualTo("/latest/USD")).willReturn(okJson(API_RESPONSE_JSON)));

    moedaService.carregarMoedas();

    assertThat(moedaService.isMoedaValida("brl")).isTrue();
    assertThat(moedaService.isMoedaValida("BrL")).isTrue();
    assertThat(moedaService.isMoedaValida("EUR")).isTrue();
  }
}
