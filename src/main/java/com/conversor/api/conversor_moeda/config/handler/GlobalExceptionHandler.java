package com.conversor.api.conversor_moeda.config.handler;

import com.conversor.api.conversor_moeda.DTO.response.ErrorResponse;
import com.conversor.api.conversor_moeda.config.exception.MoedaNaoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MoedaNaoEncontradaException.class)
  public ResponseEntity<ErrorResponse> handleMoedaInvalida(MoedaNaoEncontradaException ex) {
    ErrorResponse response = new ErrorResponse("MOEDA_INVALIDA", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return ResponseEntity.badRequest().body(new ErrorResponse("DADOS_INVALIDOS", errorMessage));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeErrors(
      RuntimeException ex, ServerWebExchange exchange) {
    String path = exchange.getRequest().getURI().getPath();

    if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
      throw ex;
    }

    ErrorResponse response = new ErrorResponse("ERRO_CONVERSAO", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
