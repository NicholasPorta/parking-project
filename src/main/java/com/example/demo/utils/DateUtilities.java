package com.example.demo.utils;


import com.example.demo.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class DateUtilities {



    private final static DateTimeFormatter dateFormatForDatabase = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static DateTimeFormatter getDateFormat(){
        return dateFormatForDatabase;
    }


    public static LocalDateTime prepareADateForAService(LocalDateTime date, String zoneId){
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, ZoneId.of(zoneId));
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }

    public static LocalDateTime convertToZone(LocalDateTime zoneDate, String targetZoneId) {
        return ZonedDateTime.of(zoneDate, ZoneId.of(targetZoneId)).toLocalDateTime();
    }

    public static boolean isAppointmentDateValid(LocalDateTime initialDate, LocalDateTime endingDate, String clientId){
        boolean flag = true;
        ZonedDateTime initialDateWithZone = ZonedDateTime.of(initialDate, ZoneId.of(clientId));
        ZonedDateTime endingDateWithZone = ZonedDateTime.of(endingDate, ZoneId.of(clientId));
        if (!initialDateWithZone.isAfter(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of(clientId)))){
            flag = false;
        }
        if (!endingDateWithZone.isAfter(initialDateWithZone)){
            flag = false;
        }
        return flag;
    }

    public static boolean isTheVehicleSpotAvailable(String vehicleSpotId, LocalDateTime startDate, LocalDateTime endDate, AppointmentRepository repository){
        return repository.findByDateWithin(startDate, endDate, vehicleSpotId).isEmpty();
    }


}