package com.travel.travelProject.config;

//import com.travel.travelProject.controller.TravelController;
import com.travel.travelProject.model.TravelPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.travel.travelProject.model")
public class TravelConfig {

    @Bean
    public TravelPackage travelPackage() {
        return new TravelPackage("YourPackageName", 50); 
    }

}
