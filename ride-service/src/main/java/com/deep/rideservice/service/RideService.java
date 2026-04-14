package com.deep.rideservice.service;

import com.deep.rideservice.dto.RideRequest;
import com.deep.rideservice.dto.RideResponse;
import com.deep.rideservice.event.RideRequestEvent;
import com.deep.rideservice.model.Ride;
import com.deep.rideservice.model.RideStatus;
import com.deep.rideservice.repo.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestEvent> kafkaTemplate;

    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";

//    Create ride in DB with requested status
    public RideResponse requestRide(RideRequest rideRequest){
        log.info("New ride request from rider: {}", rideRequest.getRiderId());

        // Step 1: Save ride to DB
        Ride ride = new Ride();
        ride.setRiderId(rideRequest.getRiderId());
        ride.setPickupLatitude(rideRequest.getPickupLatitude());
        ride.setPickupLongitude(rideRequest.getPickupLongitude());
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setDropAddress(rideRequest.getDropAddress());
        ride.setDropLatitude(rideRequest.getDropLatitude());
        ride.setDropLongitude(rideRequest.getDropLongitude());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimateFare(rideRequest));

        Ride savedRide = rideRepository.save(ride);


        // Step 2: Publish event to Kafka
        // Matching serving will fetch and find nearest rider


    }


}
