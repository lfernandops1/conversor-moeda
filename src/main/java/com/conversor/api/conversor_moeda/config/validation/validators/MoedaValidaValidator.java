package com.conversor.api.conversor_moeda.config.validation.validators;

import com.conversor.api.conversor_moeda.config.validation.anotation.MoedaValida;
import com.conversor.api.conversor_moeda.service.MoedaService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MoedaValidaValidator implements ConstraintValidator<MoedaValida, String> {
  private final MoedaService moedaService;

  public MoedaValidaValidator(MoedaService moedaService) {
    this.moedaService = moedaService;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return false;
    return moedaService.isMoedaValida(value);
  }
}
