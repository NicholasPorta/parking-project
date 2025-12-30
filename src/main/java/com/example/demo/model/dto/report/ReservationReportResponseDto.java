package com.example.demo.model.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta per i report delle prenotazioni
 * <p>Contiene il periodo e il numero totale di prenotazioni effettuate.</p>
 * <ul>
 *   <li>{@code period} – periodo di tempo considerato (giorno, settimana, mese o intervallo personalizzato)</li>
 *   <li>{@code totalReservations} – numero totale di prenotazioni effettuate nel tempo</li>
 * </ul>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationReportResponseDto {
    private String period;
    private long totalReservations;
}
