package com.deep.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published to Kafka topic: ride.matched
 * Consumed by ride service to update ride to assigned driver
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMatchedEvent {

    private String rideId;
    private String riderId;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private double distanceToPickupKm;

}
