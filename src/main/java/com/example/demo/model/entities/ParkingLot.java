package com.example.demo.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ParkingLot")
public class ParkingLot {

    @Id
    private String id;
    @Field("name")
    @Indexed(unique = true)
    private String name;
    @Field("vehicleSpotNumber")
    private Long vehicleSpotNumber;

}
