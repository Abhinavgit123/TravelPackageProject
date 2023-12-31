package com.travel.travelProject.repository;

import com.travel.travelProject.model.Destination;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DestinationRepository extends MongoRepository<Destination, String> {

}
