package com.example.demo.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotazione personalizzata per abilitare la validazione automatica delle date
 * in un body HTTP tramite {@link org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice}.</p>
 *
 * <p>Applicare questa annotazione a un metodo di controller che riceve un oggetto contenente
 * {@code initialDate} e {@code endingDate}. Il sistema verificherà che le date siano coerenti
 * e valide nel fuso orario specificato nell'header {@code zoneIdFromClient}.</p>
 *
 * <p>Funziona insieme a {@link com.example.demo.interceptors.DateValidationAdvice}.</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateAppointmentDate {
}
