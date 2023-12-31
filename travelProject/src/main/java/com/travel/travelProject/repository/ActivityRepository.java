package com.travel.travelProject.repository;

import com.travel.travelProject.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {

}

