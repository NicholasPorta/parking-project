package com.example.demo.model.dto.parkingLot;

import com.example.demo.model.entities.ParkingLot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface ParkingLotDtoMapper {

    @Mapping(source = "parkingLotName", target = "name")
    ParkingLot fromDtoToEntity(ParkingLotPostDto dto);

    @Mapping(source = "name", target = "parkingLotName")
    ParkingLotResponseDto fromEntityToDto(ParkingLot entity);
}
