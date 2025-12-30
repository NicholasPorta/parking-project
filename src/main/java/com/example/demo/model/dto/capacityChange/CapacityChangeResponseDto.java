package com.example.demo.model.dto.capacityChange;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO di risposta per lo storico delle modifiche di capacità di un parcheggio
 * <p>Contiene i dati principali del cambiamento:</p>
 * <ul>
 *   <li>{@code parkingLotId} - identificativo univoco del parcheggio</li>
 *   <li>{@code parkingLotName} - nome del parcheggio</li>
 *   <li>{@code oldCapacity} - capacita prima di aggiornarla</li>
 *   <li>{@code newCapacity} - la nuova capacita aggiornata</li>
 *   <li>{@code changeDate} - data e ora della modifica e il formato in cui viene mostrata</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityChangeResponseDto {
    private String parkingLotName;
    private String parkingLotId;
    private Long oldCapacity;
    private Long newCapacity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime changeDate;
}
