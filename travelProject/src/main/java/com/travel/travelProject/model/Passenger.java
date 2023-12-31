package com.travel.travelProject.model;

import com.travel.travelProject.Exception.TravelException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Data
@Document(collection = "passengers")
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    @Id
    private String id;
    private String name;
    private String passengerNumber;
    private PassengerType type;
    private double balance;
    private List<Activity> signedUpActivities;

    public Passenger(String name, String passengerNumber, PassengerType type, double balance) {
        this.name = name;
        this.passengerNumber = passengerNumber;
        this.type = type;
        this.balance = balance;
        this.signedUpActivities = new ArrayList<>();
    }

    // Method to add signed-up activity
    public void addSignedUpActivity(Activity activity) {
        double activityCost = activity.getCost();

        switch (type) {
            case STANDARD:
                if (balance >= activityCost) {
                    balance -= activityCost;
                } else {
                    throw new TravelException("Insufficient balance for the standard passenger");
                }
                break;

            case GOLD:
                // Calculate the discounted price

                double discountAmount = activityCost * (10 / 100.0);
                double discountedCost = activityCost - discountAmount;
                if (balance >= discountedCost) {
                    balance -= discountedCost;
                } else {
                    throw new TravelException("Insufficient balance for the gold passenger");
                }
                break;

            case PREMIUM:
                // Premium passengers sign up for activities for free
                break;

            default:
                throw new TravelException("Invalid passenger type");
        }
        signedUpActivities.add(activity);
    }



public enum PassengerType {
        STANDARD,
        GOLD,
        PREMIUM
    }
}
