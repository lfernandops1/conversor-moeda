package com.conversor.api.conversor_moeda.DTO.response;

import com.conversor.api.conversor_moeda.DTO.SymbolData;
import java.util.Map;
import lombok.Data;

@Data
public class SymbolApiResponse {
  private Boolean success;
  private Map<String, SymbolData> symbols;
}
