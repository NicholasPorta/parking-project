package com.example.demo.repository;

import com.example.demo.model.entities.Appointment;
import com.example.demo.model.entities.VehicleSpot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    @Query("{ 'initialDate' : { $lte: ?1 }, 'endingDate' : { $gte: ?0 }, 'vehicleSpotId' : ?2, 'appointmentState' : 'VALID'}")
    List<Appointment> findByDateWithin(LocalDateTime startingDate, LocalDateTime endingDate, String vehicleSpotId);

    @Query("{'endingDate' : { $lte: ?0 }, 'appointmentState' : 'VALID'}")
    List<Appointment> findValidAppointments(LocalDateTime startingDate);

    List<Appointment> findByParkingLotName (String parkingLotName);

    List<Appointment> findByVehicleSpotId(String vehicleSpotId);
 }
