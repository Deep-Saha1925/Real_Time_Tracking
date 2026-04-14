package com.deep.rideservice.repo;

import com.deep.rideservice.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {
}
