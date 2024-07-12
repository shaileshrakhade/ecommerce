package com.micro.notification.config;

import brave.Tracer;
import com.micro.notification.constant.AppConstant;
import com.micro.notification.dto.OrderPlacedEvent;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

import java.util.IllegalFormatException;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaListeners {

    private final ObservationRegistry observationRegistry;
    private final Tracer tracer;

    @KafkaListener(topics = AppConstant.ORDER_NOTIFICATION_TOPIC,groupId = "user-group")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) throws IllegalFormatException {
        Observation.createNotStarted("on-message", this.observationRegistry).observe(() -> {
            log.info("Notification Order Number :: {}", orderPlacedEvent.getOrderNumber());
            log.info("TraceId- {}, Received Notification for Order - {}", this.tracer.currentSpan().context().traceId(),
                    orderPlacedEvent.getOrderNumber());
        });
        // send out an email notification
    }
}
