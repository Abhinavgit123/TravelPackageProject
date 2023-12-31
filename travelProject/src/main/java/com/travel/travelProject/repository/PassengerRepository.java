package com.travel.travelProject.repository;

import com.travel.travelProject.model.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PassengerRepository extends MongoRepository<Passenger, String> {
    boolean existsBypassengerNumber(String number);
}