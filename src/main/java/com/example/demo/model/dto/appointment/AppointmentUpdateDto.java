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
 * DTO per l'aggiornamento di una prenotazione esistente
 * <p>Permette di modificare le informazioni relative alla prenotazione</p>
 * <ul>
 *   <li>{@code parkingLotName} – nomedel parcheggio</li>
 *   <li>{@code vehicleSpotCategory} – categoria del posto auto (es. AUTO, MOTO, BICI)</li>
 *   <li>{@code initialDate} – nuova data e ora di inizio della prenotazione (formato: {@code dd/MM/yyyy HH:mm})</li>
 *   <li>{@code endingDate} – nuova data e ora di fine della prenotazione (formato: {@code dd/MM/yyyy HH:mm})</li>
 *   <li>{@code appointmentState} – nuovo stato della prenotazione {@link AppointmentState}</li>
 * </ul>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUpdateDto {

    private String parkingLotName;
    private VehicleSpotCategory vehicleSpotCategory;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime initialDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endingDate;
    private AppointmentState appointmentState;
}
