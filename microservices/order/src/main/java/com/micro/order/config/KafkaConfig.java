package com.micro.order.config;

import com.micro.order.constant.AppConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    //create order-notification topic in kafka at compile time.
//    @Bean
//    public NewTopic topic() {
//        return TopicBuilder
//                .name(AppConstant.ORDER_NOTIFICATION)
//                .build();
//    }
}
