package com.deep.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
Event published to Kafka when ride is requested
Matching request consumes this event
Topic: ride.requested
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestEvent {

    private String rideId;
    private String riderId;

    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;

}
