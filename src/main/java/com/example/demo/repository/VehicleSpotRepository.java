package com.example.demo.repository;

import com.example.demo.enumerators.VehicleSpotCategory;
import com.example.demo.model.entities.VehicleSpot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleSpotRepository extends MongoRepository<VehicleSpot, String> {

    long countByParkingLotName(String parkingLotName);

    void deleteByParkingLotId(String parkingLotId);


    List<VehicleSpot> findByVehicleSpotCategory(VehicleSpotCategory vehicleSpotCategory);

    List<VehicleSpot> findByParkingLotNameAndVehicleSpotCategory(String parkingLotName, VehicleSpotCategory category);

    List<VehicleSpot> findByParkingLotName(String parkingLotName);

}
