package com.conversor.api.conversor_moeda.config.validation.anotation;

import com.conversor.api.conversor_moeda.config.validation.validators.MoedaValidaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MoedaValidaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MoedaValida {

  String message() default "Moeda inválida";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
