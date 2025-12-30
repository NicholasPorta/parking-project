package com.example.demo.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "CapacityChange")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CapacityChange {

    @Id
    private String id;
    @Field("parkingLotId")
    private String parkingLotId;
    @Field("parkingLotName")
    private String parkingLotName;
    @Field("oldCapacity")
    private Long oldCapacity;
    @Field("newCapacity")
    private Long newCapacity;
    @Field("changeDate")
    private LocalDateTime changeDate;
}

