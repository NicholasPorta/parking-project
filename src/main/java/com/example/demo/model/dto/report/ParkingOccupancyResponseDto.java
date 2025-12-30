package com.example.demo.model.dto.report;

import com.example.demo.enumerators.VehicleSpotCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO di risposta per le statistiche di occupazione di un parcheggio.
 * <p>Contiene i dati globali del parcheggio e un dettaglio per categoria.</p>
 * <ul>
 *   <li>{@code parkingLotId} – identificativo univoco del parcheggio</li>
 *   <li>{@code parkingLotName} – nome del parcheggio</li>
 *   <li>{@code totalSlots} – numero totale di posti veicolo</li>
 *   <li>{@code occupiedSlots} – numero di posti attualmente occupati</li>
 *   <li>{@code availableSlots} – numero di posti ancora disponibili</li>
 *   <li>{@code categoryBreakdown} – mappa con dettaglio per {@link VehicleSpotCategory},
 *   contenente {@code total}, {@code occupied} e {@code available} per ciascuna categoria</li>
 * </ul>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingOccupancyResponseDto {
    private String parkingLotId;
    private String parkingLotName;
    private long totalSlots;
    private long occupiedSlots;
    private long availableSlots;
    private Map<VehicleSpotCategory, Map<String, Long>> categoryBreakdown;
}
