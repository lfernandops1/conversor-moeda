package com.conversor.api.conversor_moeda.controller;

import com.conversor.api.conversor_moeda.DTO.request.ConversaoRequest;
import com.conversor.api.conversor_moeda.DTO.response.ConversaoResponse;
import com.conversor.api.conversor_moeda.service.ConversorService;
import com.conversor.api.conversor_moeda.service.MoedaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/conversor")
public class ConversorController {

  @Autowired private ConversorService conversorService;
  @Autowired private MoedaService moedaService;

  @PostMapping
  public Mono<ConversaoResponse> converter(@Valid @RequestBody ConversaoRequest requisicao) {
    return conversorService.converterMoeda(requisicao);
  }
}
