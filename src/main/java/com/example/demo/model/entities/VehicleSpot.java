package com.example.demo.model.entities;

import com.example.demo.enumerators.VehicleSpotCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "VehicleSpot")
public class VehicleSpot {

    @Id
    private String id;
    @Field("parkingLotId")
    private String parkingLotId;
    @Field("parkingLotName")
    private String parkingLotName;
    @Field("VehicleSpotCategory")
    private VehicleSpotCategory vehicleSpotCategory;


}
