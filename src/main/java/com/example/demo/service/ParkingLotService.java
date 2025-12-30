package com.example.demo.service;

import com.example.demo.model.dto.parkingLot.ParkingLotDtoMapper;
import com.example.demo.model.dto.parkingLot.ParkingLotPostDto;
import com.example.demo.model.dto.parkingLot.ParkingLotResponseDto;
import com.example.demo.model.entities.CapacityChange;
import com.example.demo.model.entities.ParkingLot;
import com.example.demo.repository.CapacityChangeRepository;
import com.example.demo.repository.ParkingLotRepository;
import com.example.demo.repository.VehicleSpotRepository;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotService {
    @Autowired
    private ParkingLotRepository parkingLotRepository;
    @Autowired
    private ParkingLotDtoMapper mapper;
    @Autowired
    private VehicleSpotRepository vehicleSpotRepository;
    @Autowired
    private CapacityChangeRepository capacityChangeRepository;

    /**
     * <p>Aggiunge un nuovo parcheggio </p>
     * <p>Controllo che il nome sia univoco con l'annotazione {@code @Indexed(unique = true)} presente in {@link ParkingLot}</p>
     * @param dto parametri richiesti per la creazione {@link ParkingLotPostDto}
     * @return {@code Optional<ParkingLotResponseDto>} contenente il parcheggio creato
     */
    public Optional<ParkingLotResponseDto> postParkingLot(ParkingLotPostDto dto) {
        return Optional.of(parkingLotRepository.save(mapper.fromDtoToEntity(dto))).map(mapper::fromEntityToDto);
    }

    /**
     * <p>Restituisce tutti i parcheggi esistenti</p>
     * @return lista di {@link ParkingLotResponseDto} contenente tutti i parcheggi presenti
     */
    public List<ParkingLotResponseDto> getAllParkingLot() {
        return parkingLotRepository.findAll()
                .stream()
                .map(mapper::fromEntityToDto)
                .toList();

    }

    /**
     * <p>Ricerca un parcheggio specifico tramite il NOME</p>
     * @param parkingLotName parametro richiesto per la ricerca
     * @return {@code Optional<ParkingLotResponseDto>} con le informazioni
     */
    public Optional<ParkingLotResponseDto> findParkingLotByName(String parkingLotName) {
        return parkingLotRepository.findByName(parkingLotName)
                .map(mapper::fromEntityToDto);
    }

    /**
     * <p>Aggiorna la capacita (posti veicolo) di un parcheggio</p>
     * <p>Salvo la capacita originale "oldCapacity" e poi la nuova "updatedParkingLot"
     * cosi posso usarla per il report {@link ReportService}</p>
     * <p>Se la nuova capacità è uguale a quella attuale, non viene effettuata alcuna modifica</p>
     * @param parkingLotName parametro richiesto per la ricerca del parcheggio
     * @param newSpotNumber parametro richiesto per aggiornare la nuova capacita
     * @return {@link ParkingLotResponseDto} contenente i dati aggiornati del parcheggio
     * @throws RuntimeException se il parcheggio non viene trovato
     */
    public Optional<ParkingLotResponseDto> updateVehicleSpotNumberByParkingLotName(String parkingLotName, Long newSpotNumber) {
        ParkingLot parkingLot = parkingLotRepository.findByName(parkingLotName)
                .orElseThrow(() -> new RuntimeException("Parking lot with name " + parkingLotName + " not found"));

        Long oldCapacity = parkingLot.getVehicleSpotNumber();

        ParkingLotPostDto updateDto = new ParkingLotPostDto();
        updateDto.setVehicleSpotNumber(newSpotNumber);

        ParkingLot updatedParkingLot = (ParkingLot) Utils.fromUpdateDtoToEntity(parkingLot, updateDto);
        parkingLotRepository.save(updatedParkingLot);

        Optional<CapacityChange> existing = capacityChangeRepository
                .findByParkingLotId(updatedParkingLot.getId())
                .stream()
                .findFirst();

        CapacityChange capacityChange = CapacityChange.builder()
                .parkingLotName(updatedParkingLot.getName())
                .parkingLotId(updatedParkingLot.getId())
                .oldCapacity(oldCapacity)
                .newCapacity(newSpotNumber)
                .changeDate(LocalDateTime.now())
                .build();

        existing.ifPresent(c -> capacityChange.setId(c.getId()));
        capacityChangeRepository.save(capacityChange);

        return Optional.of(mapper.fromEntityToDto(updatedParkingLot));
    }

    /**
     * <p>Cancella un parcheggio tramite il nome</p>
     * <p>Cancella il parcheggio e tutti i posti veicolo associati.</p>
     * @param parkingLotName parametro richiesto per la cancellazione del parcheggio
     * @return {@code true} se il parcheggio è stato cancellato correttamente
     */
    public boolean deleteParkingLotByName(String parkingLotName) {
        parkingLotRepository.findByName(parkingLotName)
                .ifPresentOrElse(parkingLot -> {
                    vehicleSpotRepository.deleteByParkingLotId(parkingLot.getId());

                    parkingLotRepository.delete(parkingLot);
                }, () -> {
                    throw new RuntimeException("Parking lot with name '" + parkingLotName + "' not found");
                });
        return true;
    }
    /**
     * <p>Cancella un parcheggio tramite ID</p>
     * <p>Cancella il parcheggio e tutti i posti veicolo associati.</p>
     * @param id parametro richiesto per la cancellazione del parcheggio
     * @return {@code true} se il parcheggio è stato cancellato correttamente
     */
    public boolean deleteParkingLotById(String id) {
        parkingLotRepository.findById(id)
                .ifPresentOrElse(parkingLot -> {
                    vehicleSpotRepository.deleteByParkingLotId(parkingLot.getId());

                    parkingLotRepository.delete(parkingLot);
                }, () -> {
                    throw new RuntimeException("Parking lot with name '" + id + "' not found");
                });
        return true;
    }

}
