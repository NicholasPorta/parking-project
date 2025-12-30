package com.example.demo.service;

import com.example.demo.enumerators.AppointmentState;
import com.example.demo.enumerators.VehicleSpotCategory;
import com.example.demo.model.dto.appointment.*;
import com.example.demo.model.entities.Appointment;
import com.example.demo.model.entities.VehicleSpot;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.VehicleSpotRepository;
import com.example.demo.utils.DateUtilities;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppointmentDtoMapper mapper;
    @Autowired
    private VehicleSpotRepository vehicleSpotRepository;

    /**
     * <p>Aggiunge la prenotazione per un posto veicolo</p>
     * <p>Verifica che il posto selezionato sia disponibile</p>
     * <p>Se non è disponibile lancia un eccezione</p>
     *
     * @param dto parametri richiesti per prenotare {@link AppointmentPostDto}
     * @param zoneIdFromClient zona oraria del client utilizzata per convertire le date
     * @return {@link AppointmentResponseDto} contenente i dati della prenotazione
     * @throws RuntimeException se il posto è occupato in una data e ora specifica
     */
    public Optional<Object> postAppointment(AppointmentPostDto dto, String zoneIdFromClient) {
        List<VehicleSpot> spots = vehicleSpotRepository.findByParkingLotNameAndVehicleSpotCategory(
                dto.getParkingLotName(),
                dto.getVehicleSpotCategory()
        );

        // Converti le date al formato UTC per la verifica
        LocalDateTime initialDate = DateUtilities.prepareADateForAService(dto.getInitialDate(), zoneIdFromClient);
        LocalDateTime endingDate = DateUtilities.prepareADateForAService(dto.getEndingDate(), zoneIdFromClient);

        // Trova il primo posto libero in quell'orario
        VehicleSpot spot = spots.stream()
                .filter(s -> DateUtilities.isTheVehicleSpotAvailable(
                        s.getId(),
                        initialDate,
                        endingDate,
                        appointmentRepository))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessun posto disponibile per la categoria '" +
                        dto.getVehicleSpotCategory() + "' nel parcheggio '" + dto.getParkingLotName() + "'"));

        // Crea la prenotazione
        Appointment appointment = mapper.fromDtoToEntity(dto, zoneIdFromClient);
        appointment.setVehicleSpotId(spot.getId());
        appointment.setParkingLotName(spot.getParkingLotName());
        appointment.setVehicleSpotCategory(spot.getVehicleSpotCategory());

        return Optional.of(appointmentRepository.save(appointment))
                .map(value -> mapper.fromEntityToAppointmentResponseDto(value, zoneIdFromClient));
    }



    /**
     * <p>Restituisce tutti i posti veicolo disponibili per una specifica categoria e intervallo di date</p>
     * <p>Le date vengono convertite dal fuso orario del client a UTC per la verifica con i dati del database</p>
     * <p>Vengono filtrati solo i posti veicolo che risultano liberi nell'intervallo specificato</p>
     *
     * @param category categoria del posto veicolo {@link VehicleSpotCategory}
     * @param dto {@link AppointmentDatesDto} contenente la data di inizio e fine
     * @param zoneIdFromClient identificatore della zona oraria del client
     * @return lista di {@link VehicleSpot} disponibili nella categoria e periodo indicati
     * @throws RuntimeException se la categoria non è valida
     */

    public List<VehicleSpot> getAvailableVehicleSpotByCategory(String category, AppointmentDatesDto dto,String zoneIdFromClient){
        VehicleSpotCategory foundCategory =  Arrays.stream(VehicleSpotCategory.values())
                .filter(enumerator -> enumerator.getCategoryName().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("invalid category, please insert a correct category: " + Arrays.toString(VehicleSpotCategory.values()))
        );
        List<VehicleSpot> vehicleSpots = vehicleSpotRepository.findByVehicleSpotCategory(foundCategory);

        // Converto l'orario in utc del database
        LocalDateTime InitialDate = DateUtilities.prepareADateForAService(dto.getInitialDate(), zoneIdFromClient);
        LocalDateTime EndingDate = DateUtilities.prepareADateForAService(dto.getEndingDate(), zoneIdFromClient);

        return vehicleSpots.stream().filter
                (vehicleSpot->
                    DateUtilities.isTheVehicleSpotAvailable(
                            vehicleSpot.getId(),
                            InitialDate,
                            EndingDate,
                            appointmentRepository)

        ).toList();
    }

    /**
     * <p>Restituisce tutte le prenotazioni</p>
     * <p>Utilizza il repository per ottenere tutte le prenotazioni</p>
     * <p>Le date vengono convertite dal fuso orario del client a UTC per la visualizzazione</p>
     * @param zoneIdFromClient identificatore della zona oraria del client
     * @return lista di {@link AppointmentResponseDto} contenente i dati delle prenotazioni
     * @throws RuntimeException se si verifica un errore durante il recupero delle prenotazioni
     */
    public Optional<Object> getAllAppointments(String zoneIdFromClient) {
        return Optional.of(appointmentRepository.findAll().stream()
                .map(value -> mapper.fromEntityToAppointmentResponseDto(value, zoneIdFromClient)).toList());
    }

    /**
     * <p>Restituisce una prenotazione </p>
     * @param id parametro richiesto per la ricerca
     * @param zoneIdFromClient identificatore della zona oraria del client
     * @return {@link AppointmentResponseDto} contenente i dati delle prenotazioni
     * @throws RuntimeException se non esiste una prenotazione con l'ID specificato
     */
    public Optional<Object> getAppointmentById(String id, String zoneIdFromClient) {
        return appointmentRepository.findById(id).map(value -> mapper.fromEntityToAppointmentResponseDto(value, zoneIdFromClient));
    }

    /**
     * <p>Cancella una prenotazione</p>
     * <p>Controllo se la prenotazione esiste</p>
     * @param id parametro richiesto per la ricerca
     * @return {@code true} se la prenotazione è stata cancellata correttamente
     * @throws RuntimeException se non esiste oppure fallisce
     */
    public boolean deleteAppointmentById(String id){
        Appointment recordToDelete = appointmentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("record not found with given id " + id)
        );
        appointmentRepository.delete(recordToDelete);
        if (Utils.isRecordInTheDatabase(recordToDelete.getId(), appointmentRepository)){
            throw new RuntimeException("delete of the record with given id " + id + " has failed");
        }
        return true;
    }


    // ADMIN

    /**
     * <p>Aggiorna una prenotazione esistente tramite ID </p>
     * <p>Controllo che la prenotazione esista e poi effettua l'aggiornamento</p>
     *
     * @param id ID dell'appuntamento da aggiornare
     * @param dto {@link AppointmentUpdateDto} contenente i nuovi dati
     * @param zoneIdFromClient zona oraria del client per la conversione delle date
     * @return {@link AppointmentResponseDto} con i dati aggiornati
     * @throws RuntimeException se l'appuntamento non esiste
     */

    public Optional<Object> updateAppointmentById(String id, AppointmentUpdateDto dto, String zoneIdFromClient) {
        if (!Utils.isRecordInTheDatabase (id, appointmentRepository)){
            throw new RuntimeException("appointment not found with given id : " + id);
        };
        Appointment appointmentToUpdate =  appointmentRepository.findById(id).get();
        Appointment updatedAppointment = (Appointment) Utils.fromUpdateDtoToEntity(appointmentToUpdate, dto);

        return Optional.of(appointmentRepository.save(updatedAppointment)).map(entity -> mapper.fromEntityToAppointmentResponseDto(updatedAppointment, zoneIdFromClient));
    }

    //USER

    /**
     * <p>Imposta lo stato di una prenotazione su {@code CANCELED}</p>
     * <p>Controllo che la prenotazione esista e poi effettua l'aggiornamento</p>
     *
     * @param id ID della prenotazione da annullare
     * @param zoneIdFromClient zona oraria del client per la conversione delle date
     * @return {@link AppointmentResponseDto} con lo stato aggiornato
     * @throws RuntimeException se l'appuntamento non esiste
     */

    public Optional<Object> updateAppointmentStateToCancel(String id, String zoneIdFromClient){
        if (!Utils.isRecordInTheDatabase (id, appointmentRepository)){
            throw new RuntimeException("appointment not found with given id : " + id);
        };
        Appointment appointmentToUpdate =  appointmentRepository.findById(id).get();
        appointmentToUpdate.setAppointmentState(AppointmentState.CANCELED);

        return Optional.of(appointmentRepository.save(appointmentToUpdate)).map(entity -> mapper.fromEntityToAppointmentResponseDto(appointmentToUpdate, zoneIdFromClient));
    }

    /**
     * <p>Task schedulato ogni 60 secondi che aggiorna lo stato delle prenotazioni scadute</p>
     * <p>Le prenotazioni scadute sono quelle con una data di fine passata e stato ancora {@code VALID}</p>
     * Lo stato viene aggiornato a {@code EXPIRED}.
     * <p>Utilizza il metodo {@link #expiredAppointments()} per ottenere le prenotazioni scadute</p>
     * <p>Le prenotazioni aggiornate vengono salvate nel database</p>
     * @throws RuntimeException se si verifica un errore durante il salvataggio</p>
     */

    @Scheduled(fixedDelay = 60000)
    public  void updateExpiredAppointments(){
        List<Appointment> updatedAppointments = new ArrayList<>();
        expiredAppointments()
                .forEach(
                        (appointment)->{
                            appointment.setAppointmentState(AppointmentState.EXPIRED);
                            updatedAppointments.add(appointment);
                        }
                );
        appointmentRepository.saveAll(updatedAppointments);
    }

    /**
     * <p>Restituisce tutte le prenotazioni ancora valide ma con data di fine superata</p>
     * <p>Utilizza il repository per trovare le prenotazioni con stato {@code VALID} e data di fine passata</p>
     * <p>Le prenotazioni vengono filtrate in base alla data corrente</p>
     * <p>Utilizza il fuso orario UTC per la verifica delle date</p>
     *
     * @return lista di appuntamenti scaduti da aggiornare
     */

    private List<Appointment> expiredAppointments(){
        return appointmentRepository.findValidAppointments(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));
    }
}
