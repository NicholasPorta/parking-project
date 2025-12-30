package com.example.demo.model.dto.parkingLot;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO lato client per la creazione di un nuovo parcheggio
 * <p>Contiene i dati principali per la creazione del parcheggio</p>
 * <ul>
 *   <li>{@code parkingLotName}- nome del parcheggio</li>
 *   <li>{@code vehicleSpotNumber} - numero di posti veicolo disponibili</li>
 * </ul>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLotPostDto {

    @NotBlank
    @JsonProperty("name")
    private String parkingLotName;
    @Min(value = 1, message = "Vehicle spot number must be greater than 0")
    private Long vehicleSpotNumber;
}
