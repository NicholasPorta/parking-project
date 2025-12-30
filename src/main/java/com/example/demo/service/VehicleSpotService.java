package com.example.demo.service;

import com.example.demo.enumerators.VehicleSpotCategory;
import com.example.demo.model.dto.vehicleSpot.VehicleSpotDtoMapper;
import com.example.demo.model.dto.vehicleSpot.VehicleSpotPostDto;
import com.example.demo.model.dto.vehicleSpot.VehicleSpotResponseDto;
import com.example.demo.model.dto.vehicleSpot.VehicleSpotUpdateDto;
import com.example.demo.model.entities.ParkingLot;
import com.example.demo.model.entities.VehicleSpot;
import com.example.demo.repository.ParkingLotRepository;
import com.example.demo.repository.VehicleSpotRepository;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleSpotService {
    @Autowired
    private VehicleSpotRepository vehicleSpotRepository;
    @Autowired
    private VehicleSpotDtoMapper mapper;
    @Autowired
    private ParkingLotRepository parkingLotRepository;

    /**
     *<p>Aggiunge dei nuovi posti veicolo a un parcheggio specifico</p>
     *<p>Controlla prima che esista e non è pieno </p>
     *<p>Se pieno lancio un'eccezione</p>
     * @param dto è un oggetto {@link VehicleSpotPostDto} che contiene le informazioni del posto da creare
     * @return un optional dto con il posto creato
     * @throws RuntimeException se il parcheggio non esiste o se i posti disponibili sono insufficienti
     */
    public Optional<Object> postVehicleSpot(VehicleSpotPostDto dto) {
        ParkingLot parkingLot = parkingLotRepository.findByName(dto.getParkingLotName())
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        long currentSpots = vehicleSpotRepository.countByParkingLotName(parkingLot.getName());

        int remainingSpots = Math.toIntExact(
                Optional.ofNullable(parkingLot.getVehicleSpotNumber())
                        .orElseThrow(() -> new RuntimeException("Il numero di posti è nullo"))
                        - currentSpots
        );


        if (dto.getQuantity() > remainingSpots) {
            throw new RuntimeException("Posti disponibili insufficienti. Massimo creabili: " + remainingSpots);
        }

        List<VehicleSpot> createdSpots = new ArrayList<>();

        for (int i = 0; i < dto.getQuantity(); i++) {
            VehicleSpot spot = new VehicleSpot();
            spot.setParkingLotId(parkingLot.getId());
            spot.setParkingLotName(parkingLot.getName());
            spot.setVehicleSpotCategory(VehicleSpotCategory.valueOf(dto.getVehicleSpotCategory().toUpperCase()));
            createdSpots.add(vehicleSpotRepository.save(spot));
        }


        return Optional.of(createdSpots.stream().map(mapper::fromEntityToDto).toList());

    }


    /**
     * <p>Restituisce tutti i posti esistenti</p>
     * @return una lista di {@link VehicleSpotResponseDto} che contiene informazioni per tutti i posti veicolo
     */
    public List<VehicleSpotResponseDto> getAllVehicleSpot() {
        return vehicleSpotRepository.findAll().stream().map(mapper::fromEntityToDto).toList();
    }

    /**
     * <p>Ricerca un posto veicolo specifico tramite ID</p>
     * @param id parametro richiesto per la ricerca
     * @return un optional che contiene un {@link VehicleSpotResponseDto} se il posto esiste, altrimenti è vuoto
     */
    public Optional<Object> getVehicleSpotById(String id) {
        return vehicleSpotRepository.findById(id).map(mapper::fromEntityToDto);
    }

    /**
     * <p>Restituisce tutti i posti veicolo di un parcheggio specifico</p>
     * @param parkingLotName parametro richiesto per la ricerca dei posti veicolo
     * @return una lista di {@link VehicleSpotResponseDto} che contiene informazioni per i posti veicolo del parcheggio specificato
     */

    public List<VehicleSpotResponseDto> getByParkingLotName(String parkingLotName) {
        List<VehicleSpot> spots = vehicleSpotRepository.findByParkingLotName(parkingLotName);
        return spots.stream().map(mapper::fromEntityToDto).toList();
    }


    /**
     * <p>Aggiorna categoria e posto veicolo nel parcheggio</p>
     * <p>Controlla prima che esista </p>
     * <p>Durante lo spostamento può essere modificata anche la categoria del posto veicolo
     ({@link com.example.demo.enumerators.VehicleSpotCategory}).</p>
     * <p>Se inesistente lancia un'eccezione</p>
     * @param id parametro richiesto per la ricerca del posto da aggiornare
     * @param dto è un oggetto {@link VehicleSpotUpdateDto} che contiene le informazioni del posto da aggiornare
     * @return un optional che contiene un {@link VehicleSpotResponseDto} se il posto è stato aggiornato correttamente, altrimenti è vuoto
     * @throws RuntimeException se il posto veicolo non esiste
     */
    public Optional<Object> updateVehicleSpotById(String id, VehicleSpotUpdateDto dto){
        if (!Utils.isRecordInTheDatabase (id, vehicleSpotRepository)){
            throw new RuntimeException("Vehicle spot not found with given id : " + id);
        };
        VehicleSpot vehicleSpotToUpdate =  vehicleSpotRepository.findById(id).get();
        VehicleSpot updatedSpot = (VehicleSpot) Utils.fromUpdateDtoToEntity(vehicleSpotToUpdate, dto);
        return Optional.of(vehicleSpotRepository.save(updatedSpot)).map(mapper::fromEntityToDto);
    }

    /**
     * <p>Cancella un posto veicolo</p>
     * <p>Controlla prima che esista </p>
     * <p>Se inesistente lancia un'eccezione</p>
     * @param id parametro richiesto per la ricerca del posto da eliminare
     * @return {@code true} se il posto è stato cancellato correttamente
     * @throws RuntimeException se il posto veicolo non esiste o se la cancellazione fallisce
     */
    public boolean deleteVehicleSpotById(String id){
        VehicleSpot recordToDelete = vehicleSpotRepository.findById(id).orElseThrow(
                () -> new RuntimeException("record not found with given id " + id)
        );
        vehicleSpotRepository.delete(recordToDelete);
        if (Utils.isRecordInTheDatabase(recordToDelete.getId(), vehicleSpotRepository)){
            throw new RuntimeException("delete of the record with given id " + id + " has failed");
        }
        return true;
    }

    /**
     * <p>Sposto un posto veicolo da un parcheggio a un altro</p>
     * <p>Controllo se esiste sia il posto veicolo che il parcheggio</p>
     * <p>Se uno dei due non esiste oppure il parcheggio è pieno lancio un'eccezione</p>
     *
     * @param vehicleSpotId parametro richiesto per la ricerca del posto veicolo
     * @param newParkingLotId parametro richiesto per il nuovo posto veicolo inserendo il {@code parkingLotId}
     * @return un {@link VehicleSpotResponseDto} con le nuove informazioni aggiornate
     * @throws RuntimeException se il posto veicolo non esiste, il parcheggio non esiste, il parcheggio è pieno o il posto è già assegnato al parcheggio di destinazione
     */
    public VehicleSpotResponseDto moveVehicleSpotInParkingLotById (String vehicleSpotId,String newParkingLotId){
        VehicleSpot vehicleSpot = vehicleSpotRepository.findById(vehicleSpotId)
                .orElseThrow(() -> new RuntimeException("VehicleSpot not found"));

        ParkingLot parkingLot = parkingLotRepository.findById(newParkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking Lot with name '" + newParkingLotId + "' not found"));

        long currentVehicleSpot = vehicleSpotRepository.countByParkingLotName(parkingLot.getName());

        if (vehicleSpot.getParkingLotId().equals(parkingLot.getId())) {
            throw new RuntimeException("This spot is already assigned to the target parking lot. " + newParkingLotId);
        }

        if (currentVehicleSpot >= parkingLot.getVehicleSpotNumber()) {
            throw new RuntimeException("Cannot reassign slot. Parking Lot '" + newParkingLotId + "' has reached maximum capacity.");
        }
        vehicleSpot.setParkingLotId(parkingLot.getId());
        vehicleSpot.setParkingLotName(parkingLot.getName());

        VehicleSpot updatedSpot = vehicleSpotRepository.save(vehicleSpot);
        return mapper.fromEntityToDto(updatedSpot);
    }
}
