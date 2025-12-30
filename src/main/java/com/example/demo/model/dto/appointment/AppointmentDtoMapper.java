package com.example.demo.model.dto.appointment;


import com.example.demo.model.entities.Appointment;
import com.example.demo.utils.DateUtilities;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(componentModel = "Spring", uses = {DateUtilities.class})
public interface AppointmentDtoMapper {



    default Appointment fromDtoToEntity(AppointmentPostDto dto, String zoneId){
        return Appointment.builder()
                .initialDate(dateConverterForDb(dto.getInitialDate(), zoneId))
                .endingDate(dateConverterForDb(dto.getEndingDate(), zoneId))
                .parkingLotName(dto.getParkingLotName())
                .vehicleSpotCategory(dto.getVehicleSpotCategory())
                .build();
    };



    default LocalDateTime dateConverterForDb(LocalDateTime date, String zoneId){
        return DateUtilities.prepareADateForAService(date, zoneId);
    }


    default AppointmentResponseDto fromEntityToAppointmentResponseDto(Appointment entity, String zoneId){
        return AppointmentResponseDto.builder()
                .initialDate(DateUtilities.convertToZone(entity.getInitialDate(), zoneId))
                .endingDate(DateUtilities.convertToZone(entity.getEndingDate(), zoneId))
                .id(entity.getId())
                .parkingLotName(entity.getParkingLotName())
                .appointmentState(entity.getAppointmentState())
                .vehicleSpotCategory(entity.getVehicleSpotCategory())
                .build();
    }
}
