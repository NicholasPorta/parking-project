package com.example.demo.interceptors;



import com.example.demo.utils.DateUtilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class DateValidationAdvice extends RequestBodyAdviceAdapter {

    /**
     * Verifica se il metodo corrente è annotato con {@link com.example.demo.annotations.ValidateAppointmentDate}.
     * Questo permette di applicare una logica personalizzata solo ai metodi che richiedono
     * la validazione della data dell'appuntamento.
     *
     * @param methodParameter il parametro del metodo controller
     * @param targetType il tipo target della conversione
     * @param converterType il tipo di converter HTTP utilizzato
     * @return {@code true} se l'annotazione {@code @ValidateAppointmentDate} è presente, {@code false} altrimenti
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        return methodParameter.hasMethodAnnotation(com.example.demo.annotations.ValidateAppointmentDate.class);
    }

    /**
     * Esegue la validazione personalizzata delle date presenti nel body dopo che è stato deserializzato.
     * Se il body contiene i campi {@code initialDate} e {@code endingDate}, verifica che l'intervallo sia valido
     * rispetto alla zona oraria ricevuta nell'header {@code zoneIdFromClient}.
     *
     * @param body oggetto deserializzato dal JSON
     * @param inputMessage messaggio HTTP in ingresso
     * @param parameter metadati del metodo del controller
     * @param targetType tipo dell'oggetto atteso
     * @param converterType tipo del converter usato
     * @return l'oggetto {@code body} se la validazione è superata
     * @throws RuntimeException se le date sono assenti o non valide
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        if (!doTheHttpBodyHasDateProperties(body)) {
            return body;
        }

        Map<String, Object> fields = Arrays.stream(body.getClass().getDeclaredFields()).map(
                        (field) -> {
                            field.setAccessible(true);
                            return field;
                        })
                .collect(Collectors.toMap(
                        Field::getName,
                        field -> {
                            try {
                                return field.get(body);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                ));
        LocalDateTime initialDate = (LocalDateTime) fields.get(dateFieldNames.START_DATE.getName());
        LocalDateTime endingDate = (LocalDateTime) fields.get(dateFieldNames.ENDING_DATE.getName());
        String zoneID = inputMessage.getHeaders().getFirst("zoneIdFromClient");

        if (DateUtilities.isAppointmentDateValid(initialDate, endingDate, zoneID)) {
            return body;
        } else {
            throw new RuntimeException("please insert a valid date span for appointment ");
        }
    }

    /**
     * Verifica se il body contiene entrambi i campi {@code initialDate} e {@code endingDate}.
     * Utilizza reflection per controllare i campi presenti e non nulli.
     *
     * @param body oggetto deserializzato dal JSON
     * @return {@code true} se entrambi i campi sono presenti e non nulli, {@code false} altrimenti
     */
    private boolean doTheHttpBodyHasDateProperties(Object body) {
        Map<String, Object> fields = Arrays.stream(body.getClass().getDeclaredFields()).map(
                        (field) -> {
                            field.setAccessible(true);
                            return field;
                        })
                .filter(
                        field -> {
                            try {
                                return field.get(body) != null;
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                )
                .collect(Collectors.toMap(
                        Field::getName,
                        field -> {
                            try {
                                return field.get(body);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                ));

        return fields.containsKey(dateFieldNames.START_DATE.getName()) && fields.containsKey(dateFieldNames.ENDING_DATE.getName());
    }

    /**
     * Enum per rappresentare i nomi dei campi data da validare nel body.
     */
    @AllArgsConstructor
    @Getter
    private enum dateFieldNames {
        START_DATE("initialDate"),
        ENDING_DATE("endingDate");

        private final String name;
    }
}
