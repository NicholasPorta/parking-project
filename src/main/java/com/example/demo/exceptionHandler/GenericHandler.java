package com.example.demo.exceptionHandler;

import com.example.demo.model.pojo.ExceptionInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Gestore globale delle eccezioni per l'applicazione.
 * <p>
 * Utilizza l'annotazione {@link RestControllerAdvice} per intercettare e gestire
 * le eccezioni lanciate dai controller REST, restituendo una risposta strutturata
 * al client tramite {@link ExceptionInfo}.
 * </p>
 * <p>
 * Ogni eccezione gestita viene trasformata in un oggetto {@link ExceptionInfo}
 * contenente:
 * <ul>
 *     <li>{@code message} – messaggio dell'eccezione</li>
 *     <li>{@code details} – stack trace in formato stringa</li>
 *     <li>{@code exceptionClass} – nome della classe dell'eccezione</li>
 *     <li>{@code date} – data e ora dell'errore in formato {@code dd/MM/yyyy HH:mm}</li>
 * </ul>
 * Lo status HTTP restituito è {@link HttpStatus#BAD_REQUEST} per tutti i metodi.
 * </p>
 */

@RestControllerAdvice
public class GenericHandler {

    /**
     * Gestisce tutte le eccezioni di tipo {@link RuntimeException}.
     *
     * @param e eccezione catturata
     * @return {@link ResponseEntity} contenente le informazioni dell'errore in {@link ExceptionInfo}
     * e status HTTP {@code 400 Bad Request}
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ExceptionInfo> runtimeExceptionHandler(RuntimeException e){
        String dateFormat = ("dd/MM/yyyy HH:mm");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        String date = formatter.format(LocalDateTime.now());
        ExceptionInfo exceptionInfo = ExceptionInfo.builder()
                .message(e.getMessage() != null ? e.getMessage() : "message not available ")
                .details(Arrays.toString(e.getStackTrace()))
                .exceptionClass(e.getClass().getSimpleName())
                .date(date)
                .build();
        return new ResponseEntity<>(exceptionInfo, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestisce le eccezioni di tipo {@link MethodArgumentNotValidException}
     * generate dalla validazione dei parametri di input nei controller.
     *
     * @param e eccezione catturata
     * @return {@link ResponseEntity} contenente le informazioni dell'errore in {@link ExceptionInfo}
     * e status HTTP {@code 400 Bad Request}
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionInfo> methodArgumentNotValidHandler(MethodArgumentNotValidException e){
        String dateFormat = ("dd/MM/yyyy HH:mm");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        String date = formatter.format(LocalDateTime.now());
        ExceptionInfo exceptionInfo = ExceptionInfo.builder()
                .message(e.getMessage())
                .exceptionClass(e.getClass().getSimpleName())
                .details(Arrays.toString(e.getStackTrace()))
                .date(date)
                .build();
        return new ResponseEntity<>(exceptionInfo, HttpStatus.BAD_REQUEST);
    }




}
