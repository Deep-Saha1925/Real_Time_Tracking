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

import java.time.LocalDateTime;

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

        RideRequestEvent event = new RideRequestEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getPickupAddress(),
                savedRide.getPickupLatitude(),
                savedRide.getDropLongitude(),
                savedRide.getDropAddress()
        );

        kafkaTemplate.send(RIDE_REQUESTED_TOPIC, savedRide.getId(), event);
        log.info("Ride request published to kafka for ride: {}", savedRide.getId());

        // Update status to matching
        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);

        return mapToResponse(savedRide);
    }

    // Will be called by the matching service to change status
    public void updateRideWithDriver(String rideId, String driverId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    public RideResponse startRide(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if(ride.getStatus() != RideStatus.ACCEPTED){
            throw new RuntimeException("Ride cannot be started!! Current status: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);

        return mapToResponse(ride);
    }

    private RideResponse mapToResponse(Ride ride){
        return new RideResponse(
                ride.getId(),
                ride.getRiderId(),
                ride.getDriverId(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getPickupAddress(),
                ride.getPickupLatitude(),
                ride.getDropLongitude(),
                ride.getDropAddress(),
                ride.getStatus(),
                ride.getEstimatedFare(),
                ride.getActualFare(),
                ride.getCreatedAt(),
                ride.getCompletedAt(),
                ride.getStartedAt(),
                ride.getCompletedAt()
        );
    }

}
