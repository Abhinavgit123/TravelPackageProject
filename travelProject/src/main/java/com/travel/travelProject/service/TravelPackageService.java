package com.travel.travelProject.service;

import com.travel.travelProject.Exception.NotFoundException;
import com.travel.travelProject.Exception.TravelException;
import com.travel.travelProject.model.*;
import com.travel.travelProject.repository.ActivityRepository;
import com.travel.travelProject.repository.DestinationRepository;
import com.travel.travelProject.repository.PassengerRepository;
import com.travel.travelProject.repository.TravelPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TravelPackageService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public TravelPackageService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    private TravelPackageRepository travelPackageRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    public List<TravelPackage> getAllTravelPackages() {
        return travelPackageRepository.findAll();
    }

    public TravelPackage getTravelPackageById(String travelPackageId) {
        return travelPackageRepository.findById(travelPackageId).orElse(null);
    }

    public Passenger getPassengerById(String passengerId) {
        return passengerRepository.findById(passengerId).orElse(null);
    }

    public List<Activity> getAvailableActivities(String travelPackageId) {
        TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId)
                .orElseThrow(() -> new RuntimeException("Travel Package not found"));

        List<Activity> availableActivities = new ArrayList<>();
        for (Destination destination : travelPackage.getItinerary()) {
            List<Activity> activities = Objects.requireNonNull(mongoTemplate.findById(destination.getId(), Destination.class)).getActivities();
            availableActivities.addAll(activities);
        }

        return availableActivities;
    }


    private int calculateAvailableSpaces(TravelPackage travelPackage, Activity activity) {
        long enrolledPassengersCount = travelPackage.getPassengers().stream()
                .filter(passenger -> passenger.getSignedUpActivities() != null)
                .flatMap(passenger -> passenger.getSignedUpActivities().stream())
                .filter(signedUpActivity -> signedUpActivity.equals(activity))
                .count();

        return activity.getCapacity() - (int) enrolledPassengersCount;
    }


    // Method to add a destination to a travel package
    public TravelPackage addDestinationToTravelPackage(String travelPackageId, Destination destination) {
        TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId)
                .orElseThrow(() -> new RuntimeException("Travel Package not found"));

        destination.setTravelPackageID(travelPackage.getId());
        travelPackage.addDestination(destination);
        destinationRepository.save(destination);
        travelPackageRepository.save(travelPackage);

        return travelPackage;
    }


    public TravelPackage addActivityToDestination(String travelPackageId, String destinationID, Activity activity) {
        Destination destination = destinationRepository.findById(destinationID)
                .orElseThrow(() -> new RuntimeException("Destination ID not found or incorrect"));

        // Ensure the activities list is initialized
        if (destination.getActivities() == null) {
            destination.setActivities(new ArrayList<>());
        }

        if (isActivityAlreadyPresent(destination, activity.getName())) {
            throw new RuntimeException("Activity '" + activity.getName() + "' is already present in the destination.");
        }

        saveActivity(activity, destination.getName());

        List<Activity> activities = destination.getActivities();
        activities.add(activity);
        destination.setActivities(activities);
        saveDestination(destination, travelPackageId);

        TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId)
                .orElseThrow(() -> new RuntimeException("Travel Package not found"));

        Destination destinationInPackage = travelPackage.getItinerary().stream()
                .filter(d -> d.getId().equals(destinationID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Destination not found in the travel package"));

        // Ensure the activities list is initialized
        if (destinationInPackage.getActivities() == null) {
            destinationInPackage.setActivities(new ArrayList<>());
        }

        destinationInPackage.getActivities().add(activity);

        return travelPackageRepository.save(travelPackage);
    }

    private boolean isActivityAlreadyPresent(Destination destination, String activityName) {
        return destination.getActivities().stream()
                .anyMatch(activity -> activity.getName().equals(activityName));
    }


    public TravelPackage addPassengerToTravelPackage(String travelPackageId, Passenger passenger) {
        TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId)
                .orElseThrow(() -> new RuntimeException("Travel Package not found"));

        if (travelPackage.getPassengers().stream()
                .anyMatch(existingPassenger -> existingPassenger.getPassengerNumber().equals(passenger.getPassengerNumber()))) {
            throw new TravelException("Passenger with the same number already exists");
        }

        // Validate if there is enough capacity for the passenger
        if (travelPackage.getPassengers().size() < travelPackage.getPassengerCapacity()) {
            passengerRepository.save(passenger);
            travelPackage.addPassenger(passenger);
            return travelPackageRepository.save(travelPackage);
        } else {
            throw new TravelException("Travel Package is already at full capacity");
        }
    }


    // Method for a passenger to sign up for an activity
    public HttpStatus signUpForActivity(String travelPackageId, String passengerId, String activityID) {
        TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId)
                .orElseThrow(() -> new NotFoundException("Travel Package", "ID: " + travelPackageId));

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new NotFoundException("Passenger for this package", " ID: " + passengerId));

        Activity activity = travelPackage.getItinerary().stream()
                .flatMap(destination -> destination.getActivities().stream())
                .filter(a -> a.getId().equals(activityID))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Activity for this package", "ID: " + activityID));

        try {
            if (passenger.getSignedUpActivities() == null) {
                passenger.setSignedUpActivities(new ArrayList<>());
            }

            // Check available spaces before signing up
            int availableSpaces = calculateAvailableSpaces(travelPackage, activity);
            if (availableSpaces > 0) {
                passenger.addSignedUpActivity(activity);
                passengerRepository.save(passenger);
                return HttpStatus.OK; // Signup successful
            } else {
                return HttpStatus.CONFLICT; // Capacity full, cannot sign up
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public TravelPackage getTravelPackageWithPassengerDetails(String travelPackageId) {
        return travelPackageRepository.findById(travelPackageId)
                .orElseThrow(() -> new RuntimeException("Travel Package not found"));
    }

    public List<Passenger> getPassengersForTravelPackage(String travelPackageId) {
        TravelPackage travelPackage = getTravelPackageWithPassengerDetails(travelPackageId);
        return travelPackage.getPassengers();
    }


    // Method to fetch a TravelPackage by name
    public Optional<TravelPackage> getTravelPackageByName(String packageName) {
        return travelPackageRepository.findByName(packageName);
    }

    public void saveActivity(Activity activity, String name) {
        if (activity != null) {
            // Check if the name field is not null and not empty before saving
            if (name != null && !name.isEmpty()) {
                activity.setDestinationName(name);
                activityRepository.save(activity);
            } else {
                throw new RuntimeException("Destination name cannot be null or empty");
            }
        } else {
            throw new RuntimeException("Activity object cannot be null");
        }
    }


    public Activity createActivity(String name, String description, double cost, int capacity, String destinations) {
        Activity newActivity = new Activity(name, description, cost, capacity, destinations);

        return activityRepository.save(newActivity);
    }

    public void saveDestination(Destination destination,String travelPackageID) {
        if (destination != null) {
            if (destination.getName() != null && !destination.getName().isEmpty()) {
                    destinationRepository.save(destination);
            } else {
                throw new RuntimeException("Destination name cannot be null or empty");
            }
        } else {
            throw new RuntimeException("Destination object cannot be null");
        }
    }


    public Destination createDestination(String name) {
        Destination destination=new Destination(name);
        return destinationRepository.save(destination);
    }

    public Passenger createPassenger(String name,String passengerNumber, Passenger.PassengerType type, double balance) {
    Passenger passenger=new Passenger(name,passengerNumber, type, balance);
        return passengerRepository.save(passenger);
    }

    public TravelPackage createTravelPackage(String name, int passengerCapacity) {
        if (!travelPackageRepository.existsByName(name)) {
            TravelPackage travelPackage = new TravelPackage(name, passengerCapacity);
            return travelPackageRepository.save(travelPackage);
        } else {
            throw new RuntimeException("A TravelPackage with the name '" + name + "' already exists.");
        }
    }

}
