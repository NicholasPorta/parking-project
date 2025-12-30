package com.example.demo.model.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO contenente l'intervallo di date per il controllo disponibilità
 * <p>Utilizzato per verificare la disponibilità dei posti veicolo in un determinato periodo</p>
 * <ul>
 *   <li>{@code initialDate} – data e ora di inizio (formato: {@code dd/MM/yyyy HH:mm})</li>
 *   <li>{@code endingDate} – data e ora di fine (formato: {@code dd/MM/yyyy HH:mm})</li>
 * </ul>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDatesDto {

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime initialDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endingDate;

}
