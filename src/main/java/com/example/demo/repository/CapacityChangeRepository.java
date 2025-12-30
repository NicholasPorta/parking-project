package com.example.demo.repository;

import com.example.demo.model.entities.CapacityChange;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CapacityChangeRepository extends MongoRepository<CapacityChange, String> {

    List<CapacityChange> findByParkingLotId(String parkingLotId);

    List<CapacityChange> findByParkingLotNameIgnoreCase(String parkingLotName);

}
