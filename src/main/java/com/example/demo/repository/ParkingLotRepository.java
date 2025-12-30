package com.example.demo.repository;

import com.example.demo.model.entities.ParkingLot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingLotRepository extends MongoRepository<ParkingLot, String> {

    @Query("{ 'name': { $regex: '^?0$', $options: 'i' } }")
    Optional<ParkingLot> findByName(String name);

    Optional<ParkingLot> findByNameIgnoreCase(String name);

}
