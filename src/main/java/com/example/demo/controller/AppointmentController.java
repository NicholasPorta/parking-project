package com.example.demo.controller;

import com.example.demo.annotations.ValidateAppointmentDate;
import com.example.demo.enumerators.VehicleSpotCategory;
import com.example.demo.model.dto.appointment.AppointmentDatesDto;
import com.example.demo.model.dto.appointment.AppointmentPostDto;
import com.example.demo.model.dto.appointment.AppointmentUpdateDto;
import com.example.demo.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * <p>Crea una nuova prenotazione per un posto veicolo.</p>
     * <p>Valida le date ricevute e verifica la disponibilità dello slot richiesto.</p>
     *
     * @param dto dati della prenotazione ({@link AppointmentPostDto})
     * @param zoneIdFromClient zona oraria del client (default: Europe/Rome)
     * @return {@link ResponseEntity} contenente la prenotazione creata o errore se non disponibile
     */
    @PostMapping
    @ValidateAppointmentDate
    public ResponseEntity<?> postAppointment(
            @RequestBody AppointmentPostDto dto,
            @RequestHeader(value = "zoneIdFromClient", defaultValue = "Europe/Rome") String zoneIdFromClient) {
        return appointmentService.postAppointment(dto, zoneIdFromClient)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Unable to post appointment"));
    }

    /**
     * <p>Restituisce tutti i posti veicolo disponibili per una determinata categoria e intervallo di date.</p>
     *
     * @param dto intervallo di date ({@link AppointmentDatesDto})
     * @param category categoria del posto veicolo ({@link VehicleSpotCategory})
     * @param zoneIdFromClient zona oraria del client (default: Europe/Rome)
     * @return {@link ResponseEntity} contenente la lista di posti disponibili
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @PostMapping("/availableSpots")
    @ValidateAppointmentDate
    public ResponseEntity<?> getAllAvailableVehicleSpots(
            @RequestBody AppointmentDatesDto dto,
            @RequestParam String category,
            @RequestHeader(value = "zoneIdFromClient", defaultValue = "Europe/Rome") String zoneIdFromClient) {
        return ResponseEntity.ok(
                appointmentService.getAvailableVehicleSpotByCategory(category, dto, zoneIdFromClient)
        );
    }

    /**
     * <p>Restituisce tutte le prenotazioni registrate.</p>
     *
     * @param zoneIdFromClient zona oraria del client (default: Europe/Rome)
     * @return {@link ResponseEntity} contenente la lista delle prenotazioni o errore se vuota
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllAppointments(
            @RequestHeader(value = "zoneIdFromClient", defaultValue = "Europe/Rome") String zoneIdFromClient) {
        return appointmentService.getAllAppointments(zoneIdFromClient)
                .stream()
                .findAny()
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>("No record found in the database", HttpStatus.NOT_FOUND));
    }

    /**
     * <p>Recupera una prenotazione tramite il suo ID.</p>
     *
     * @param id ID della prenotazione
     * @param zoneIdFromClient zona oraria del client (default: Europe/Rome)
     * @return {@link ResponseEntity} contenente la prenotazione trovata o errore se non esiste
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable String id,
            @RequestHeader(value = "zoneIdFromClient", defaultValue = "Europe/Rome") String zoneIdFromClient) {
        return appointmentService.getAppointmentById(id, zoneIdFromClient)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Appointment not found with given id: " + id));
    }

    /**
     * <p>Aggiorna i dati di una prenotazione esistente.</p>
     *
     * @param id ID della prenotazione
     * @param dto nuovi dati della prenotazione ({@link AppointmentUpdateDto})
     * @param zoneIdFromClient zona oraria del client (default: Europe/Rome)
     * @return {@link ResponseEntity} contenente la prenotazione aggiornata
     */
    @PutMapping("/{id}")
    @ValidateAppointmentDate
    public ResponseEntity<?> updateAppointmentById(
            @PathVariable String id,
            @RequestBody AppointmentUpdateDto dto,
            @RequestHeader(value = "zoneIdFromClient", defaultValue = "Europe/Rome") String zoneIdFromClient) {
        return appointmentService.updateAppointmentById(id, dto, zoneIdFromClient)
                .map(ResponseEntity::ok)
                .get();
    }

    /**
     * <p>Annulla una prenotazione impostando lo stato su {@code CANCELED}.</p>
     *
     * @param id ID della prenotazione
     * @param zoneIdFromClient zona oraria del client (default: Europe/Rome)
     * @return {@link ResponseEntity} contenente la prenotazione aggiornata
     */
    @PutMapping("/cancelAppointment/{id}")
    public ResponseEntity<?> cancelAppointmentById(
            @PathVariable String id,
            @RequestHeader(value = "zoneIdFromClient", defaultValue = "Europe/Rome") String zoneIdFromClient) {
        return appointmentService.updateAppointmentStateToCancel(id, zoneIdFromClient)
                .map(ResponseEntity::ok)
                .get();
    }

    /**
     * <p>Elimina una prenotazione tramite il suo ID.</p>
     *
     * @param id ID della prenotazione
     * @return {@link ResponseEntity} con messaggio di conferma o errore
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointmentById(@PathVariable String id) {
        if (appointmentService.deleteAppointmentById(id)) {
            return ResponseEntity.ok("Record deleted successfully");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
