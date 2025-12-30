package com.example.demo.model.dto.vehicleSpot;

import com.example.demo.enumerators.VehicleSpotCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO lato client per la creazione di un nuovo posto veicolo.
 * <p>Contiene le informazioni necessarie per associare un posto veicolo a un parcheggio.</p>
 * <ul>
 *   <li>{@code parkingLotName} – nome del parcheggio a cui associare il posto veicolo</li>
 *   <li>{@code vehicleSpotCategory} – categoria del posto veicolo {@link VehicleSpotCategory}</li>
 *   <li>{@code quantity} – quantità di posti da creare</li>
 * </ul>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSpotPostDto {


    @NotBlank
    private String parkingLotName;
    @NotBlank
    private String vehicleSpotCategory;
    @Min(1)
    private long quantity;
}
