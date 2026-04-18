package com.deep.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

//    Topic where ride service publishes ride request
//    Matching services subscribe to this topic
    public NewTopic rideRequestedTopic(){
        
    }

}
