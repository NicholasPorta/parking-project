package com.example.demo.model.dto.report;

import com.example.demo.enumerators.VehicleSpotCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface ParkingOccupancyMapper {
    ParkingOccupancyMapper INSTANCE = Mappers.getMapper(ParkingOccupancyMapper.class);


    @Mapping(target = "totalSlots", source = "totalSlots")
    @Mapping(target = "occupiedSlots", source = "occupiedSlots")
    @Mapping(target = "availableSlots", source = "availableSlots")
    @Mapping(target = "categoryBreakdown", source = "categoryBreakdown") // 🔹 Aggiunto
    ParkingOccupancyResponseDto toOccupancyDTO(long totalSlots, long occupiedSlots, long availableSlots, Map<VehicleSpotCategory, Map<String, Long>> categoryBreakdown);

}
