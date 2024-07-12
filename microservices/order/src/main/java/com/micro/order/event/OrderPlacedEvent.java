package com.micro.order.event;

import lombok.*;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import java.util.Map;

@Setter
@Getter
public class OrderPlacedEvent{
    private String orderNumber;

    public OrderPlacedEvent(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
