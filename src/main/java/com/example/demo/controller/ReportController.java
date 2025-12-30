package com.example.demo.controller;

import com.example.demo.model.dto.capacityChange.CapacityChangeResponseDto;
import com.example.demo.model.dto.report.ParkingOccupancyResponseDto;
import com.example.demo.model.dto.report.ReservationReportResponseDto;
import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('AMMINISTRATORE')")
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * <p>Restituisce le statistiche di occupazione per tutti i parcheggi</p>
     * @return {@link ResponseEntity} con la lista {@link ParkingOccupancyResponseDto} dei posti occupati
     */
    @GetMapping("/occupancy/by-name/{parkingLotName}")
    public ResponseEntity<?> getOccupancyByName(@PathVariable String parkingLotName) {
        List<ParkingOccupancyResponseDto> all = reportService.getParkingOccupancy();
        return ResponseEntity.ok(
                all.stream()
                        .filter(dto -> dto.getParkingLotName().equalsIgnoreCase(parkingLotName))
                        .findFirst()
                        .orElse(null)
        );
    }


    /**
     * Restituisce la % delle prenotazioni cancellate sul totale delle prenotazioni effettuate
     * @return {@link ResponseEntity} con la % delle cancellazioni su tutte le prenotazioni esistenti
     */
    @GetMapping("/cancellation-rate")
    public ResponseEntity<?> getCancellationRate() {
        return ResponseEntity.ok(reportService.getCancellationRate());
    }

    /**
     * <p>Restituisce le statistiche di occupazione giornaliere per il parcheggio indicato</p>
     * @param parkingLotName nome del parcheggio
     * @return {@link ResponseEntity} con la lista {@link ReservationReportResponseDto} con il numero totale di prenotazioni per giorno
     */
    @GetMapping("/daily/by-name/{parkingLotName}")
    public ResponseEntity<?> getDailyReportByName(@PathVariable String parkingLotName) {
        return ResponseEntity.ok(reportService.getDailyReportByName(parkingLotName));
    }

    /**
     * <p>Restituisce le statistiche di occupazione settimanali per il parcheggio indicato</p>
     * @param parkingLotName nome del parcheggio
     * @return {@link ResponseEntity} con la lista {@link ReservationReportResponseDto} con il numero totale di prenotazioni settimanali
     */
    @GetMapping("/week/by-name/{parkingLotName}")
    public ResponseEntity<?> getWeekReportByName(@PathVariable String parkingLotName) {
        return ResponseEntity.ok(reportService.getWeekReportByName(parkingLotName));
    }

    /**
     * <p>Restituisce le statistiche di occupazione mensili per il parcheggio indicato</p>
     * @param parkingLotName nome del parcheggio
     * @return {@link ResponseEntity} con la lista {@link ReservationReportResponseDto} con il numero totale di prenotazioni mensili
     */
    @GetMapping("/monthly/by-name/{parkingLotName}")
    public ResponseEntity<?> getMonthReportByName(@PathVariable String parkingLotName) {
        return ResponseEntity.ok(reportService.getMonthReportByName(parkingLotName));
    }

    /**
     * <p>Restituisce lo storico delle prenotazioni per un determinato parcheggio</p>
     * @param parkingLotName nome del parcheggio
     * @return lista {@link ReservationReportResponseDto} con le informazioni storiche delle prenotazioni
     */
    @GetMapping("/history/by-name/{parkingLotName}")
    public List<ReservationReportResponseDto> getReportHistoryByParkingLotName(@PathVariable String parkingLotName) {
        return reportService.getReportHistoryByParkingLotName(parkingLotName);
    }

    /**
     * <p>Restituisce lo storico delle modifiche alla capacità di un parcheggio</p>
     * @param parkingLotName parametro richiesto per la ricerca
     * @return {@link ResponseEntity} con la lista di {@link CapacityChangeResponseDto} con le informazioni
     */
    @GetMapping("/capacity-changes/by-name/{parkingLotName}")
    public ResponseEntity<List<CapacityChangeResponseDto>> getCapacityChangeHistoryByName(@PathVariable String parkingLotName) {
        return ResponseEntity.ok(reportService.getCapacityChangeHistoryByName(parkingLotName));
    }
}
