package com.example.demo.model.dto.appointment;

import com.example.demo.enumerators.AppointmentState;
import com.example.demo.enumerators.VehicleSpotCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

/**
 * DTO di risposta per una prenotazione
 * <p>Contiene le informazioni della prenotazione effettuata</p>
 * <ul>
 *   <li>{@code id} – identificativo univoco della prenotazione</li>
 *   <li>{@code parkingLotName} – nome del parcheggio associato alla prenotazione</li>
 *   <li>{@code vehicleSpotCategory} – categoria del posto auto prenotato</li>
 *   <li>{@code initialDate} – data e ora di inizio della prenotazione (formato: {@code dd/MM/yyyy HH:mm})</li>
 *   <li>{@code endingDate} – data e ora di fine della prenotazione (formato: {@code dd/MM/yyyy HH:mm})</li>
 *   <li>{@code appointmentState} – stato attuale della prenotazione {@link AppointmentState}</li>
 * </ul>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDto {


    private String id;

    private String parkingLotName;
    private VehicleSpotCategory vehicleSpotCategory;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime initialDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endingDate;

    private AppointmentState appointmentState = AppointmentState.VALID;
}
