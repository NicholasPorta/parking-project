package com.example.demo.model.dto.appointment;


import com.example.demo.enumerators.VehicleSpotCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO lato client per la creazione di una nuova prenotazione
 * <p>Contiene i dati necessari per effettuare una prenotazione su uno specifico posto veicolo</p>
 * <ul>
 *   <li>{@code parkingLotName} – nome del parcheggio in cui si desidera effettuare la prenotazione</li>
 *   <li>{@code vehicleSpotCategory} – categoria del posto veicolo (ad esempio, auto, moto, ecc.)</li>
 *   <li>{@code initialDate} – data e ora di inizio della prenotazione (formato: {@code dd/MM/yyyy HH:mm})</li>
 *   <li>{@code endingDate} – data e ora di fine della prenotazione (formato: {@code dd/MM/yyyy HH:mm})</li>
 * </ul>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentPostDto {

    @NotBlank
    private String parkingLotName;

    @NotNull
    private VehicleSpotCategory vehicleSpotCategory;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime initialDate;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endingDate;
}

