package com.deep.locationservice.service;

import com.deep.locationservice.dto.DriverLocationRequest;
import com.deep.locationservice.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;

//    Redis Key for driver location
    private static final String DRIVER_GEO_KEY = "drivers:locations";

    /**
     *
     * @param driverLocationRequest
     */
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {

        log.info("Updating driver location: {}", driverLocationRequest.getDriverId());

//        Longitude first, then latitude
        Point driverPoint = new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );

        redisTemplate.opsForGeo().add(
                DRIVER_GEO_KEY,
                driverPoint,
                driverLocationRequest.getDriverId()
        );

        log.info("Location updated for driver: {}", driverLocationRequest.getDriverId());
    }

    /**
     * Find nearby drivers
     * Match request
     * Maps to Redis GEORADIUS command
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    public List<NearByDriverResponse> findNearByDrivers(double latitude, double longitude, double radius) {

        log.info("Finding drivers near lat: {}, long: {} withing {}Km", latitude, longitude, radius);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radius, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        DRIVER_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(10);
                );

        List<NearByDriverResponse> nearByDrivers = new ArrayList<>();
        if(results != null){
            results.getContent().forEach(result -> {
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                nearByDrivers.add(new NearByDriverResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getDistance().getValue()
                ));
            });
        }

        log.info("Found {} drivers nearby", nearByDrivers.size());
    }

    public void removeDriverByDriverId(String driverId) {

    }

    /**
     * Update driver location call every 3 sec
     * Maps GEOADD command
     */


}
