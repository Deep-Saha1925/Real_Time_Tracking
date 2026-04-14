package com.deep.rideservice.controller;

import com.deep.rideservice.dto.RideRequest;
import com.deep.rideservice.dto.RideResponse;
import com.deep.rideservice.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    /*
        Rider request ride
     */

    @PostMapping("/request")
    public ResponseEntity<RideResponse> requestRide(
            @Valid
            @RequestBody RideRequest rideRequest
            ){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rideService.requestRide(rideRequest));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String rideId){
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    // Get rides for rider
    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponse>> getRidesByRiderId(
            @PathVariable String riderId
    ){
        return ResponseEntity.ok(rideService.getRidesByRiderId(riderId));
    }


    //Driver starts ride
    @PostMapping("/{rideId}/start")
    public ResponseEntity<RideResponse> startRide(
            @PathVariable String rideId
    ){
        return ResponseEntity.ok(rideService.startRide(rideId));
    }


    //complete ride
    @PostMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(
            @PathVariable String rideId
    ){
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    //cancel ride
    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable String rideId
    ){
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }

}
