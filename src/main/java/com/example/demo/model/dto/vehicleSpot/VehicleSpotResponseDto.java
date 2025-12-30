package com.example.demo.model.dto.vehicleSpot;

import com.example.demo.enumerators.VehicleSpotCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta per un posto veicolo creato
 * <p>Contiene le informazioni essenziali del posto veicolo generato</p>
 * <ul>
 *   <li>{@code id} – identificativo univoco per il posto</li>
 *   <li>{@code parkingLotId} – ID del parcheggio a cui è associato il posto</li>
 *   <li>{@code parkingLotName} – nome del parcheggio a cui è associato il posto</li>
 *   <li>{@code vehicleSpotCategory} – categoria del posto {@link VehicleSpotCategory}</li>
 * </ul>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSpotResponseDto {

    private String id;
    private String parkingLotId;
    private String parkingLotName;
    private VehicleSpotCategory vehicleSpotCategory;
}
