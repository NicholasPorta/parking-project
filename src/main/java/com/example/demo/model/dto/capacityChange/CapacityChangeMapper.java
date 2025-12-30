package com.example.demo.model.dto.capacityChange;

import com.example.demo.model.entities.CapacityChange;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CapacityChangeMapper {

    CapacityChangeResponseDto fromEntityToDto(CapacityChange dto);

}
