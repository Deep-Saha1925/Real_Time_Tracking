package com.deep.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

//    Topic where ride service publishes ride request
//    Matching services subscribe to this topic

    @Bean
    public NewTopic rideRequestedTopic(){
        return TopicBuilder.name("ride.requetted")
                .partitions(3)
                .replicas(1)
                .build();
    }


    // Topic where matching service published match results
    // Ride service subscribe to this topic
}
