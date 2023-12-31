package com.travel.travelProject.repository;

import com.travel.travelProject.model.TravelPackage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TravelPackageRepository extends MongoRepository<TravelPackage, String> {
    Optional<TravelPackage> findByName(String name);
    boolean existsByName(String name);
}
