package com.travel.travelProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "destinations")
@NoArgsConstructor
@AllArgsConstructor
public class Destination {
    @Id
    private String id;
    private String name;
    @DBRef(lazy = true)
    private List<Activity> activities;
    private String travelPackageID;


    public Destination(String name) {
        this.name = name;
        this.activities = new ArrayList<>();
    }

    public void addActivities(List<Activity> activityList) {
        this.activities.addAll(activityList);
    }
}



