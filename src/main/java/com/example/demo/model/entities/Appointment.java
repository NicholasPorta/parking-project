package com.example.demo.model.entities;

import com.example.demo.enumerators.AppointmentState;
import com.example.demo.enumerators.VehicleSpotCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "Appointment")
public class Appointment {

    @Id
    private String id;
    @Field("vehicleSpotId")
    private String vehicleSpotId;
    @Field("ParkingLot reference")
    private String parkingLotId;
    @Field("parkingLotName")
    private String parkingLotName;
    @Field("initialDate")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime initialDate;
    @Field("endingDate")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endingDate;
    @Field("spot vehicleSpotCategory ")
    private VehicleSpotCategory vehicleSpotCategory;
    @Field("appointmentState")
    @Builder.Default
    private AppointmentState appointmentState = AppointmentState.VALID;
}
