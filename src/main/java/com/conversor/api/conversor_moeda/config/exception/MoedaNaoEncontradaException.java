package com.conversor.api.conversor_moeda.config.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoedaNaoEncontradaException extends RuntimeException {
  public MoedaNaoEncontradaException(String moeda) {
    super("Moeda não suportada: " + moeda);
  }
}
