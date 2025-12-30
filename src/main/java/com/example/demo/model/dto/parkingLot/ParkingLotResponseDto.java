package com.example.demo.model.dto.parkingLot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta per un parcheggio.
 * <p>Contiene i dati principali del parcheggio dopo la creazione o il recupero:</p>
 * <ul>
 *   <li>{@code id} - identificatore univoco del parcheggio</li>
 *   <li>{@code parkingLotName} - nome del parcheggio</li>
 *   <li>{@code vehicleSpotNumber} - numero di posti veicolo disponibili</li>
 * </ul>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLotResponseDto {

    private String id;
    @JsonProperty("name")
    private String parkingLotName;
    private Long vehicleSpotNumber;
}
