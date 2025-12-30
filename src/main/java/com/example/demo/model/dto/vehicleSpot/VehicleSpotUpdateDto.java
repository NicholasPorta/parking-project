package com.example.demo.model.dto.vehicleSpot;

import com.example.demo.enumerators.VehicleSpotCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per l'aggiornamento di un posto veicolo esistente.
 * <p>Permette di aggiornare la categoria del posto veicolo.</p>
 * <ul>
 *   <li>{@code parkingLotId} – ID del parcheggio a cui è associato il posto</li>
 *   <li>{@code parkingLotName} – nome del parcheggio a cui è associato il posto</li>
 *   <li>{@code vehicleSpotCategory} – nuova categoria da assegnare {@link VehicleSpotCategory}</li>
 * </ul>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSpotUpdateDto {

    private String parkingLotId;
    private String parkingLotName;
    private VehicleSpotCategory vehicleSpotCategory;
}
