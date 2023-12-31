package com.travel.travelProject.service;

import com.travel.travelProject.model.Activity;
import com.travel.travelProject.model.Destination;
import com.travel.travelProject.model.Passenger;
import com.travel.travelProject.model.TravelPackage;
import com.travel.travelProject.repository.ActivityRepository;
import com.travel.travelProject.repository.DestinationRepository;
import com.travel.travelProject.repository.PassengerRepository;
import com.travel.travelProject.repository.TravelPackageRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class TravelPackageServiceTest {

    @Mock
    PassengerRepository passengerRepository;
    @Mock
    private TravelPackageRepository travelPackageRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private TravelPackageService travelPackageService;

    @Test
    public void testGetTravelPackageById() {
        // Mocking behavior for the repository
        String travelPackageId = "123";
        TravelPackage mockTravelPackage = new TravelPackage();
        Mockito.when(travelPackageRepository.findById(travelPackageId)).thenReturn(Optional.of(mockTravelPackage));

        // Perform the actual method invocation
        TravelPackage result = travelPackageService.getTravelPackageById(travelPackageId);

        // Verify the result
        assertNotNull(result);
        assertEquals(mockTravelPackage, result);
    }

    @Test
    public void testGetPassengersForTravelPackage() {
        // Mock data
        String travelPackageId = "123";
        List<Passenger> mockPassengers = Arrays.asList(
                new Passenger("John Doe", "P123", Passenger.PassengerType.STANDARD, 100.0),
                new Passenger("Jane Smith", "P456", Passenger.PassengerType.GOLD, 200.0)
        );
        TravelPackage mockTravelPackage = new TravelPackage();
        mockTravelPackage.setPassengers(mockPassengers);

        // Mock the behavior of the repository and service methods
        Mockito.when(travelPackageRepository.findById(travelPackageId)).thenReturn(Optional.of(mockTravelPackage));

        // Perform the actual method invocation
        List<Passenger> result = travelPackageService.getPassengersForTravelPackage(travelPackageId);

        // Verify the result
        assertNotNull(result);
        assertEquals(mockPassengers, result);
    }

    @Test
    public void testGetTravelPackageByName() {
        // Mock data
        String packageName = "TestPackage";
        TravelPackage mockTravelPackage = new TravelPackage();

        // Mock the behavior of the repository and service methods
        Mockito.when(travelPackageRepository.findByName(packageName)).thenReturn(Optional.of(mockTravelPackage));

        // Perform the actual method invocation
        Optional<TravelPackage> result = travelPackageService.getTravelPackageByName(packageName);

        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockTravelPackage, result.get());
    }

    @Test
    public void testGetPassengerById() {
        // Mock data
        String passengerId = "123";
        Passenger mockPassenger = new Passenger();
        mockPassenger.setId(passengerId);

        // Mock the behavior of the repository method
        Mockito.when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(mockPassenger));

        // Call the service method
        Passenger result = travelPackageService.getPassengerById(passengerId);

        // Verify the result
        assertEquals(mockPassenger, result);
    }

    @Test
    public void testCreateTravelPackage() {
        // Arrange
        String packageName = "TestPackage";
        int passengerCapacity = 10;

        // Mock the repository behavior
        Mockito.when(travelPackageRepository.existsByName(packageName)).thenReturn(false);
        Mockito.when(travelPackageRepository.save(any())).thenAnswer(invocation -> {
            TravelPackage createdPackage = invocation.getArgument(0);
            createdPackage.setId("123"); // Set an ID for the created package
            return createdPackage;
        });

        // Act
        TravelPackage createdPackage = travelPackageService.createTravelPackage(packageName, passengerCapacity);

        // Assert
        assertNotNull(createdPackage);
        assertEquals(packageName, createdPackage.getName());
        assertEquals(passengerCapacity, createdPackage.getPassengerCapacity());
        assertNotNull(createdPackage.getId()); // ID should be set after saving

        // Verify that repository methods were called
        verify(travelPackageRepository, times(1)).existsByName(packageName);
        verify(travelPackageRepository, times(1)).save(any());
    }

    @Test
    public void testCreateTravelPackageWhenPackageExists() {
        // Arrange
        String packageName = "ExistingPackage";
        int passengerCapacity = 5;

        // Mock the repository behavior
        Mockito.when(travelPackageRepository.existsByName(packageName)).thenReturn(true);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            travelPackageService.createTravelPackage(packageName, passengerCapacity);
        });

        // Verify that repository methods were called
        verify(travelPackageRepository, times(1)).existsByName(packageName);
        verify(travelPackageRepository, never()).save(any());
    }

    @Test
    public void testCreatePassenger() {
        // Arrange
        String name = "John Doe";
        String passengerNumber = "P123";
        Passenger.PassengerType type = Passenger.PassengerType.STANDARD;
        double balance = 100.0;

        // Create a passenger instance with the provided parameters
        Passenger expectedPassenger = new Passenger(name, passengerNumber, type, balance);

        // Mock the behavior of the repository's save method
        Mockito.when(passengerRepository.save(any(Passenger.class))).thenReturn(expectedPassenger);

        // Act
        Passenger createdPassenger = travelPackageService.createPassenger(name, passengerNumber, type, balance);

        // Assert
        assertNotNull(createdPassenger);
        assertEquals(expectedPassenger, createdPassenger);

        // Verify that the save method was called on passengerRepository
        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    public void testCreateDestination() {
        // Arrange
        String destinationName = "New Destination";

        // Create a destination instance with the provided name
        Destination expectedDestination = new Destination(destinationName);

        // Mock the behavior of the repository's save method
        Mockito.when(destinationRepository.save(any(Destination.class))).thenReturn(expectedDestination);

        // Act
        Destination createdDestination = travelPackageService.createDestination(destinationName);

        // Assert
        assertNotNull(createdDestination);
        assertEquals(expectedDestination, createdDestination);

        // Verify that the save method was called on destinationRepository
        verify(destinationRepository, times(1)).save(any(Destination.class));
    }


    @Test
    public void testCreateActivity() {
        // Arrange
        String activityName = "New Activity";
        String activityDescription = "Description of the activity";
        double activityCost = 50.0;
        int activityCapacity = 100;
        String activityDestinations = "Destination1, Destination2";

        // Create an activity instance with the provided data
        Activity expectedActivity = new Activity(activityName, activityDescription, activityCost, activityCapacity, activityDestinations);

        // Mock the behavior of the repository's save method
        Mockito.when(activityRepository.save(any(Activity.class))).thenReturn(expectedActivity);

        // Act
        Activity createdActivity = travelPackageService.createActivity(activityName, activityDescription, activityCost, activityCapacity, activityDestinations);

        // Assert
        assertNotNull(createdActivity);
        assertEquals(expectedActivity, createdActivity);

        // Verify that the save method was called on activityRepository
        verify(activityRepository, times(1)).save(any(Activity.class));
    }


    @Test
    public void testGetTravelPackageWithPassengerDetails() {
        // Arrange
        String travelPackageId = "sampleId";
        TravelPackage expectedTravelPackage = new TravelPackage();
        expectedTravelPackage.setId(travelPackageId);

        // Mock the behavior of the repository's findById method
        Mockito.when(travelPackageRepository.findById(travelPackageId)).thenReturn(Optional.of(expectedTravelPackage));

        // Act
        TravelPackage result = travelPackageService.getTravelPackageWithPassengerDetails(travelPackageId);

        // Assert
        assertEquals(expectedTravelPackage, result);

        // Verify that the findById method was called on travelPackageRepository
        verify(travelPackageRepository, times(1)).findById(travelPackageId);
    }

}
