package com.conversor.api.conversor_moeda.DTO.request;

import com.conversor.api.conversor_moeda.config.validation.anotation.MoedaValida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConversaoRequest {

  @NotBlank(message = "Moeda de origem é obrigatória")
  @MoedaValida
  private String moedaOrigem;

  @NotBlank(message = "Moeda de destino é obrigatória")
  @MoedaValida
  private String moedaDestino;

  @Positive(message = "Valor deve ser positivo")
  private double valor;
}
