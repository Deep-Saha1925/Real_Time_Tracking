package com.deep.rideservice.model;


/**
 *
 * REQUEST --> MATCHING --> ACCEPT --> DRIVER_ARRIVING
 *          ---> RIDE STARTED --> COMPLETED
 *          --> CANCELLED
 */
public enum RideStatus {

    REQUESTED,
    MATCHING,
    ACCEPTED,
    DRIVER_ARRIVING,
    RIDE_STARTED,
    COMPLETED,
    CANCELLED
}
