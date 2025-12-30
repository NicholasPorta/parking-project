package com.example.demo.controller;


import com.example.demo.model.dto.parkingLot.ParkingLotPostDto;
import com.example.demo.model.dto.parkingLot.ParkingLotResponseDto;
import com.example.demo.service.ParkingLotService;



import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/parkingLot")
public class ParkingLotController {
    @Autowired
    private ParkingLotService parkingLotService;

    //CREATE

    /**
     *<p>Aggiunge un nuovo parcheggio </p>
     * @param dto informazioni {@link ParkingLotPostDto} per la creazione del parcheggio
     * @return {@link ResponseEntity} con le informazioni {@link ParkingLotPostDto}
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @PostMapping
    public ResponseEntity<?> postParkingLot(@Valid @RequestBody ParkingLotPostDto dto) {
        return parkingLotService.postParkingLot(dto).map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("failed to create a new parking lot")
        );
    };

    /**
     * <p>Restituisce tutti i parcheggi esistenti</p>
     * @return {@link ResponseEntity} una lista con tutti i parcheggi esistenti
     */
    @GetMapping("/all")
    public ResponseEntity<List<ParkingLotResponseDto>> getAllParkingLot() {
        return ResponseEntity.ok(parkingLotService.getAllParkingLot());
    }



    /**
     * <p>Ricerca un parcheggio specifico tramite il NOME</p>
     * @param parkingLotName parametro richiesto per la ricerca
     * @return {@link ResponseEntity} con le informazioni
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @GetMapping("/{parkingLotName}")
    public ResponseEntity<?> getParkingLotByName(@PathVariable String parkingLotName) {
        return parkingLotService.findParkingLotByName(parkingLotName)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Parking lot with name '" + parkingLotName + "' not found"));
    }

    /**
     * <p>Aggiorna la capacita (posti veicolo) di un parcheggio</p>
     * @param parkingLotName parametro richiesto per la ricerca del parcheggio
     * @param newSpotNumber parametro richiesto per aggiornare la nuova capacita
     * @return {@link ResponseEntity} contenente un messaggio informatico con l'avvenuto successo
        * @throws RuntimeException se l'aggiornamento fallisce
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @PutMapping("/updateSpot/{parkingLotName}")
    public ResponseEntity<?> updateVehicleSpotNumberByParkingLotName(@PathVariable String parkingLotName, @RequestParam Long newSpotNumber) {
        return parkingLotService.updateVehicleSpotNumberByParkingLotName(parkingLotName, newSpotNumber)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Failed to update parking lot capacity"));
    }

    /**
     * <p>Cancella un parcheggio tramite il nome</p>
     * @param parkingLotName parametro richiesto per la ricerca
     * @return {@link ResponseEntity} con stato {@code 204 No Content} se la cancellazione ha successo
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @DeleteMapping("/by-name/{parkingLotName}")
    public ResponseEntity<?> deleteParkingLot(@PathVariable String parkingLotName) {
        parkingLotService.deleteParkingLotByName(parkingLotName);
        return ResponseEntity.noContent().build();
    }

    /**
     * <p>Cancella un parcheggio tramite id</p>
     * @param id parametro richiesto per la ricerca
     * @return {@link ResponseEntity} con stato {@code 204 No Content} se la cancellazione ha successo
     */
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @DeleteMapping("/by-id/{id}")
    public ResponseEntity<?> deleteParkingLotById(@PathVariable String id) {
        parkingLotService.deleteParkingLotById(id);
        return ResponseEntity.noContent().build();
    }

}

