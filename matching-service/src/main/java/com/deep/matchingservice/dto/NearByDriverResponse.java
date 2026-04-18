package com.deep.matchingservice.dto;

/**
 * Response received from location service
 * When querying for nearby drivers
 */

public class NearByDriverResponse {

    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
