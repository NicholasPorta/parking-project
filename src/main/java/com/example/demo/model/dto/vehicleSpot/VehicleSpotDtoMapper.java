package com.example.demo.model.dto.vehicleSpot;


import com.example.demo.enumerators.VehicleSpotCategory;
import com.example.demo.model.entities.VehicleSpot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public interface VehicleSpotDtoMapper {

    @Mapping(source = "vehicleSpotCategory", target = "vehicleSpotCategory", qualifiedByName = "findVehicleSpotCategoryByName")
    VehicleSpot fromDtoToEntity(VehicleSpotPostDto dto);

    @Mapping(source = "parkingLotName", target = "parkingLotName")
    VehicleSpotResponseDto fromEntityToDto(VehicleSpot dto);

    @Named("findVehicleSpotCategoryByName")
    default VehicleSpotCategory findVehicleSpotCategoryByName(String name) {
        return Arrays.stream(VehicleSpotCategory.values())
                .filter(enumerator -> enumerator.name().equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new RuntimeException("category not found with given name: " + name +
                        " available categories: " + Arrays.toString(VehicleSpotCategory.values())));
    }

}
