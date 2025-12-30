package com.example.demo.model.dto.report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationReportMapper {

    @Mapping(target = "period", source = "period")
    @Mapping(target = "totalReservations", source = "totalReservations")
    ReservationReportResponseDto toStatisticsDTO(String period, long totalReservations);
}
