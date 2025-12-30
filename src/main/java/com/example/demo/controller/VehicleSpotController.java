package com.example.demo.controller;


import com.example.demo.model.dto.vehicleSpot.VehicleSpotPostDto;
import com.example.demo.model.dto.vehicleSpot.VehicleSpotResponseDto;
import com.example.demo.model.dto.vehicleSpot.VehicleSpotUpdateDto;
import com.example.demo.service.VehicleSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@PreAuthorize("hasRole('AMMINISTRATORE')")
@RestController
@RequestMapping("/api/vehicleSpot")
public class VehicleSpotController {
    @Autowired
    private VehicleSpotService vehicleSpotService;


    //CREATE
    /**
     *<p>Crea un nuovo posto veicolo in un parcheggio specificato</p>
     *<p>Controlla i dati ricevuti e li inoltra {@link VehicleSpotService} per la creazione</p>
     *
     * @param dto oggetto {@link VehicleSpotPostDto} contenente le informazioni del posto veicolo da creare
     * @return {@link ResponseEntity} contenente il posto creato in caso di successo
     */
    @PostMapping
    public ResponseEntity<?> postVehicleSpot(@Valid @RequestBody VehicleSpotPostDto dto){
        return vehicleSpotService.postVehicleSpot(dto).map(ResponseEntity::ok).orElseThrow(
                () -> new RuntimeException("failed to create a new vehicle spot"));
    };

    //READ

    /**
     * <p>Restituisce i posti veicolo esistenti</p>
     * @return {@link ResponseEntity} contenente la lista dei posti veicolo oppure un messaggio di errore se vuota
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllVehicleSpot() {
        List<VehicleSpotResponseDto> spots = vehicleSpotService.getAllVehicleSpot();

        if (spots.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no record found in the database");
        }

        return ResponseEntity.ok(spots);
    }


    /**
     * <p>Ricerca un posto veicolo specifico tramite ID</p>
     * @param id parametro richiesto per la ricerca
     * @return {@link ResponseEntity} contenente informazioni del posto veicolo trovato oppure un messaggio di errore se vuota
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleSpotById(@PathVariable String id){
        return vehicleSpotService.getVehicleSpotById(id).map(ResponseEntity::ok)
                .orElseThrow(()-> new RuntimeException("vehicle spot not found with given id: " + id));
    }

    @GetMapping("/by-parkingLot/{parkingLotName}")
    public ResponseEntity<?> getVehicleSpotsByParkingLotName(@PathVariable String parkingLotName) {
        return ResponseEntity.ok(vehicleSpotService.getByParkingLotName(parkingLotName));
    }


    /**<p>Aggiorna categoria e posto veicolo nel parcheggio</p>
     *
     * @param id parametro richiesto per la ricerca del posto da aggiornare
     * @param dto oggetto {@link VehicleSpotUpdateDto} contenente i nuovi dati da aggiornare
     * @return {@link ResponseEntity} con il posto aggiornato
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicleSpotById(@PathVariable String id, @RequestBody VehicleSpotUpdateDto dto){
        return vehicleSpotService.updateVehicleSpotById(id, dto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     *<p>Cancella un posto veicolo</p>
     *
     * @param id parametro richiesto per la ricerca del posto da eliminare
     * @return {@link ResponseEntity} con stato {@code 204 No Content} se la cancellazione ha successo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicleSpotById (@PathVariable String id) {
        vehicleSpotService.deleteVehicleSpotById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     *<p>Sposto un posto veicolo da un parcheggio a un altro</p>
     *<p>Verifica l'esistenza del posto veicolo e del parcheggio di destinazione,
     * aggiornando l'associazione se disponibile.</p>
     *
     * @param vehicleSpotId parametro richiesto per la ricerca del posto veicolo da spostare
     * @param newParkingLotId parametro richiesto per il nuovo posto veicolo inserendo il {@code parkingLotId}
     * @return {@link ResponseEntity} con il posto aggiornato
     * @throws RuntimeException se il posto veicolo o il parcheggio di destinazione non esistono
     * o se non è possibile completare lo spostamento
     */
    @PutMapping("/{vehicleSpotId}/move")
    public ResponseEntity <?> moveVehicleSpotInParkingLotById (@PathVariable String vehicleSpotId, @RequestParam String newParkingLotId){
        return ResponseEntity.ok(vehicleSpotService.moveVehicleSpotInParkingLotById(vehicleSpotId, newParkingLotId ));
    }
}
