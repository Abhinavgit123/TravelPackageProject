package com.travel.travelProject.controller;

import com.travel.travelProject.Exception.NotFoundException;
import com.travel.travelProject.Exception.TravelException;
import com.travel.travelProject.model.*;
import com.travel.travelProject.repository.ActivityRepository;
import com.travel.travelProject.service.TravelPackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/travel")
@Slf4j
public class TravelController {

    @Autowired
    private TravelPackageService travelPackageService;

    @Autowired
    ActivityRepository activityRepository;

//    API according to the requirements

    // API to print itinerary of the travel package
    @GetMapping("/itinerary/{travelPackageId}")
    public ResponseEntity<TravelPackage> printItinerary(@PathVariable String travelPackageId) {
        TravelPackage travelPackage = travelPackageService.getTravelPackageById(travelPackageId);

        if (travelPackage != null) {
            return new ResponseEntity<>(travelPackage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // API to print the passenger list of the travel package
    @GetMapping("/passengerList/{travelPackageId}")
    public ResponseEntity<PassengerListResponse> printPassengerList(@PathVariable String travelPackageId) {
        List<Passenger> passengers = travelPackageService.getPassengersForTravelPackage(travelPackageId);
        TravelPackage travelPackage = travelPackageService.getTravelPackageWithPassengerDetails(travelPackageId);

        PassengerListResponse response = new PassengerListResponse(
                travelPackage.getName(),
                travelPackage.getPassengerCapacity(),
                passengers.size(),
                passengers);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // API to print the details of an individual passenger
    @GetMapping("/passengerDetails/{passengerId}")
    public Passenger printPassengerDetails(@PathVariable String passengerId) {
        return travelPackageService.getPassengerById(passengerId);
    }


    // API to print details of activities with available spaces
    @GetMapping("/availableActivities/{travelPackageId}")
    public ResponseEntity<Object> printAvailableActivities(@PathVariable String travelPackageId) {
        try {
            List<Activity> availableActivities = travelPackageService.getAvailableActivities(travelPackageId);
            return new ResponseEntity<>(availableActivities, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    Extra API's to add fetch data


    // API for a passenger to sign up for an activity
    @PostMapping("/signupActivity/{travelPackageId}/{passengerId}/{activityID}")
    public ResponseEntity<String> signUpForActivity(
            @PathVariable String travelPackageId,
            @PathVariable String passengerId,
            @PathVariable String activityID) {

        try {
            HttpStatus status = travelPackageService.signUpForActivity(travelPackageId, passengerId, activityID);

            if (status == HttpStatus.CONFLICT) {
                return new ResponseEntity<>("Capacity is full for this activity", status);
            } else {
                return new ResponseEntity<>("Signup successful", status);
            }
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // API to fetch a TravelPackage by name
    @GetMapping("/getTravelPackage/{packageName}")
    public ResponseEntity<?> getTravelPackageByName(@PathVariable String packageName) {
        try {
            Optional<TravelPackage> travelPackage = travelPackageService.getTravelPackageByName(packageName);

            if (travelPackage.isPresent()) {
                return new ResponseEntity<>(travelPackage.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Travel Package not found", HttpStatus.NOT_FOUND);
            }
        } catch (TravelException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/createTravelPackage")
    public ResponseEntity<String> createTravelPackage(@RequestBody TravelPackage travelPackage) {
        try {
            TravelPackage createdTravelPackage = travelPackageService.createTravelPackage(travelPackage.getName(), travelPackage.getPassengerCapacity());
            return new ResponseEntity<>(createdTravelPackage.getId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

//    API to get all travel package details
    @GetMapping("/getAllTravelPackages")
    public ResponseEntity<Object> getAllTravelPackage(){
        List<TravelPackage> list= travelPackageService.getAllTravelPackages();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }


    // API to add a destination to a travel package
    @PostMapping(value = "/addDestination/{travelPackageId}", consumes = "application/json")
    public ResponseEntity<Object> addDestination(@PathVariable String travelPackageId, @RequestBody Destination destination) {
        try {
            TravelPackage travelPackage= travelPackageService.addDestinationToTravelPackage(travelPackageId, destination);
            return new ResponseEntity<>(travelPackage,HttpStatus.CREATED);
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API to add passenger details to a travel package
    @PostMapping("/addPassenger/{travelPackageId}")
    public ResponseEntity<Object> addPassenger(@PathVariable String travelPackageId, @RequestBody Passenger passenger) {
        try {
            TravelPackage tp= travelPackageService.addPassengerToTravelPackage(travelPackageId, passenger);
            return new ResponseEntity<>(tp, HttpStatus.CREATED);
        }catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    // API to add an activity to a destination in a travel package
    @PostMapping("/addActivity/{travelPackageId}/{destinationID}")
    public ResponseEntity<Object> addActivityToDestination(
            @PathVariable String travelPackageId,
            @PathVariable String destinationID,
            @RequestBody Activity activity) {
        try {
            TravelPackage tp= travelPackageService.addActivityToDestination(travelPackageId, destinationID, activity);
            return new ResponseEntity<>(tp, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Handle the exception, you might want to log it or return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add activity: " + e.getMessage());
        }
    }

    @PostMapping("/createActivity")
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity createdActivity = travelPackageService.createActivity(
                activity.getName(),
                activity.getDescription(),
                activity.getCost(),
                activity.getCapacity(),
                activity.getDestinationName()
        );
        return new ResponseEntity<>(createdActivity, HttpStatus.CREATED);
    }

    @PostMapping("/createDestination")
    public ResponseEntity<?> createDestination(@RequestBody Destination destination) {
        if (destination.getName() == null || destination.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Destination name is null or empty");
        }

        Destination createdDestination = travelPackageService.createDestination(destination.getName());
        return new ResponseEntity<>(createdDestination, HttpStatus.CREATED);
    }

}
