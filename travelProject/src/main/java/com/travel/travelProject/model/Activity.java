package com.travel.travelProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Document(collection = "activities")
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    @Id
    private String id;
    private String name;
    private String description;
    private double cost;
    private int capacity;
    private String destinationName;

    public Activity(String name, String description, double cost, int capacity, String destinations) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.capacity = capacity;
        this.destinationName = destinations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Double.compare(activity.cost, cost) == 0 &&
                capacity == activity.capacity &&
                Objects.equals(id, activity.id) &&
                Objects.equals(name, activity.name) &&
                Objects.equals(description, activity.description) &&
                Objects.equals(destinationName, activity.destinationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, cost, capacity, destinationName);
    }

    public void setDestinations(String destinations) {
        this.destinationName = destinations;
    }

}