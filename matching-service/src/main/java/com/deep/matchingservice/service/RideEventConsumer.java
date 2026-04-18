package com.deep.matchingservice.service;

import com.deep.matchingservice.client.LocationServiceClient;
import com.deep.matchingservice.dto.NearByDriverResponse;
import com.deep.matchingservice.event.RideMatchedEvent;
import com.deep.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Find the best driver to MATCH for the ride.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;

    /**
     * Main Matching algorithm
     * Called when ride request is consumed
     * @param event
     *
     * STEP:
     * 1. Ask location for nearby drivers
     * 2. Score each driver and pick the best one
     * 3. Publish ride match event to KAFKA
     */
    public void matchDriverForRide(RideRequestedEvent event){

        List<NearByDriverResponse> nearByDrivers = locationServiceClient.getNearbyDrivers(
                event.getPickupLatitude(),
                event.getPickupLongitude(),
                DEFAULT_SEARCH_RADIUS_KM
        );

        if(nearByDrivers.isEmpty()){
            log.warn("No drivers found near ride: {}", event.getRideId());
            return;
        }


        // STEP 2
        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearByDrivers);

        if(bestDriver.isEmpty()){
            log.warn("could not find suitable ride: {}", event.getRideId());
            return;
        }

        NearByDriverResponse assignDriver = bestDriver.get();

        // STEP 3
        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignDriver.getDriverId(),
                assignDriver.getLatitude(),
                assignDriver.getLongitude(),
                assignDriver.getDistanceInKm()
        );

        kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), matchedEvent);
        log.info("Ride matched event published");

    }


    /**
     * Diver Scoring algorithm
     *
     * Distance: 70%
     * Rating: 30%
     *
     * Score = (1/Distance) * distanceWeight + rating * ratingWeight
     *
     * @param drivers
     * @return
     */
    private Optional<NearByDriverResponse> findBestDriver(List<NearByDriverResponse> drivers){

        double distanceWt = 0.7;
        double ratingWt = 0.3;

        return drivers.stream()
                .max(Comparator.comparingDouble(driver -> {
                    // Distance closer -> closer = high score
                    // 0.1 to avoid div by 0
                    double distanceScore = 1.0/(driver.getDistanceInKm() * 0.1);

                    // rating between 4.0 and 5.0 (for now)
                    double simulatedRating = 4.0 + Math.random();

                    // Final weighted score
                    return (distanceScore * distanceWt) + (simulatedRating * ratingWt);
                }));

    }
}
