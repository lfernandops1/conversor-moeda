package com.conversor.api.conversor_moeda.DTO.response;

import java.util.Map;
import lombok.Data;

@Data
public class MoedaApiResponse {

  private String base;
  private Map<String, Double> rates;
}
