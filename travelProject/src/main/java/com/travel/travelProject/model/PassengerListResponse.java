package com.travel.travelProject.model;

import lombok.Data;

import java.util.List;
@Data
public class PassengerListResponse {

    private String packageName;
    private int passengerCapacity;
    private int enrolledPassengersCount;
    private List<Passenger> passengers;

    public PassengerListResponse(String packageName, int passengerCapacity, int enrolledPassengersCount, List<Passenger> passengers) {
        this.packageName = packageName;
        this.passengerCapacity = passengerCapacity;
        this.enrolledPassengersCount = enrolledPassengersCount;
        this.passengers = passengers;
    }

}
