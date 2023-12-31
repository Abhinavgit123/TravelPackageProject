package com.travel.travelProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "travelPackages")
@NoArgsConstructor
@AllArgsConstructor
public class TravelPackage {
    @Id
    private String id;
    private String name;
    private int passengerCapacity;
    private List<Destination> itinerary;
    @DBRef
    private List<Passenger> passengers;


    public TravelPackage(String name, int passengerCapacity) {
        this.name = name;
        this.passengerCapacity = passengerCapacity;
        this.itinerary = new ArrayList<>();
        this.passengers = new ArrayList<>();
    }

    // Method to add destinations to the itinerary
    public void addDestination(Destination destination) {
        itinerary.add(destination);
    }


    // Method to add passengers to the travel package
    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

}
