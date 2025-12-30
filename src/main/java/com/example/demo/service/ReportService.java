package com.example.demo.service;

import com.example.demo.enumerators.VehicleSpotCategory;
import com.example.demo.model.dto.capacityChange.CapacityChangeMapper;
import com.example.demo.model.dto.capacityChange.CapacityChangeResponseDto;
import com.example.demo.model.dto.report.ParkingOccupancyResponseDto;
import com.example.demo.model.dto.report.ParkingOccupancyMapper;
import com.example.demo.model.dto.report.ReservationReportResponseDto;
import com.example.demo.model.dto.report.ReservationReportMapper;
import com.example.demo.model.entities.Appointment;
import com.example.demo.model.entities.ParkingLot;
import com.example.demo.model.entities.VehicleSpot;
import com.example.demo.enumerators.AppointmentState;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.CapacityChangeRepository;
import com.example.demo.repository.ParkingLotRepository;
import com.example.demo.repository.VehicleSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {



    @Autowired
    private ReservationReportMapper reservationReportMapper;
    @Autowired
    private ParkingOccupancyMapper parkingOccupancyMapper;
    @Autowired
    private ParkingLotService parkingLotService;
    @Autowired
    private ParkingLotRepository parkingLotRepository;
    @Autowired
    private VehicleSpotRepository vehicleSpotRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private CapacityChangeRepository capacityChangeRepository;
    @Autowired
    private CapacityChangeMapper capacityChangeMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;


    /**
     * <p>Restituisce le statistiche di occupazione per tutti parcheggio</p>
     * <p>Per ogni parcheggio il totale:</p>
     * <ul>
     *     <li>Il numero totale di posti veicolo</li>
     *     <li>Il numero di posti occupati</li>
     *     <li>Il numero di posti disponibili</li>
     *     <li>Un dettaglio per categoria {@link com.example.demo.enumerators.VehicleSpotCategory} con totale, occupati e disponibili</li>
     * </ul>
     *
     * @return lista di {@link ParkingOccupancyResponseDto} con le informazioni per ogni parcheggio
     * @throws RuntimeException se non vengono trovati parcheggi o si verifica un errore durante l'elaborazione
     */
    public List<ParkingOccupancyResponseDto> getParkingOccupancy() {
        try {
            List<ParkingLot> parkingLots = parkingLotRepository.findAll();
            List<Appointment> allAppointments = appointmentRepository.findAll();

            if ((parkingLots.isEmpty())) {
                throw new RuntimeException("no parking lots found");
            }
            return parkingLots.stream().map(parkingLot -> {
                long totalVehichleSpot = parkingLot.getVehicleSpotNumber();

                List<Appointment> appointments = allAppointments.stream()
                        .filter(spot -> spot.getAppointmentState() == AppointmentState.VALID
                                && spot.getParkingLotName().equals(parkingLot.getName()))
                        .toList();

                long occupiedSpots = appointments.size();
                long availableSpots = Math.max(totalVehichleSpot - occupiedSpots, 0);

                Map<VehicleSpotCategory, Long> totalByCategory = vehicleSpotRepository.findAll().stream()
                        .filter(slot -> slot.getParkingLotId().equals(parkingLot.getId()))
                        .collect(Collectors.groupingBy(VehicleSpot::getVehicleSpotCategory, Collectors.counting()));

                Map<VehicleSpotCategory, Long> occupiedByCategory = allAppointments.stream()
                        .filter(spot -> spot.getAppointmentState() == AppointmentState.VALID
                                && parkingLot.getName().equals(spot.getParkingLotName()))
                        .collect(Collectors.groupingBy(Appointment::getVehicleSpotCategory, Collectors.counting()));

                Map<VehicleSpotCategory, Map<String, Long>> categoryBreakdown = totalByCategory.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> {
                                    Map<String, Long> orderedMap = new LinkedHashMap<>();
                                    orderedMap.put("total", entry.getValue());
                                    orderedMap.put("occupied", occupiedByCategory.getOrDefault(entry.getKey(), 0L));
                                    orderedMap.put("available", Math.max(entry.getValue() - occupiedByCategory.getOrDefault(entry.getKey(), 0L), 0));
                                    return orderedMap;
                                }
                        ));


                return new ParkingOccupancyResponseDto(parkingLot.getId(), parkingLot.getName(), totalVehichleSpot, occupiedSpots, availableSpots, categoryBreakdown);
            }).toList();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error calculating parking occupancy: " + e.getMessage());
        }
    }

    /**
     * <p>Restituisce le statistiche di occupazione giornaliere per un parcheggio specifico</p>
     * <p>Consideriamo solo le prenotazioni con stato {@code VALID}(esclude {@code CANCELED,EXPIRED}</p>
     * <p>Per ogni giorno viene calcolato il numero totale di prenotazioni effettuate</p>
     * @return una lista {@link ReservationReportResponseDto} con le informazioni giornaliere
     * @throws RuntimeException se non sono presenti prenotazioni valide nel sistema
     */
    public List<ReservationReportResponseDto> getDailyReportByName(String parkingLotName) {
        Map<LocalDate, Long> dailyReport = appointmentRepository.findAll().stream()
                .filter(spot -> spot.getAppointmentState() != AppointmentState.CANCELED &&
                        spot.getAppointmentState() != AppointmentState.EXPIRED &&
                        spot.getParkingLotName().equalsIgnoreCase(parkingLotName))
                .collect(Collectors.groupingBy(
                        spot -> spot.getInitialDate().toLocalDate(),
                        Collectors.counting()
                ));

        if (dailyReport.isEmpty()) {
            return List.of();
        }

        return dailyReport.entrySet().stream()
                .map(entry -> reservationReportMapper.toStatisticsDTO(
                        entry.getKey().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)),
                        entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * <p>Restituisce le statistiche di occupazione settimanali per un parcheggio specifico</p>
     * <p>Consideriamo solo le prenotazioni con stato {@code VALID} (esclude {@code CANCELED,EXPIRED}</p>
     * <p>Per ogni settimana viene calcolato il numero totale di prenotazioni effettuate</p>
     * @return una lista {@link ReservationReportResponseDto} con le informazioni settimanali
     * @throws RuntimeException se non sono presenti prenotazioni valide nel sistema
     */
    public List<ReservationReportResponseDto> getWeekReportByName(String parkingLotName) {
        Map<Integer, Long> weekReport = appointmentRepository.findAll().stream()
                .filter(spot -> spot.getAppointmentState() != AppointmentState.CANCELED &&
                        spot.getAppointmentState() != AppointmentState.EXPIRED &&
                        spot.getParkingLotName().equalsIgnoreCase(parkingLotName))
                .collect(Collectors.groupingBy(
                        spot -> spot.getInitialDate().toLocalDate().get(WeekFields.ISO.weekOfYear()),
                        Collectors.counting()
                ));

        if (weekReport.isEmpty()) {
            return List.of();
        }

        return weekReport.entrySet().stream()
                .map(weekEntry -> {
                    int weekNumber = weekEntry.getKey();
                    int year = LocalDate.now().getYear();

                    LocalDate firstDay = LocalDate.of(year, 1, 1)
                            .with(WeekFields.ISO.weekOfYear(), weekNumber)
                            .with(WeekFields.ISO.dayOfWeek(), 1);
                    LocalDate lastDay = firstDay.plusDays(6);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
                    String period = firstDay.format(formatter) + " - " + lastDay.format(formatter);

                    return reservationReportMapper.toStatisticsDTO(
                            period,
                            weekEntry.getValue());
                })
                .collect(Collectors.toList());
    }

    /**
     * <p>Restituisce le statistiche di occupazione mensili per un parcheggio specifico</p>
     * <p>Consideriamo solo le prenotazioni con stato {@code VALID} (esclude {@code CANCELED,EXPIRED}</p>
     * <p>Per ogni mese viene calcolato il numero totale di prenotazioni effettuate</p>
     * @return una lista {@link ReservationReportResponseDto} con le informazioni mensili
     * @throws RuntimeException se non sono presenti prenotazioni valide nel sistema
     */
    public List<ReservationReportResponseDto> getMonthReportByName(String parkingLotName) {
        Map<Integer, Long> monthlyReport = appointmentRepository.findAll().stream()
                .filter(spot -> spot.getAppointmentState() != AppointmentState.CANCELED &&
                        spot.getAppointmentState() != AppointmentState.EXPIRED &&
                        spot.getParkingLotName().equalsIgnoreCase(parkingLotName))
                .collect(Collectors.groupingBy(
                        spot -> spot.getInitialDate().getMonthValue(),
                        Collectors.counting()
                ));

        if (monthlyReport.isEmpty()) {
            return List.of();
        }

        return monthlyReport.entrySet().stream()
                .map(monthEntry -> reservationReportMapper.toStatisticsDTO(
                        Month.of(monthEntry.getKey()).name(),
                        monthEntry.getValue()))
                .collect(Collectors.toList());
    }


    /**
     * <p>Restituisce lo storico delle prenotazioni per ciascun parcheggio</p>
     * <p>Consideriamo tutte le prenotazioni </p>
     * <p>Per ogni prenotazione viene generato un report che include data, orario, stato, categoria e parcheggio</p>
     * @return una lista {@link ReservationReportResponseDto} con le informazioni storiche delle prenotazioni
     * @throws RuntimeException se non sono presenti prenotazioni nel sistema
     */
    public List<ReservationReportResponseDto> getReportHistoryByParkingLotName(String parkingLotName) {
        List<Appointment> appointments = appointmentRepository.findAll();

        if (appointments.isEmpty()) {
            throw new RuntimeException("No reservations found");
        }

        return appointments.stream()
                .filter(app -> app.getParkingLotName().equalsIgnoreCase(parkingLotName))
                .map(app -> {
                    String formattedStart = app.getInitialDate()
                            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH));

                    String formattedEnd = app.getEndingDate()
                            .format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH));

                    String period = formattedStart + " - " + formattedEnd
                            + " → " + app.getAppointmentState()
                            + " → " + app.getVehicleSpotCategory();

                    return reservationReportMapper.toStatisticsDTO(period, 1L);
                })
                .collect(Collectors.toList());
    }

    /**
     * <p>Restituisce la % delle prenotazioni cancellate sul totale delle prenotazioni effettuate</p>
     * <p>Consideriamo solo le prenotazioni con lo stato {@code CANCELED} </p>
     * <p>Per ogni prenotazione cancellata viene generato un report con la % delle cancellazioni in base alle prenotazioni</p>
     * @return percentuale di cancellazioni
     * @throws RuntimeException se non sono presenti prenotazioni valide nel sistema
     */
    public double getCancellationRate() {
        long totalReservations = appointmentRepository.count();
        long canceledReservations = appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getAppointmentState() == AppointmentState.CANCELED)
                .count();

        if (totalReservations == 0) {
            throw new RuntimeException("no reservation found");
        }
        return ((double) canceledReservations / totalReservations) * 100;
    }

    /**
     * <p>Restituisce lo storico delle modifiche alla capacità di un parcheggio</p>
     * <p>Controllo se esiste il parcheggio, se non esiste il parcheggio lancio un'eccezione</p>
     * <p>Se esiste il parcheggio ma non è stata registrata nessuna modifica restituisco una lista vuota</p>
     * @param parkingLotName informazione che identifica il parcheggio
     * @return una lista di {@link CapacityChangeResponseDto} con le informazioni dettagliate
     * @throws RuntimeException se il parcheggio non esiste o se si verifica un errore durante l'elaborazione
     */
    public List<CapacityChangeResponseDto> getCapacityChangeHistoryByName(String parkingLotName) {
        ParkingLot parkingLot = parkingLotRepository.findByNameIgnoreCase(parkingLotName)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));


        return capacityChangeRepository.findByParkingLotNameIgnoreCase(parkingLotName).stream()
                .map(change -> CapacityChangeResponseDto.builder()
                        .parkingLotId(change.getParkingLotId())
                        .parkingLotName(parkingLot.getName())
                        .oldCapacity(change.getOldCapacity())
                        .newCapacity(change.getNewCapacity())
                        .changeDate(change.getChangeDate())
                        .build())
                .collect(Collectors.toList());
    }



}
