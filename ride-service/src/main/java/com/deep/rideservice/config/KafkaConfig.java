package com.deep.rideservice.config;

import com.deep.rideservice.event.RideRequestEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

//    Topic where ride service publishes ride request
//    Matching services subscribe to this topic

    @Bean
    public NewTopic rideRequestedTopic(){
        return TopicBuilder.name("ride.requested")
                .partitions(3)
                .replicas(1)
                .build();
    }


    // Topic where matching service published match results
    // Ride service subscribe to this topic

    @Bean
    public NewTopic rideMatchTopic(){
        return TopicBuilder.name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
