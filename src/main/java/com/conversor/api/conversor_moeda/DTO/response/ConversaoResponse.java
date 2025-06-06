package com.conversor.api.conversor_moeda.DTO.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConversaoResponse {
  private double valorConvertido;
  private double taxaUtilizada;
  private LocalDateTime dataConversao;
}
