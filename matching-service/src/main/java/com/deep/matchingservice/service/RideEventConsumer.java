package com.deep.matchingservice.service;

import com.deep.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideEventConsumer {

    private final MatchingService matchingService;

    /**
     * Listens to ride.requested KAFKA topic
     * Triggers every time ride service publishes new ride request
     */

    @KafkaListener(topics = "ride.requested", groupId = "matching-service-group")
    public void consumeRideRequestedEvent(RideRequestedEvent event){
        try{
            matchingService.matchDriverForRide(event);


        }catch (Exception e){
            log.error("Error processing ride request {} - {}", event.getRideId(), e.getMessage());
        }
    }
}
